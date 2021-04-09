package com.zgg.common.netty;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.zgg.common.constant.ConfigConstant;
import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.ActionEnum;
import com.zgg.common.enums.TcpClientEnum;
import com.zgg.common.netty.receiver.MessageReceiverHandler;
import com.zgg.common.netty.protocol.message.MessageDecoder;
import com.zgg.common.netty.protocol.message.MessageEncoder;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import com.zgg.common.netty.sender.MessageSendHandler;
import com.zgg.common.netty.config.SendResult;

import com.zgg.common.netty.config.SendResult.SendResultStatus;
import com.zgg.common.util.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * netty的客户端工具  由TcpActionLoader 启动时调用此类连接服务端
 */
@Slf4j
@Component
public class NettyClient {

    private EventLoopGroup workerGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("worker_client", true));;
    private final EventExecutorGroup executors = new DefaultEventExecutorGroup(10, new DefaultThreadFactory("business_client", true));
    private Bootstrap bootstrap;
    private List<Channel> channels = new ArrayList<>();

    /**
     * 连接服务端
     * <<clientType, id>, MessageSendProtocol>
     */
    private Map<Map<String, String>, MessageSendProtocol> loginMessages = getLoginMessage();

    @Resource(name = "tcpClientTaskExecutor")
    private TaskExecutor executor;
    @Autowired
    private MessageSendHandler sendHandler;


    /**
     * 启动客户端，建立Tcp长连接
     *
     * @throws InterruptedException
     */
    private void connect() throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                // 解码编码
                socketChannel.pipeline().addLast(new MessageDecoder());
                socketChannel.pipeline().addLast(new MessageEncoder());
                socketChannel.pipeline().addLast(executors, new MessageReceiverHandler(executor));
            }
        });
    }

    /**
     * 登陆
     */
    private void login() throws Exception {
        loginMessages.forEach((client, msg) ->{
            try {
                Channel channel = bootstrap
                    .connect(new InetSocketAddress(ConfigConstant.client1Host, ConfigConstant.client1Port))
                    .sync().channel();
                channels.add(channel);
                SendResult loginResult;//发送登录请求
                String clientType = client.get("clientType");
                String id = client.get("id");
                loginResult = sendHandler.login(msg, channel).get();
                if (ObjectUtils.notEqual(SendResultStatus.success, loginResult.getStatus())) {
                    throw new IllegalStateException("登录失败");
                }
                channel.attr(AttributeKey.valueOf(TcpConstant.ATTRIBUTE_KEY_CLIENT_TYPE)).set(clientType);
                sendHandler.addChannel(TcpClientEnum.of(clientType), id ,channel);
            } catch (Exception e) {
                log.error("登录失败", e);
            }
        });
    }

    /**
     * 启动客户端，执行登录请求，并在连接失败时自动重连。登录失败不会触发重连
     */
    public void connectWithRetry() throws Exception {
        try {
            connect();
            login();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.warn(ConfigConstant.intervalSecond+"s 后重连...");
            Thread.sleep(ConfigConstant.intervalSecond);
            connectWithRetry();
        }
    }

    @PreDestroy
    @Order(1)
    public void destroy() {
        log.info("Shutdown Netty Client...");
        channels.forEach(channel ->{
            if (!channel.isActive()){
                channel.close();
            }
        });
        workerGroup.shutdownGracefully();
        log.info("Shutdown Netty Client...");
    }

    /**
     * 模拟生成客户端登录的消息体
     * @return
     */
    public static Map<Map<String, String>, MessageSendProtocol> getLoginMessage() {
        Map<Map<String, String>, MessageSendProtocol> loginMessages = new HashMap<>();
        {
            Map<String, String> msg = new HashMap<>();
            msg.put("clientType", ActionEnum.TX0001.getClient().name());
            msg.put("id", "fdahudsf123ddfi98712");
            byte[] content = JsonUtil.objectToJson(msg).getBytes();
            char action = ActionEnum.LOGIN.getAction();
            MessageSendProtocol protocol = new MessageSendProtocol(action, TcpConstant.TCP_REPLY_SIGN_ACTION, TcpConstant.TCP_ENCRYPTION_NON, content);
            loginMessages.put(msg, protocol);
        }
        {
            Map<String, String> msg = new HashMap<>();
            msg.put("clientType", ActionEnum.TX0002.getClient().name());
            msg.put("id", "sdfjhrewygc4565gtuw3edc");
            byte[] content = JsonUtil.objectToJson(msg).getBytes();
            char action = ActionEnum.LOGIN.getAction();
            MessageSendProtocol protocol = new MessageSendProtocol(action, TcpConstant.TCP_REPLY_SIGN_ACTION, TcpConstant.TCP_ENCRYPTION_NON, content);
            loginMessages.put(msg, protocol);
        }
        return loginMessages;
    }
}
