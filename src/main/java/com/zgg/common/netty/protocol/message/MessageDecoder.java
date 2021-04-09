package com.zgg.common.netty.protocol.message;

import java.net.InetSocketAddress;
import java.util.List;

import com.zgg.common.constant.TcpConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * tcp消息协议的解码器，负责将受到的数据包解码成接收协议，并处理拆包粘包问题
 * 接收消息时先解码
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {
    /**
     * tcp数据部分最小可读长度：2字节起始标志，2字节请求标志，1字节应答标志，1字节加密标志，4字节内容长度，32字节requestId
     * tcp报文段：            tcp首部 + tcp数据部分
     * 单个tcp报文大小：        20字节  +  42字节  =  62字节 = 496bit
     */
    private static final int BASE_LENGTH = 42;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> list) {
        if (buffer.readableBytes() < BASE_LENGTH) {
            return;
        }
        // 记录包头开始的index
        int beginReader;
        while (true) {
            // 获取包头开始的index
            beginReader = buffer.readerIndex();
            // 标记包头开始的index
            buffer.markReaderIndex();
            // 读到了协议的开始标志，结束while循环
            for (byte head : TcpConstant.HEAD_DATA) {
                byte h = buffer.readByte();
                log.debug("{}={}", String.format("0x%02x", h), String.format("0x%02x", head));
                if (h != head) {
                    // 未读到包头，略过一个字节
                    // 每次略过，一个字节，去读取，包头信息的开始标记
                    buffer.resetReaderIndex();
                    buffer.readByte();
                    // 当略过，一个字节之后，
                    // 数据包的长度，又变得不满足
                    // 此时，应该结束。等待后面的数据到达
                    if (buffer.readableBytes() < BASE_LENGTH - TcpConstant.HEAD_DATA.length) {
                        log.debug("数据包未到齐");
                        return;
                    }
                }
            }
            break;
        }
        char action = buffer.readChar();
        byte answer = buffer.readByte();
        byte encryption = buffer.readByte();
        int length = buffer.readInt();
        // 判断请求数据包数据是否到齐，+32是因为长度后有32的requestId，+1是因为最后一字节校验码
        if (buffer.readableBytes() < length + 32 + 1) {
            // 还原读指针
            buffer.readerIndex(beginReader);
            log.debug("数据包未到齐");
            return;
        }
        byte[] requestIdData = new byte[32];
        buffer.readBytes(requestIdData);
        String requestId = new String(requestIdData);
        // 读取data数据
        byte[] data = new byte[length];
        buffer.readBytes(data);
        byte checkCode = buffer.readByte();
        InetSocketAddress address = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        MessageReceiveProtocol protocol = new MessageReceiveProtocol(action, answer, encryption, requestId, data, length, checkCode, address.getAddress().getHostAddress(), address.getPort());
        list.add(protocol);

        //channelHandlerContext.channel().attr(AttributeKey.valueOf("channel_global_value1")).set("value");

        log.debug("解码内容。。。action={} , answer={} , encryption={} , length={} , requestId={} , content={} , checkCode={}",
            String.format("0x%04x", (int) action),
            String.format("0x%02x", answer),
            String.format("0x%02x", encryption),
            length,
            requestId,
            new String(data),
            String.format("0x%02x", checkCode)
        );
    }
}
