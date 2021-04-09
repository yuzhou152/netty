package com.zgg.request.service.tcp.impl;

import java.util.Map;

import com.zgg.common.enums.TcpClientEnum;
import com.zgg.common.util.IdBuilder;
import com.zgg.request.service.tcp.LoginService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Override
    public String terminal(Channel channel, Map body) {
        return IdBuilder.getID();
    }

    @Override
    public String oem(Channel channel, Map body) {
        return IdBuilder.getID();
    }

    @Override
    public String barrier(Channel channel, Map body) {
        return IdBuilder.getID();
    }

    @Override
    public String vehicle(Channel channel, Map body) {
        return IdBuilder.getID();
    }

    @Override
    public void logout(TcpClientEnum clientEnum, String id, Channel channel) {

    }

    @Override
    public String parkServer(Channel channel, Map body) {
        return IdBuilder.getID();
    }
}
