package com.zgg.common.netty.config;

import java.util.concurrent.TimeUnit;

import com.zgg.common.constant.ConfigConstant;
import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.ActionEnum;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 5秒发送一次 发送心跳包 一般在ReceiverHandler中激活channel时启动心跳
 */
@Slf4j
public class HeartbeatPinger {
    /**
     * 发送心跳包 连接断开后 isActive() 会变为false ，以此判定连接是否已断开，再决定是否要关闭channel
     */
    public static void ping(Channel channel) {
        channel.eventLoop().scheduleAtFixedRate(() -> {
            if (channel.isActive()) {
                log.debug("Channel {} sending heart beat to the other side...", channel.id().asShortText());
                channel.writeAndFlush(new MessageSendProtocol(ActionEnum.HEARTBEAT.getAction(), TcpConstant.TCP_REPLY_SIGN_ACTION, TcpConstant.TCP_ENCRYPTION_NON, new byte[0]));
            } else {
                log.debug("Channel {} had broken, cancel the task that will send a heart beat.", channel.id().asShortText());
                channel.closeFuture();
                throw new RuntimeException();
            }
        }, 0, ConfigConstant.sendFreq, TimeUnit.MILLISECONDS);
    }

}
