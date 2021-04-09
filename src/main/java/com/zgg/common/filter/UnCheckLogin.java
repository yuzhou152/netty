package com.zgg.common.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 自定义注解（非token校验方法）
 * Author: zy
 * Date: 2020-08-05 15:15:06
 */
@Target({ElementType.TYPE, ElementType.METHOD})   //类名或方法上
@Retention(RetentionPolicy.RUNTIME)//运行时
public @interface UnCheckLogin {
    boolean check() default true;//参数
}
