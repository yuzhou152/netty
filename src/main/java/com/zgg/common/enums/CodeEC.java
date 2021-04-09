package com.zgg.common.enums;


/**
 * Description: 自定义异常枚举
 * Author: zy
 * Date: 2019-07-16 17:14:07
 */
public enum CodeEC {

    /**
     *错误码规范：
     *0XXXXX：公共模块
     *1XXXXX：用户模块
     */

    /**
     * 公共模块
     */
    SUCCESS("0", "成功"),
    PARAM_INVALID("1", "参数无效"),
    DB_ERROR("2", "数据库错误"),
    NONE("3", "未知错误"),
    FORCED_OFF_LINE("4", "您的帐号在另一个地点/设备登录，您已被迫下线。如非本人操作，建议立即修改密码。"),
    PERMISSION_REFUSE("5", "无权访问"),
    SERVICE_ERROR("500", "服务器内部错误"),
    REPEAT_DATA_ERROR("6", "重复数据"),
    /**
     * 流程模块
     */
    BASE_FLOW_NOT_EXIT("1000001", "次处理事项基础流程不存在"),
    FLOW_NOT_EXIT("1000002", "此处理事项流程不存在"),
    /**
     * 处理事项模块
     */
    MATTERS_NOT_EXIT("2000001", "处理事项不存在"),
    /**
     * 案卷模块
     */
    TENANT_CONFIG_ARCHIVENUM_ERROR("3000035", "租户案卷号配置有误"),
    /**
     * netty
     */
    CLIENT_TYPE_UNKNOW("N000001","Client type is unknown."),
    ACTION_NOT_FOUND("N000002","Action is not found, please check the action code or login status."),
    SYSTEM_ERROR("N000003","System error."),
    LOGIN_SUCESS("N000004","Login success.");
    private String code;
    private String msg;

    CodeEC(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
