package com.zgg.common.netty;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.zgg.common.constant.ConfigConstant;
import com.zgg.common.netty.receiver.MessageReceiverHandler;
import com.zgg.common.netty.protocol.message.MessageDecoder;
import com.zgg.common.netty.protocol.message.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * netty的server端
 */
@Slf4j
@Component
@Data
public class NettyServer {
    /**
     * 构造两个线程组，bossGroup 用于接收客户端传过来的请求，接收到请求后将后续操作交由 workerGroup 处理
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("boss", true));
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("worker", true));
    private final EventExecutorGroup executors = new DefaultEventExecutorGroup(10, new DefaultThreadFactory("business", true));
    @Resource(name = "tcpServerTaskExecutor")
    private TaskExecutor executor;
    private Channel channel;

    /**
     * 启动服务
     */
    public ChannelFuture start() {
        ChannelFuture f = null;
        try {
            //服务端启动辅助类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) {
                    // 解码编码
                    channel.pipeline().addLast(new ChunkedWriteHandler());
                    channel.pipeline().addLast(new MessageEncoder());
                    channel.pipeline().addLast(new MessageDecoder());
                    // 自动断连触发器配置
                    channel.pipeline().addLast(new IdleStateHandler(ConfigConstant.readerIdle, ConfigConstant.writerIdle, ConfigConstant.allIdle));
                    channel.pipeline().addLast(new ServerIdleStateTrigger());
                    // channel监听器配置
                    channel.pipeline().addLast(executors, new MessageReceiverHandler(executor));
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            f = b.bind(ConfigConstant.nettyPort).syncUninterruptibly();
            channel = f.channel();

        } catch (Exception e) {
            log.error("Netty start error:", e);
            throw e;
        } finally {
            if (f != null && f.isSuccess()) {
                log.info("Netty server listening on port {} and ready for connections...", ConfigConstant.nettyPort);
            } else {
                log.error("Netty server start up Error!");
            }
        }
        return f;
    }

    /**
     * servelet卸载时调用
     */
    @PreDestroy
    @Order(2)
    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            channel.close();
        }
        // 退出，释放线程池资源
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        executors.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }

    /**
     * 自动断连触发器，只在服务端生效，在规定时间客户端无读写数据时触发，使用IdleStateHandler规定触发条件
     */
    class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                // 心跳
                IdleState state = ((IdleStateEvent) evt).state();
                log.info("Channel {} triggered event {}, will be closed.", ctx.channel().id().asShortText(), state.name());
                ctx.close();
            } else {
                // 传递给下一个处理程序
                super.userEventTriggered(ctx, evt);
            }
        }
    }

}
