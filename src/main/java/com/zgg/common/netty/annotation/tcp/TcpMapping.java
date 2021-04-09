package com.zgg.common.netty.annotation.tcp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zgg.common.enums.ActionEnum;
import com.zgg.common.enums.TcpReturnEnum;

/**
 * tcp请求的方法标识
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TcpMapping {
    /**
     * 命令标识
     */
    ActionEnum action();

    /**
     * 需要在业务处理之前先返回给客户端的内容
     */
    TcpReturnEnum immediatelyReturn() default TcpReturnEnum.NON;
}
