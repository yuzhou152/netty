package com.zgg.common.netty.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import com.zgg.common.enums.ActionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 缓存所有的TcpController的映射
 */
@Component
public class TcpActionMappings {
    /**
     * ActionEnum @TcpMapping 方法的唯一标识
     * Invocker @TcpMapping 方法的相关信息
     */
    private ConcurrentHashMap<ActionEnum, Invocker> invockers = new ConcurrentHashMap<>();

    /**
     * 添加扫描到的TcpController
     */
    public void addInvocker(ActionEnum action, Invocker invocker) {
        if (invockers.containsKey(action)) {
            throw new RuntimeException("Tcp action " + action + " conflict between [" + invocker.method + "] with [" + invockers.get(action).getMethod() + "]");
        }
        invockers.put(action, invocker);
    }

    /**
     * 根据action获取一个TcpController
     *
     * @param action
     * @return
     */
    public Invocker getInvocker(ActionEnum action) {
        return action == null ? null : invockers.get(action);
    }

    @Data
    @AllArgsConstructor
    public static class Invocker {
        private Object bean;
        private Method method;
        private Annotation[] params;
    }
}
