package com.zgg.common.netty.annotation.tcp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * tcp请求的控制器
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TcpController {

    /**
     * spring bean的name
     * @return
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
