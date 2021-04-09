package com.zgg.common.netty.protocol.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * tcp协议的编码器，将消息发送协议编码成tcp的业务包
 * 发送消息时编码
 */
public class MessageEncoder extends MessageToByteEncoder<MessageSendProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageSendProtocol msg, ByteBuf out) {
        for (int h : msg.getHeader()) {
            out.writeByte(h);
        }
        msg.encrypt();
        out.writeChar(msg.getAction());
        out.writeByte(msg.getAnswer());
        out.writeByte(msg.getEncryption());
        out.writeInt(msg.getContent().length);
        out.writeBytes(msg.getRequestId().getBytes());
        out.writeBytes(msg.getContent());
        out.writeByte(msg.getCheckCode());
    }
}
