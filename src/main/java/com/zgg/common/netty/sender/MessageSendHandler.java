package com.zgg.common.netty.sender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.TcpClientEnum;
import com.zgg.common.netty.config.SendResult;
import com.zgg.common.netty.protocol.message.MessageReceiveProtocol;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 发送指令的handler
 */
@Component
@Slf4j
public class MessageSendHandler {
    /**
     * 记录客户端连接到本端 和 本端连接到服务端 的channel， 建立连接后实际上已不分客户/服务端了
     *
     *     1号机 业务A   ->  channel1  <-  本机
     *     1号机 业务B   ->  channel2  <-  本机
     *     2号机 业务A   ->  channel3  <-  本机
     *     2号机 业务B   ->  channel4  <-  本机
     */
    private ConcurrentHashMap<Client, Channel> channels = new ConcurrentHashMap<>();
    /**
     * 发送指令时，记录发送过的指令，以requestId为key，异步获取到接受端的响应包之后删除
     */
    @Getter
    private ConcurrentHashMap<String, TcpSendHandler> sendingHolder = new ConcurrentHashMap<>();
    /**
     * 异步发送指令的线程池
     */
    @Resource(name = "tcpServerSendExecutor")
    private ThreadPoolTaskExecutor executor;

    /**
     * 向其它端发起登陆
     */
    public Future<SendResult> login(MessageSendProtocol protocol, Channel channel) {
        TcpSendHandler sender = new TcpSendHandler(channel, protocol, 0L, null);
        TcpSendHandler exists = sendingHolder.put(protocol.getRequestId(), sender);
        if (exists != null) {
            exists.stop();
        }
        return executor.submit(sender);//线程池异步执行发送指令的任务
    }
    /**
     * 主动发送指令，并返回Future，异步获取应答
     */
    public Future<SendResult> send(TcpClientEnum clientEnum, String clientId, MessageSendProtocol protocol) {
        return send0(clientEnum, clientId, protocol, null, null);//线程池异步执行发送指令的任务
    }

    private Future<SendResult> send0(TcpClientEnum clientEnum, String clientId, MessageSendProtocol protocol, Long timeOut, MessageSendListener listener) {
        Client client = new Client(clientEnum, clientId);
        Channel channel = channels.get(client);
        TcpSendHandler sender = new TcpSendHandler(channel, protocol, timeOut, listener);
        TcpSendHandler exists = sendingHolder.put(protocol.getRequestId(), sender);
        if (exists != null) {
            exists.stop();
        }
        return executor.submit(sender);//线程池异步执行发送指令的任务
    }

    /**
     * 响应之前发送给接收端的指令
     */
    public void answer(MessageReceiveProtocol protocol) {
        TcpSendHandler sender = sendingHolder.remove(protocol.getRequestId());//删除线程;
        if (sender != null) {
            sender.answer(protocol);//调用线程的应答方法并重新唤醒线程
        }
    }

    /**
     * 记录已登录的channel，作为服务端被登陆时 作为客户端登陆时都进行记录
     */
    public void addChannel(TcpClientEnum clientEnum, String id, Channel channel) {
        Client client = new Client(clientEnum, id);
        Channel c = channels.get(client);
        if (c != null) {
            c.close();
        }
        channels.put(client, channel);
        System.out.println(channels.toString());
    }

    /**
     * 连接是否可用
     */
    public boolean isActive(TcpClientEnum clientEnum, String id) {
        Client client = new Client(clientEnum, id);
        Channel channel = channels.get(client);
        return channel != null && channel.isActive();
    }

    /**
     * 移除当前已关闭的channel
     */
    public void removeChannel(Channel channel) {
        for (Map.Entry<Client, Channel> entry : channels.entrySet()) {
            if (entry.getValue().id().asLongText().equals(channel.id().asLongText())) {
                Client client = entry.getKey();
                channels.remove(client);
            }
        }
    }

    /**
     * 关闭所有的连接 channel两端的任意一端关闭时都会通知对方
     */
    @PreDestroy
    @Order(0)
    public void closeAll() {
        log.info("Closing all client channel ...");
        channels.forEach((k,v) -> {

        });
        log.info("Close all client channel ...");
    }

    /**
     * 一台机器可以按照业务与本端建立多个连接
     */
    @AllArgsConstructor
    @EqualsAndHashCode
    private class Client {
        /**
         * 业务的分类
         */
        private TcpClientEnum clientEnum;
        /**
         * 机器ID
         */
        private String id;
    }
}
