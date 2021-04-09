package com.zgg.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Description: redis配置类
 * Author: zy
 * Date: 2020-03-23 15:43:58
 */
@Configuration
public class RedisConfig {

    /**
     * Description: 自定义序列化
     * JdkSerializationRedisSerializer. 使用JDK提供的序列化功能。 优点是反序列化时不需要提供类型信息(class)，但缺点是序列化后的结果非常庞大，是JSON格式的5倍左右，这样就会消耗redis服务器的大量内存。
     * Jackson2JsonRedisSerializer. 使用Jackson库将对象序列化为JSON字符串。优点是速度快，序列化后的字符串短小精悍
     * Author: zy
     * Date: 2020-03-23 16:40:04
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericToStringSerializer generic = new GenericToStringSerializer<String>(String.class);
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}
