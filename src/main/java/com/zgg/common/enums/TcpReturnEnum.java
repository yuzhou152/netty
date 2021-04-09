package com.zgg.common.enums;

import com.zgg.common.json.JsonResult;

/**
 * tcp接收到消息后，需要立即返回（在业务处理之前先返回，也就是异步处理业务之前的返回内容）的信息
 */
public enum TcpReturnEnum {
    /**
     * 不需要立即应答，消息将根据方法的返回值同步应答
     */
    NON(null),
    /**
     * 立即应答的应答内容，暂时不支持
     */
    RIGHT_NOW(JsonResult.SUCCESS());

    private JsonResult value;

    TcpReturnEnum(JsonResult value) {
        this.value = value;
    }

    public JsonResult getValue() {
        return value;
    }

}
