package com.zgg.request.service.tcp;

import java.util.Map;

import com.zgg.common.enums.TcpClientEnum;
import io.netty.channel.Channel;

/**
 * 登录服务
 *
 */
public interface LoginService {
    /**
     * 路侧终端登录
     *
     * @param channel
     * @param body
     * @return
     */
    String terminal(Channel channel, Map body);

    /**
     * 车辆登录OEM
     *
     * @param channel
     * @param body
     * @return
     */
    String oem(Channel channel, Map body);

    /**
     * 道闸登录
     *
     * @param channel
     * @param body
     * @return
     */
    String barrier(Channel channel, Map body);

    /**
     * avp车辆登录
     *
     * @param channel
     * @param body
     * @return
     */
    String vehicle(Channel channel, Map body);

    /**
     * 客户端退出
     *
     * @param clientEnum
     * @param id
     * @param channel
     */
    void logout(TcpClientEnum clientEnum, String id, Channel channel);

    String parkServer(Channel channel, Map body);
}
