package com.zgg.common.netty.config;

import com.zgg.common.json.JsonResult;
import com.zgg.common.netty.protocol.message.MessageReceiveProtocol;
import com.zgg.common.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TcpSendHandler请求完成后返回此对象，用来校验请求是否成功
 */
@Data
@AllArgsConstructor
public class SendResult {
    private SendResultStatus status;
    private MessageReceiveProtocol receiveProtocol;

    public boolean isSuccess() {
        return SendResultStatus.success.equals(status);
    }

    /**
     * 解析receiveProtocol 并将content转为JsonResult
     */
    public JsonResult toJsonResult(){
        if (!isSuccess()) {
            throw new IllegalStateException("请求失败，status is : " + status.name());
        }
        byte[] rcontent = receiveProtocol.getContent();
        if (rcontent != null && rcontent.length != 0){
            // 将接口返回的JsonResult直接返回
            return JsonUtil.byteJsonToObject(rcontent, JsonResult.class);
        }
        return JsonResult.SUCCESS();
    }

    /**
     * 状态集
     */
    public enum SendResultStatus {
        exception, noChannel, channelNotActive, timeOut, stopped, success
    }

}
