package com.zgg.common.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @TcpMapping的唯一标识
 */
@Slf4j
@Getter
public enum ActionEnum {
    HEARTBEAT(null, (char) 0x0001, "心跳"),
    LOGIN(null, (char) 0x0002, "客户端登录"),

    TX0001(TcpClientEnum.TERMINAL, (char) 0x0003, "终端长连接入口"),
    TX0002(TcpClientEnum.PARKSERVER, (char) 0x0004, "终端长连接入口");

    private char action;
    private TcpClientEnum client;
    private String text;

    ActionEnum(TcpClientEnum client, char action, String text) {
        this.action = action;
        this.client = client;
        this.text = text;
    }

    /**
     * 根据 action 获取 enum
     */
    public static ActionEnum of(char action) {
        for (ActionEnum actionEnum : ActionEnum.values()) {
            if (actionEnum.action == action) {
                return actionEnum;
            }
        }
        return null;
    }

    /**
     * 根据action获取text
     */
    public static String getText(char action, String defaultText) {
        String text = getText(action);
        return text == null ? defaultText : text;
    }

    public static String getText(char action) {
        ActionEnum dot = of(action);
        return dot == null ? null : dot.getText();
    }

    @Override
    public String toString() {
        return "ActionEnum{" +
                "action=" + String.format("0x%04x", (int) this.action) +
                ", client=" + client +
                ", text='" + text + '\'' +
                '}';
    }
}
