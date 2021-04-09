package com.zgg.common.netty.protocol.message;

import com.zgg.common.constant.TcpConstant;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * tcp的默认协议体
 */
@RequiredArgsConstructor
@Data
public class DefaultProtocol {
    /**
     * tcp协议的起始符
     */
    private byte[] header = TcpConstant.HEAD_DATA;
    /**
     * tcp协议的命令标识
     */
    @NonNull
    private char action;
    /**
     * tcp协议的应答标识
     */
    @NonNull
    private byte answer;
    /**
     * tcp协议的加密标识
     */
    @NonNull
    private byte encryption;
    /**
     * 每次请求的唯一标识
     */
    @NonNull
    private String requestId;
    /**
     * tcp协议的数据单元
     */
    @NonNull
    private byte[] content;

    public DefaultProtocol() {
    }

    public String getHexAction() {
        return String.format("0x%04x", (int) this.action);
    }

    public String getHexAnswer() {
        return String.format("0x%02x", this.answer);
    }

    public String getHexEncryption() {
        return String.format("0x%02x", this.encryption);
    }
}
