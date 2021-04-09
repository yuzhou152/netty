package com.tceasy.cavphub.test.netty;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;

import com.zgg.common.constant.TcpConstant;
import com.zgg.common.enums.ActionEnum;
import com.zgg.common.enums.TcpClientEnum;
import com.zgg.common.netty.config.HeartbeatPinger;
import com.zgg.common.netty.protocol.message.MessageDecoder;
import com.zgg.common.netty.protocol.message.MessageEncoder;
import com.zgg.common.netty.protocol.message.MessageReceiveProtocol;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import com.zgg.common.netty.sender.TcpSendHandler;
import com.zgg.common.netty.config.SendResult;
import com.zgg.common.util.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * netty的客户端工具
 *
 * @author <a href="mailto:wendong.sun@tingjiandan.com">wendong.sun</a>
 * @create 2019/11/29 17:35
 */
public class NettyClient {
    private Bootstrap b;
    private ChannelFuture f;
    //    private InetSocketAddress address = new InetSocketAddress("123.56.7.118", 50088);
    private InetSocketAddress address = new InetSocketAddress("localhost", 50088);
    /**
     * 客户端接收到服务端的消息后的处理
     */
    private AnswerHandler answerHandler;
    /**
     * 客户端连接后，向服务端登录的消息体
     */
    private MessageSendProtocol loginMessage;
    /**
     * 当前客户端的客户端类型
     */
    private TcpClientEnum clientEnum;
    /**
     * 发送消息的线程池
     */
    private ThreadPoolTaskExecutor executor = getExecutor(10, 100, 1000, "client", true, 30, null, null);
    /**
     * 记录向客户端发送过的指令，以requestId为key，异步获取到客户端的响应包之后删除
     */
    private ConcurrentHashMap<String, TcpSendHandler> sendingHolder = new ConcurrentHashMap<>();

    public NettyClient(TcpClientEnum clientEnum, MessageSendProtocol loginMessage, AnswerHandler answerHandler) {
        this.clientEnum = clientEnum;
        this.loginMessage = loginMessage;
        this.answerHandler = answerHandler;
    }

    /**
     * 启动客户端，并执行登录请求
     */
    public void start() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            System.out.println("connecting ...");
            b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    // 解码编码
                    socketChannel.pipeline().addLast(new MessageDecoder());
                    socketChannel.pipeline().addLast(new MessageEncoder());
                    socketChannel.pipeline().addLast(new MySimpleChannelInboundHandler(answerHandler));
                }
            });
            f = b.connect(address).sync();//启动客户端并建立tcp连接
            if (loginMessage != null) {
                System.out.println(loginMessage);
                SendResult sendResult = write(loginMessage).get();//发送登录请求
                System.out.println(sendResult.getReceiveProtocol());
                Map result = JsonUtil.toMap(new String(sendResult.getReceiveProtocol().getContent()));//解析登录请求
                if (sendResult.isSuccess() && "0".equals(result.get("isSuccess"))) {
                    System.out.println("登录成功：" + sendResult.getStatus());
                    f.channel().attr(AttributeKey.valueOf("loginStatus")).set(true);
                } else {
                    System.out.println("登录失败：" + sendResult.getStatus() + ",result=" + result);
                    f.channel().close();
                    return;
                }
            }
            f.channel().closeFuture().addListener(future -> {//添加channel的关闭事件监听器，当channel断开时触发
                workerGroup.shutdownGracefully();//关闭workerGroup
                start();//重新启动客户端
            });
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            try {
                Thread.sleep(10000);//启动失败，等待10s
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            start();//启动失败，重新启动
        }
    }

    /**
     * 响应之前发送给客户端的指令
     *
     * @param protocol
     */
    public void answer(MessageReceiveProtocol protocol) {
        TcpSendHandler sender = sendingHolder.remove(protocol.getRequestId());//删除线程;
        if (sender != null) {
            sender.answer(protocol);//调用线程的应答方法并重新唤醒线程
        }
    }

    /**
     * 向服务器发送消息
     *
     * @param protocol 消息体
     * @return
     */
    public Future<SendResult> write(MessageSendProtocol protocol) {
        TcpSendHandler sender = new TcpSendHandler(f.channel(), protocol, 30L, null);
        TcpSendHandler exists = sendingHolder.put(protocol.getRequestId(), sender);
        if (exists != null) {
            exists.stop();
        }
        return executor.submit(sender);//线程池异步执行发送指令的任务
//        // 传数据给服务端
//        return f.channel().writeAndFlush(protocol);
    }

    /**
     * 判断当前客户端是否存活（TCP连接是否仍然可用）
     *
     * @return
     */
    public boolean isActive() {
        return f.channel().isActive() && f.channel().hasAttr(AttributeKey.valueOf("loginStatus")) && String.valueOf(f.channel().attr(AttributeKey.valueOf("loginStatus")).get()).equalsIgnoreCase("true");
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize
     * @param maxPoolSize
     * @param queueCapacity
     * @param threadNamePrefix
     * @param waitForTasksToCompleteOnShutdown
     * @param keepAliveSeconds
     * @param rejectedExecutionHandler
     * @param taskDecorator
     * @return
     */
    private ThreadPoolTaskExecutor getExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String threadNamePrefix, boolean waitForTasksToCompleteOnShutdown, int keepAliveSeconds, RejectedExecutionHandler rejectedExecutionHandler, TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程初始数量
        executor.setCorePoolSize(corePoolSize);
        // 线程允许最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 线程池队列数量
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setKeepAliveSeconds(keepAliveSeconds);//线程活跃时间 （秒）
        if (rejectedExecutionHandler != null) {
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);//线程池拒绝任务的处理策略
        }
        if (taskDecorator != null) {
            executor.setTaskDecorator(taskDecorator);
        }
        executor.initialize();
        return executor;
    }

    /**
     * 收到服务端消息后的应答处理器
     */
    public interface AnswerHandler {
        List<MessageSendProtocol> answer(MessageReceiveProtocol msg);
    }

    /**
     * 接收服务端的消息
     */
    private class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<MessageReceiveProtocol> {
        private AnswerHandler answerHanlder;

        public MySimpleChannelInboundHandler(AnswerHandler answerHanlder) {
            this.answerHanlder = answerHanlder;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageReceiveProtocol msg) throws Exception {
            if (msg.getAction() != ActionEnum.HEARTBEAT.getAction()) {
                System.out.println(msg);
            }
            new Thread(() -> {
                if (msg.getAnswer() == TcpConstant.TCP_REPLY_SIGN_ACTION && answerHanlder != null) {//当前消息是服务端主动发送的指令，则调用answerHanlder处理
                    List<MessageSendProtocol> msgs = answerHanlder.answer(msg);
                    for (MessageSendProtocol p : msgs) {
                        channelHandlerContext.writeAndFlush(p);
                    }
                } else if (msg.getAnswer() == TcpConstant.TCP_REPLY_SIGN_ANSWER) {//当前消息是服务端对当前客户端发送消息的应答，则找到线程并应答给调用者
                    answer(msg);
                }
            }).start();
        }

        /**
         * channel激活时触发
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            HeartbeatPinger.ping(ctx.channel());//channel激活之后，开始发送心跳
        }

        /**
         * channel关闭时触发
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            if (ctx.channel().isOpen() || ctx.channel().isActive()) {
                ctx.channel().close();
            }
        }
    }
}
