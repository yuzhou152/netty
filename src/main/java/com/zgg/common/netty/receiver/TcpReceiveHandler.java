package com.zgg.common.netty.receiver;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.ActionEnum;
import com.zgg.common.enums.CodeEC;
import com.zgg.common.enums.TcpReturnEnum;
import com.zgg.common.json.JsonResult;
import com.zgg.common.netty.annotation.Body;
import com.zgg.common.netty.annotation.Param;
import com.zgg.common.netty.annotation.TcpAttr;
import com.zgg.common.netty.annotation.tcp.TcpMapping;
import com.zgg.common.netty.config.TcpActionMappings;
import com.zgg.common.netty.protocol.message.MessageReceiveProtocol;
import com.zgg.common.netty.sender.MessageSendHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 接收消息后处理的Handler，根据业务包找到并使用反射调用对应的TcpController，同时根据反射时的参数类型和使用自定义注解注入需要的参数
 */
@Component
@Slf4j
public class TcpReceiveHandler {
    private ObjectMapper objectMapper = getObjectMapper();
    /**
     * 被缓存到进程内存中的所有的TcpController的映射
     */
    @Autowired
    private TcpActionMappings mappings;
    @Autowired
    private MessageSendHandler sendHandler;

    public void handle(ChannelHandlerContext ctx, MessageReceiveProtocol msg) throws IOException {
        //当前业务包是对方发送过来一个应答包，用来响应之前发送给对方的指令的，这里直接使用sendHandler进行应答就好
            if (msg.getAnswer() == TcpConstant.TCP_REPLY_SIGN_ANSWER) {
            sendHandler.answer(msg);
            return;
        }
        ActionEnum actionEnum = ActionEnum.of(msg.getAction());
        TcpActionMappings.Invocker invocker = mappings.getInvocker(actionEnum);
        if (invocker == null) {
            log.error("Action不存在: action={}", msg.getHexAction());
            ctx.write(msg.answer(objectMapper.writeValueAsBytes(JsonResult.FAIL(CodeEC.ACTION_NOT_FOUND))));
            return;
        }
        TcpMapping tcpMapping = invocker.getMethod().getAnnotation(TcpMapping.class);
        if (tcpMapping.immediatelyReturn() != TcpReturnEnum.NON && tcpMapping.immediatelyReturn().getValue() != null) {
            ctx.channel().writeAndFlush(msg.answer(objectMapper.writeValueAsBytes(tcpMapping.immediatelyReturn().getValue())));
        }
        Object[] params = new Object[invocker.getParams() == null ? 0 : invocker.getParams().length];
        JsonNode root = objectMapper.readTree(msg.getContent());
        for (int i = 0; i < params.length; i++) {
            if (invocker.getMethod().getParameterTypes()[i].isAssignableFrom(ctx.getClass())) {
                params[i] = ctx;
                continue;
            }
            if (invocker.getMethod().getParameterTypes()[i].isAssignableFrom(ctx.channel().getClass())) {
                params[i] = ctx.channel();
                continue;
            }
            if (MessageReceiveProtocol.class.isAssignableFrom(invocker.getMethod().getParameterTypes()[i])) {
                params[i] = msg;
                continue;
            }
            Annotation annotation = invocker.getParams()[i];
            if (annotation instanceof TcpAttr) {
                Object attr = ctx.channel().attr(AttributeKey.valueOf(((TcpAttr) annotation).value())).get();
                if (attr == null) {
                    log.warn("Channel attribute 不存在：{}", annotation);
                } else if (invocker.getMethod().getParameterTypes()[i].isAssignableFrom(attr.getClass())) {
                    params[i] = attr;
                } else {
                    log.warn("类型错误：{},from {} to {}", annotation, attr.getClass().getName(), invocker.getMethod().getParameterTypes()[i].getName());
                }
                continue;
            }
            if (annotation instanceof Param) {
                JsonNode node = root.findValue(((Param) annotation).value());
                if (node != null) {
                    params[i] = readJsonValue(node.toString(), invocker.getMethod().getParameters()[i]);
                }
                continue;
            }
            if (annotation instanceof Body) {
                params[i] = readJsonValue(new String(msg.getContent()), invocker.getMethod().getParameters()[i]);
                continue;
            }
        }
        try {
            Object returnValue;
            try {
                // 执行调用的方法
                returnValue = invocker.getMethod().invoke(invocker.getBean(), params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw e;
            }
            // 返回给请求方
            if (invocker.getMethod().getReturnType().equals(Void.TYPE) || returnValue == null) {
                ctx.channel().writeAndFlush(msg.answer(new byte[0]));
            } else {
                ctx.channel().writeAndFlush(msg.answer(objectMapper.writeValueAsBytes(returnValue)));
            }
        } catch (Throwable e) {
            log.error("TCP未捕获的异常：", e);
            ctx.channel().writeAndFlush(msg.answer(objectMapper.writeValueAsBytes(JsonResult.FAIL(CodeEC.SYSTEM_ERROR))));
        }

    }

    private Object readJsonValue(String content, Parameter parameter) {
        try {
            if (List.class.isAssignableFrom(parameter.getType())) {
                Type[] genericTypes = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments();
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Class.forName(genericTypes[0].getTypeName()));
                return objectMapper.readValue(content, javaType);
            }
            return objectMapper.readValue(content, parameter.getType());
        } catch (JsonProcessingException e) {
            log.error("json读取异常：{},{}", parameter.getName(), content, e);
        } catch (IOException e) {
            log.error("json读取异常：{},{}", parameter.getName(), content, e);
        } catch (ClassNotFoundException e) {
            log.error("json读取异常：{},{}", parameter.getName(), content, e);
        }
        return null;
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return objectMapper;
    }
}
