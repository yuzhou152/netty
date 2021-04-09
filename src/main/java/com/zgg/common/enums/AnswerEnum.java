package com.zgg.common.enums;

import lombok.Getter;

/**
 * Tcp包的类别 命令 / 应答
 */
@Getter
public enum AnswerEnum {
    DIRECT((byte) 0x00, "命令包"), SUCCESS((byte) 0x01, "应答包");
    private byte value;
    private String text;

    AnswerEnum(byte value, String text) {
        this.value = value;
        this.text = text;
    }

    public static AnswerEnum of(byte value) {
        for (AnswerEnum dot : AnswerEnum.values()) {
            if (dot.value == value) {
                return dot;
            }
        }
        return null;
    }

    public static String getText(byte value) {
        AnswerEnum dot = of(value);
        return dot == null ? null : dot.getText();
    }

    public static String getText(byte value, String defaultText) {
        String text = getText(value);
        return text == null ? defaultText : text;
    }
}
