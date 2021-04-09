package com.zgg.common.enums;

import com.zgg.common.constant.TcpConstant;
import lombok.Getter;

/**
 * tcp的加密方式
 */
@Getter
public enum EncryptionEnum {
    NON(TcpConstant.TCP_ENCRYPTION_NON, "不加密"),
    AES128(TcpConstant.TCP_ENCRYPTION_AES128, "AES128"),
    RSA(TcpConstant.TCP_ENCRYPTION_RSA, "RSA");
    private byte value;
    private String text;

    EncryptionEnum(byte value, String text) {
        this.value = value;
        this.text = text;
    }

    public static EncryptionEnum of(byte value) {
        for (EncryptionEnum dot : EncryptionEnum.values()) {
            if (dot.value == value) {
                return dot;
            }
        }
        return null;
    }

    public static String getText(byte value) {
        EncryptionEnum dot = of(value);
        return dot == null ? null : dot.text;
    }

    public static String getText(byte value, String defaultText) {
        String text = getText(value);
        return text == null ? defaultText : text;
    }
}
