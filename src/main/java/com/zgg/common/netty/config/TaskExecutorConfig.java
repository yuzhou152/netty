package com.zgg.common.netty.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.zgg.common.util.ExecutorUtil;

/**
 * 线程池配置
 */
@Configuration
@EnableAsync
public class TaskExecutorConfig {
    /**
     * springboot默认的线程池，给@Async使用
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        return ExecutorUtil.getExecutor(100, 200, 1000, "tasks-", true, 60, new ThreadPoolExecutor.CallerRunsPolicy(), new ExecutorUtil.MdcTaskDecorator());
    }

    /**
     * 本应用作为服务端时，tcp消息处理的线程池，用来异步处理tcp发送过来的消息指令
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor tcpServerTaskExecutor() {
        return ExecutorUtil.getExecutor(100, 500, 1000, "tcp-server-tasks-", true, 60, new ThreadPoolExecutor.CallerRunsPolicy(), new ExecutorUtil.MdcTaskDecorator());
    }

    /**
     * 本应用作为服务端时，tcp消息发送的指令，用来异步想客户端发送tcp指令，并阻塞线程异步获取客户端的应答
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor tcpServerSendExecutor() {
        return ExecutorUtil.getExecutor(100, 500, 1000, "tcp-server-send-", true, 60, new ThreadPoolExecutor.CallerRunsPolicy(), new ExecutorUtil.MdcTaskDecorator());
    }

    /**
     * 本应用作为客户端时，tcp消息处理的线程池，用来异步处理tcp发送过来的消息指令
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor tcpClientTaskExecutor() {
        return ExecutorUtil.getExecutor(100, 500, 1000, "tcp-client-tasks-", true, 60, new ThreadPoolExecutor.CallerRunsPolicy(), new ExecutorUtil.MdcTaskDecorator());
    }

    /**
     * 本应用作为客户端时，tcp消息发送的指令，用来异步想客户端发送tcp指令，并阻塞线程异步获取客户端的应答
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor tcpClientSendExecutor() {
        return ExecutorUtil.getExecutor(100, 500, 1000, "tcp-client-send-", true, 60, new ThreadPoolExecutor.CallerRunsPolicy(), new ExecutorUtil.MdcTaskDecorator());
    }
}
