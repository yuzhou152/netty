package com.zgg.common.netty.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.zgg.common.netty.NettyServer;
import com.zgg.common.netty.annotation.Body;
import com.zgg.common.netty.annotation.Param;
import com.zgg.common.netty.annotation.TcpAttr;
import com.zgg.common.netty.annotation.tcp.TcpController;
import com.zgg.common.netty.annotation.tcp.TcpMapping;
import com.zgg.common.netty.NettyClient;
import com.zgg.common.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TcpController的注解扫描
 */
@Component
@Slf4j
public class TcpActionLoader implements CommandLineRunner {
    @Autowired
    private TcpActionMappings actionMappings;
    @Autowired
    private NettyServer nettyServer;
    @Autowired
    private NettyClient nettyClient;

    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> beans = SpringUtil.getBeansWithAnnotation(TcpController.class);
        // 获取所有的带TcpController注解的bean
        for (Object bean : beans.values()) {
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(bean.getClass(), TcpMapping.class);//获取bean中带有TcpMapping注解的方法
            for (Method method : methods) {
                Annotation[] params = getParams(bean.getClass(), method);//递归迭代bean及其父类中的方法，获取方法参数中自定义的注解
                TcpMapping mapping = method.getAnnotation(TcpMapping.class);
                //缓存tcp指令和bean、method、param的关系
                actionMappings.addInvocker(mapping.action(), new TcpActionMappings.Invocker(bean, method, params));
            }
        }
        // 启动服务端
        nettyServer.start();
        // 启动客户端
        //nettyClient.connectWithRetry();
    }

    /**
     * 获取方法参数的自定义注解
     *
     * @param method
     * @return
     */
    private Annotation[] getAnnotations(Method method) {
        Annotation[] params = new Annotation[method.getParameterTypes().length];
        boolean found = false;
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Annotation[] annotations = method.getParameterAnnotations()[i];
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param || annotation instanceof Body || annotation instanceof TcpAttr) {
                    params[i] = annotation;
                    found = true;
                    break;
                }
            }
        }
        return found ? params : null;
    }

    /**
     * 递归获取方法参数的注解，由于context.getBean直接获取到的bean有可能是被增强过的bean(代理类)，所以没有相关的注解，需要从父类或父接口获取
     *
     * @param clz
     * @param method
     * @return
     */
    private Annotation[] getParams(Class clz, Method method) {
        if (Object.class.getName().equals(clz.getName())) {
            return null;//已经递归到Object了，忽略
        }
        Annotation[] params = getAnnotations(method);
        if (params != null) {
            return params;//已经获取到了注解，结束递归
        }
        for (int i = 0; i < clz.getInterfaces().length + 1; i++) {
            Class superClz = i == 0 ? clz.getSuperclass() : clz.getInterfaces()[i - 1];
            try {
                Method m = superClz.getMethod(method.getName(), method.getParameterTypes());//从父类或父接口中获取相同名称和相关参数的方法
                params = getParams(clz.getSuperclass(), m);//递归调用
                if (params != null) {
                    return params;
                }
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }
}
