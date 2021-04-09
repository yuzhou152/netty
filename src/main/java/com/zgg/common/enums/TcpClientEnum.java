package com.zgg.common.enums;

/**
 * tcp命令标识的分类，用来区分命令标识适用的tcp客户端
 */
public enum TcpClientEnum {

    /**
     * 场端服务器使用的action
     */
    PARKSERVER,
    /**
     * 车载终端tcp客户端使用的action
     */
    VEHICLE,
    /**
     * 感知终端tcp客户端使用的action
     */
    TERMINAL,
    /**
     * 车载终端登录ORM时，tcp客户端使用的action
     */
    OEM,
    /**
     * 道闸tcp客户端使用的action
     */
    BARRIER;


    public static TcpClientEnum of(String name) {
        return TcpClientEnum.valueOf(String.valueOf(name).toUpperCase());
    }
}
