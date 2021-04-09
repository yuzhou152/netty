package com.zgg.common.util;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池组装站
 */
public class ExecutorUtil {

    /**
     * 根据参数组装线程池
     *
     * @param corePoolSize                     核心线程数
     * @param maxPoolSize                      最大线程数
     * @param queueCapacity                    最大任务数
     * @param threadNamePrefix                 线程名称的前缀
     * @param waitForTasksToCompleteOnShutdown 关闭时是否等待线程运行结束
     * @param keepAliveSeconds                 非核心线程的最大活跃时间
     * @param rejectedExecutionHandler         任务拒绝策略
     * @param taskDecorator                    装饰器
     * @return 组装好的线程池
     */
    public static ThreadPoolTaskExecutor getExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String threadNamePrefix, boolean waitForTasksToCompleteOnShutdown, int keepAliveSeconds, RejectedExecutionHandler rejectedExecutionHandler, TaskDecorator taskDecorator) {
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
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);//线程池拒绝任务的处理策略
        executor.setTaskDecorator(taskDecorator);
        return executor;
    }

    /**
     * 线程池中的线程装饰器，用来解决线程池中线程继承外部MD的问题
     */
    public static class MdcTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                Map<String, String> context = MDC.getCopyOfContextMap();
                if (contextMap == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(contextMap);
                }
                try {
                    runnable.run();
                } finally {
                    if (context == null) {
                        MDC.clear();
                    } else {
                        MDC.setContextMap(context);
                    }
                }
            };

        }
    }
}
