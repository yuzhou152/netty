
package com.zgg.common.constant;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 配置敞亮
 * @author zy
 */
@Component
public class ConfigConstant implements ApplicationContextAware {

    public static int nettyPort;
    public static int readerIdle;
    public static int writerIdle;
    public static int allIdle;
    public static long sendFreq;
    public static int requestTimeOut;
    public static String client1Host;
    public static int client1Port;
    public static int intervalSecond;

    @Autowired
    private Environment env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 启动时
        nettyPort = Integer.valueOf(applicationContext.getEnvironment().getProperty("netty.port").trim());
        readerIdle = Integer.valueOf(applicationContext.getEnvironment().getProperty("netty.idle.reader").trim());
        writerIdle = Integer.valueOf(applicationContext.getEnvironment().getProperty("netty.idle.writer").trim());
        allIdle = Integer.valueOf(applicationContext.getEnvironment().getProperty("netty.idle.all").trim());
        sendFreq = Long.valueOf(applicationContext.getEnvironment().getProperty("netty.sendFreq").trim());
        requestTimeOut = Integer.valueOf(applicationContext.getEnvironment().getProperty("netty.requestTimeOut").trim());
        client1Host = applicationContext.getEnvironment().getProperty("netty.client1.host").trim();
        client1Port = Integer.valueOf(applicationContext.getEnvironment().getProperty("netty.client1.port").trim());
        intervalSecond = Integer.valueOf(applicationContext.getEnvironment().getProperty("netty.client1.intervalSecond").trim());

        // 运行中
        String activeProfile = env.getActiveProfiles()[0];
        String property = env.getProperty("server.servlet.context-path");
        System.out.println();
    }
}

