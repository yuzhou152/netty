package com.tceasy.cavphub.test.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.ActionEnum;
import com.zgg.common.enums.TcpClientEnum;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import com.zgg.common.util.JsonUtil;

/**
 * 生成客户端各个业务模拟的消息体
 *
 * @author <a href="mailto:wendong.sun@tingjiandan.com">wendong.sun</a>
 * @create 2019/12/28 15:34
 */
public class ClientMessageUtils {
    /**
     * 模拟生成客户端登录的消息体
     *
     * @param clientType 客户端类型
     * @param id         客户端ID
     * @return
     */
    public static MessageSendProtocol getLoginMessage(TcpClientEnum clientType, String id) {
        Map<String, String> msg = new HashMap<>();
        msg.put("clientType", clientType.name());
        switch (clientType) {
            case OEM:
                msg.put("vinCode", id);
                break;
            case BARRIER:
                msg.put("deviceId", id);
                break;
            case TERMINAL:
                msg.put("deviceId", id);
                break;
            case VEHICLE:
                msg.put("vinCode", id);
                break;
        }
        char action = ActionEnum.LOGIN.getAction();
        return new MessageSendProtocol(action, TcpConstant.TCP_REPLY_SIGN_ACTION, TcpConstant.TCP_ENCRYPTION_NON, JsonUtil
            .objectToJson(msg).getBytes());

    }

}
