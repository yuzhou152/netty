package com.zgg.request.controller.tcp;

import java.util.Map;

import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.ActionEnum;
import com.zgg.common.enums.CodeEC;
import com.zgg.common.enums.TcpClientEnum;
import com.zgg.common.enums.TcpReturnEnum;
import com.zgg.common.json.JsonResult;
import com.zgg.common.netty.annotation.Body;
import com.zgg.common.netty.annotation.Param;
import com.zgg.common.netty.annotation.TcpAttr;
import com.zgg.common.netty.annotation.tcp.TcpController;
import com.zgg.common.netty.annotation.tcp.TcpMapping;
import com.zgg.common.netty.protocol.message.MessageReceiveProtocol;
import com.zgg.common.netty.sender.MessageSendHandler;
import com.zgg.request.service.tcp.LoginService;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * tcp客户端登录
 */
@TcpController
@Slf4j
public class LoginController {
    @Autowired
    private MessageSendHandler sendHandler;

    /**
     * 客户端登录接口
     *
     * @param body
     * @param channel
     * @return
     */
    @TcpMapping(action = ActionEnum.LOGIN)
    public JsonResult login(@Body Map body, Channel channel) {
        TcpClientEnum clientType = TcpClientEnum.valueOf(String.valueOf(body.get("clientType")).toUpperCase());
        String id = String.valueOf(body.get("id"));
        if (clientType == null || StringUtils.isEmpty(id)) {
            log.info("登入错误，client type 为空:{}", body.get("clientType"));
            return JsonResult.FAIL(CodeEC.CLIENT_TYPE_UNKNOW);
        }
        channel.attr(AttributeKey.valueOf(TcpConstant.ATTRIBUTE_KEY_CLIENT_TYPE)).set(clientType);
        sendHandler.addChannel(clientType, id, channel);
        return JsonResult.SUCCESS(CodeEC.LOGIN_SUCESS);
    }

    @TcpMapping(action = ActionEnum.TX0001)
    public JsonResult gettingMsg(Channel channel, MessageReceiveProtocol protocol, @Body Map<String, String> body,
        @Param("clientType")String clientTypeParam, @TcpAttr(TcpConstant.ATTRIBUTE_KEY_CLIENT_TYPE) String clientTypeAttr) {

        String bodyStr = body.toString();
        Object type = channel.attr(AttributeKey.valueOf(TcpConstant.ATTRIBUTE_KEY_CLIENT_TYPE)).get();
        return JsonResult.SUCCESS(body);
    }
    @TcpMapping(action = ActionEnum.TX0002, immediatelyReturn= TcpReturnEnum.RIGHT_NOW)
    public void gettingMsgWithoutReturn(@Body Map<String, Integer> body, Channel channel) {
        String bodyStr = body.toString();

    }
}
