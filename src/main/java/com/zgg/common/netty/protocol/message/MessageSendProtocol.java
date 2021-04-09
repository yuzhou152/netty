package com.zgg.common.netty.protocol.message;

import com.zgg.common.enums.AnswerEnum;
import com.zgg.common.enums.EncryptionEnum;
import com.zgg.common.util.ByteUtils;
import com.zgg.common.util.IdBuilder;
import lombok.RequiredArgsConstructor;

/**
 * tcp发送消息的协议体
 */
@RequiredArgsConstructor
public class MessageSendProtocol extends DefaultProtocol {
    /**
     * 是否已经加密过
     */
    private boolean encrypted = false;
    private Byte checkCodeSum = null;

    /**
     * requestId自动生成
     */
    public MessageSendProtocol(Character action, Byte answer, Byte encryption, byte[] content) {
        this(action, answer, encryption, IdBuilder.getID(), content);
    }

    public MessageSendProtocol(Character action, Byte answer, Byte encryption, String requestId, byte[] content) {
        super(action, answer, encryption, requestId, content);
    }

    /**
     * 快捷初始化Tcp协议体
     */
    public static MessageSendProtocol direct(Character action, byte[] content) {
        return new MessageSendProtocol(action, AnswerEnum.DIRECT.getValue(), EncryptionEnum.NON.getValue(), content);
    }

    public void encrypt() {
        if (encrypted) {
            return;
        }
        this.checkCodeSum = null;
        // TODO 对content加密
    }

    public String getHexCheckCode() {
        return String.format("0x%02x", getCheckCode());
    }

    /**
     * 生成数据单元的校验码，采用BCC（异或校验）法：<br/>
     * 校验范围从命令单元的第一个字节开始，同后一字节异或，直到校验码前一字节为止，校验码占用一个字节，当数据单元存在加密时，应先加密后校验，先校验后解密
     *
     * @return
     */
    public byte getCheckCode() {
        if (checkCodeSum != null) {
            return checkCodeSum.byteValue();
        }
        byte[] actionBytes = ByteUtils.charToBytes(getAction());
        byte[] lengthBytes = ByteUtils.intToBytes(getContent().length);
        byte[] requestIdBytes = getRequestId().getBytes();
        byte code = (byte) (actionBytes[0] ^ actionBytes[1] ^ getAnswer() ^ getEncryption() ^ lengthBytes[0] ^ lengthBytes[1] ^ lengthBytes[2] ^ lengthBytes[3]);
        for (int i = 1; i < requestIdBytes.length; i++) {
            code = (byte) (code ^ requestIdBytes[i]);
        }
        for (int i = 1; i < getContent().length; i++) {
            code = (byte) (code ^ getContent()[i]);
        }
        this.checkCodeSum = new Byte(code);
        return code;
    }

    @Override
    public String toString() {
        return String.format("MessageSendProtocol{" +
                        "action=%s" +
                        ", answer=%s" +
                        ", encryption=%s" +
                        ", requestId=%s" +
                        ", contentLength=%d" +
                        ", content=%s" +
                        ", checkCode=%s" +
                        '}',
                getHexAction(), getHexAnswer(), getHexEncryption(), getRequestId(), getContent().length, new String(getContent()), getHexCheckCode());
    }
}
