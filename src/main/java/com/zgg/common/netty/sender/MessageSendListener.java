package com.zgg.common.netty.sender;

import com.zgg.common.netty.protocol.message.MessageReceiveProtocol;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import com.zgg.common.netty.config.SendResult.SendResultStatus;

/**
 * 发送结果监听器 TcpSendHandler 中触发
 */
public interface MessageSendListener {
    void complete(MessageSendProtocol sendProtocol, MessageReceiveProtocol receiveProtocol);

    void fail(MessageSendProtocol sendProtocol, SendResultStatus reason, Throwable throwable);
}
