package com.zgg.common.netty.protocol.file;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * tcp文件下载的编码器
 */
public class FileDownloadEncoder extends MessageToByteEncoder<FileDownloadProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, FileDownloadProtocol msg, ByteBuf out) throws Exception {
        // TODO STH
    }
}
