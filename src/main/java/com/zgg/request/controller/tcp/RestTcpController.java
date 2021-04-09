package com.zgg.request.controller.tcp;

import java.util.HashMap;
import java.util.Map;

import com.zgg.common.action.BaseAction;
import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.ActionEnum;
import com.zgg.common.json.JsonResult;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import com.zgg.common.netty.sender.MessageSendHandler;
import com.zgg.common.netty.config.SendResult;


import com.zgg.common.netty.config.SendResult.SendResultStatus;
import com.zgg.common.util.JsonUtil;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试RPC 启动时客户端调用login接口注册到服务端后，两端都存了对方的channel 两端都能通过已注册的channel向对方发起调用
 */
@RestController
@RequestMapping("/restToRPC")
public class RestTcpController extends BaseAction {

    @Autowired
    private MessageSendHandler sendHandler;

    /**
     * 向已建立的channel发送消息 有返回值
     */
    @RequestMapping(value = "/sendWithoutReturn", method = RequestMethod.GET)
    public JsonResult sendWithoutReturn() throws Exception {
        Map<String, String> msg = new HashMap<>();
        msg.put("clientType", ActionEnum.TX0001.getClient().name());
        msg.put("id", "fdahudsf123ddfi98712");
        byte[] content = JsonUtil.objectToJsonByte(msg);
        char action = ActionEnum.TX0001.getAction();

        MessageSendProtocol protocol = new MessageSendProtocol(action, TcpConstant.TCP_REPLY_SIGN_ACTION, TcpConstant.TCP_ENCRYPTION_NON, content);
        SendResult result = sendHandler.send(ActionEnum.TX0001.getClient(), "fdahudsf123ddfi98712", protocol).get();

        return result.toJsonResult();
    }

    /**
     * 向已建立的channel发送消息 无返回值
     */
    @RequestMapping(value = "/sendWithReturn", method = RequestMethod.GET)
    public JsonResult sendWithReturn() throws Exception {
        Map<String, String> msg = new HashMap<>();
        msg.put("clientType", ActionEnum.TX0002.getClient().name());
        msg.put("id", "sdfjhrewygc4565gtuw3edc");
        byte[] content = JsonUtil.objectToJson(msg).getBytes();
        char action = ActionEnum.TX0002.getAction();

        MessageSendProtocol protocol = new MessageSendProtocol(action, TcpConstant.TCP_REPLY_SIGN_ACTION, TcpConstant.TCP_ENCRYPTION_NON, content);
        SendResult result = sendHandler.send(ActionEnum.TX0002.getClient(), "sdfjhrewygc4565gtuw3edc", protocol).get();

        return result.toJsonResult();
    }
}

