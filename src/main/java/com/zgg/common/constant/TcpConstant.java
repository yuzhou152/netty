package com.zgg.common.constant;


/**
 * tcp接口相关的常量
 */
public class TcpConstant {

    /**
     * tcp业务包的起始标志
     */
    public static final byte[] HEAD_DATA = {0x23, 0x23};
    /**
     * netty的channel attribute中，客户端类型的key
     */
    public static final String ATTRIBUTE_KEY_CLIENT_TYPE = "AttrClientType";
    /**
     * netty的channel attribute中，感知设备ID的key
     */
    public static final String ATTRIBUTE_KEY_DEVICE_ID = "deviceId";
    /**
     * 应答标识：命令，标识该业务包为主动向对方发送的命令
     */
    public static final byte TCP_REPLY_SIGN_ACTION = 0x00;
    /**
     * 应答标识：应答，标识该业务包为对方某个业务包的响应
     */
    public static final byte TCP_REPLY_SIGN_ANSWER = 0x01;
    /**
     * 加密方式：不加密
     */
    public static final byte TCP_ENCRYPTION_NON = 0x01;
    /**
     * 加密方式：RSA
     */
    public static final byte TCP_ENCRYPTION_RSA = 0x02;
    /**
     * 加密方式：AES128
     */
    public static final byte TCP_ENCRYPTION_AES128 = 0x03;


}
