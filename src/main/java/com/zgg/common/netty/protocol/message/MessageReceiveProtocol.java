package com.zgg.common.netty.protocol.message;

import com.zgg.common.enums.ActionEnum;
import com.zgg.common.enums.AnswerEnum;
import com.zgg.common.enums.EncryptionEnum;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * tcp接收数据的协议体
 */
@Data
@RequiredArgsConstructor
public class MessageReceiveProtocol extends DefaultProtocol {
    /**
     * 接收到的数据包中给定的数据单元长度
     */
    @NonNull
    private int contentLength;
    /**
     * 消息发送方的ip
     */
    @NonNull
    private String remoteHost;
    /**
     * 消息发送发的端口
     */
    @NonNull
    private int remotePort;
    /**
     * 接收到的数据包中给定的校验码
     */
    private byte checkCode;

    public MessageReceiveProtocol() {
    }

    public MessageReceiveProtocol(@NonNull char action, @NonNull byte answer, @NonNull byte encryption, @NonNull String requestId, @NonNull byte[] content, @NonNull int contentLength, @NonNull byte checkCode, @NonNull String remoteHost, @NonNull int remotePort) {
        super(action, answer, encryption, requestId, content);
        this.contentLength = contentLength;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.checkCode = checkCode;
    }

    public MessageSendProtocol answer(byte[] content) {
        return new MessageSendProtocol(getAction(), AnswerEnum.SUCCESS.getValue(), getEncryption(), getRequestId(), content);
    }

    public String getHexCheckCode() {
        return String.format("0x%02x", getCheckCode());
    }

    @Override
    public String toString() {
        return String.format("MessageReceiveProtocol{" +
                        "action=%s(%s)" +
                        ", answer=%s(%s)" +
                        ", encryption=%s(%s)" +
                        ", requestId=%s" +
                        ", contentLength=%d" +
                        ", content=%s" +
                        ", checkCode=%s" +
                        ", remoteHost=%s" +
                        ", remotePort=%d" +
                        '}',
                getHexAction(), ActionEnum.getText(this.getAction(), "未知命令"),
                getHexAnswer(), AnswerEnum.getText(this.getAnswer(), "未知标识"),
                getHexEncryption(), EncryptionEnum.getText(this.getEncryption(), "未知加密方式"),
                getRequestId(),
                this.contentLength,
                new String(getContent()),
                getHexCheckCode(),
                this.remoteHost,
                this.remotePort
        );
    }
}
