package com.zgg.common.netty.sender;

import java.util.concurrent.Callable;

import com.zgg.common.constant.ConfigConstant;
import com.zgg.common.netty.config.SendResult;
import com.zgg.common.netty.protocol.message.MessageReceiveProtocol;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import com.zgg.common.util.IdBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import com.zgg.common.netty.config.SendResult.SendResultStatus;

/**
 * 发送消息时处理的Handler，异步向接收端发送指令，指令发送之后阻塞栈帧(方法)不阻塞线程，直到收到对方的应答包或者超时
 */
@Slf4j
public class TcpSendHandler implements Callable<SendResult> {
    private String lock = IdBuilder.getID();
    private long timeOut;
    private MessageReceiveProtocol receiveProtocol = null;
    private Channel channel;
    private MessageSendProtocol sendProtocol;
    private MessageSendListener listener;
    private boolean stopped = false;

    public TcpSendHandler(Channel channel, MessageSendProtocol sendProtocol, Long timeOut, MessageSendListener listener) {
        this.channel = channel;
        this.sendProtocol = sendProtocol;
        this.timeOut = timeOut == null || timeOut == 0 ? ConfigConstant.requestTimeOut * 1000 : timeOut * 1000;
        this.listener = listener;
    }

    /**
     * 执行线程，并返回线程的执行结果
     * <p>hile --> synchronized --> wait，是线程wait的最佳实践的标准写法，缺一不可：</p>
     * <p>while：       校验当前线程是否满足被唤醒后继续执行</p>
     * <p>synchronized：如果要调用某个对象的wait方法，必须获取到该对象的锁，调用notify也一样必须先获取锁</p>
     *
     * @return 向接收端发送的指令的对应的接收端, null代表发送失败
     */
    @Override
    public SendResult call() {
        if (channel == null) {//接收端不存在
            return fail(SendResultStatus.noChannel, null);
        }
        if (!channel.isActive()) {//接收端不可用
            return fail(SendResultStatus.channelNotActive, null);
        }
        ChannelFuture future = channel.writeAndFlush(this.sendProtocol);
        try {
            future.sync();//等待发送完毕
        } catch (Exception e) {
        }
        if (!future.isSuccess()) {//发送失败了
            log.error("消息发送异常", future.cause());
            return fail(SendResultStatus.exception, future.cause());
        }
        long time = System.currentTimeMillis();
        while (!stopped && System.currentTimeMillis() - time < timeOut) {//校验线程是否超时
            synchronized (this.lock) {
                try {
                    //线程挂起，直到超时或者被唤醒，必须有超时机制，否则线程将一直处于挂起状态
                    this.lock.wait(timeOut - System.currentTimeMillis() + time);
                } catch (InterruptedException e) {
                    //忽略线程被打断的情况，线程如果被打断并且不满足继续执行的条件，线程将继续wait
                }
            }
        }
        log.debug("Request {} waiting time {}ms", this.sendProtocol.getRequestId(), System.currentTimeMillis() - time);
        if (System.currentTimeMillis() - time >= timeOut) {//执行时间大于超时时间，发送超时了
            return fail(SendResultStatus.timeOut, null);
        }
        if (stopped && receiveProtocol == null) {//stopped=true，被终止了
            return fail(SendResultStatus.stopped, null);
        }
        if (listener != null) {
            listener.complete(sendProtocol, receiveProtocol);//成功了,启用监听器
        }
        return new SendResult(SendResultStatus.success, this.receiveProtocol);//返回应答结果，如果是超时的话，结果为null
    }

    /**
     * 已经获取到了对方的应答，唤醒当前的线程
     *
     * MessageReceiverHandler收到接收端的回执后，TcpReceiveHandler会调用此方法，用来唤醒 wait住的call() 方法
     */
    public void answer(MessageReceiveProtocol protocol) {
        this.receiveProtocol = protocol;
        this.stopped = true;
        synchronized (this.lock) {//调用notify必须先获取锁
            this.lock.notify();//唤醒当前线程
        }
    }

    public void stop() {
        this.stopped = true;
        synchronized (this.lock) {//调用notify必须先获取锁
            this.lock.notify();//唤醒当前线程
        }
    }

    private SendResult fail(SendResultStatus status, Throwable throwable) {
        if (this.listener != null) {
            listener.fail(this.sendProtocol, status, throwable);
        }
        return new SendResult(status, null);
    }
}
