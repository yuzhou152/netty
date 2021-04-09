package com.zgg.common.util;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * springUtil类，可以动态获取spring管理的bean
 *
 * @author <a href="mailto:sunwendong@celloud.cn">sun8wd</a>
 * @version Revision: 1.0
 * @date 2016年12月23日上午11:32:17
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    /**
     * 不建议在非静态方法内给静态属性赋值
     *
     * @param applicationContext
     */
    private static void set(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext get() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean，可以获取某个接口下指定名称的实现类
     *
     * @param name
     * @param clazz
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取接口下的所以实现类（必须在spring中声明过）
     *
     * @param clazz 指定是接口的话，可以获取其所有的实现类
     * @return
     */
    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return applicationContext.getBeansWithAnnotation(annotationType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        set(applicationContext);
    }
}
