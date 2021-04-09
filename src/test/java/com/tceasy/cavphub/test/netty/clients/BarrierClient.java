package com.tceasy.cavphub.test.netty.clients;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.tceasy.cavphub.test.netty.NettyClient;
import com.tceasy.cavphub.test.utils.ClientMessageUtils;
import com.zgg.common.enums.TcpClientEnum;
import com.zgg.common.netty.protocol.message.MessageSendProtocol;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:wendong.sun@tingjiandan.com">wendong.sun</a>
 * @create 2019/11/29 17:35
 */
public class BarrierClient {
    public static void main(String[] args) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("root");
        logger.setLevel(Level.toLevel("INFO"));
        new Thread(() -> {
            MessageSendProtocol loginMessage = ClientMessageUtils.getLoginMessage(TcpClientEnum.BARRIER, "4bd4a006c006475796e96fc3ba6e4f25");
            NettyClient client = new NettyClient(TcpClientEnum.BARRIER, loginMessage, null);
            client.start();
        }).start();
    }

}
