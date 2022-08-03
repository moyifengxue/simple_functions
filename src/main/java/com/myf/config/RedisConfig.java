package com.myf.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.myf.entity.HelloWord;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author zgx
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;


    private final RedisConnectionFactory factory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    @Qualifier("helloWordRedisCache")
    public RedisTemplate<String, HelloWord> helloWordRedisCache(){
        RedisTemplate<String, HelloWord> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new FastJsonRedisSerializer<>(HelloWord.class));
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(HelloWord.class));
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    /**
     * 构建Redisson操作对象
     * @return RedissonClient
     */
    @Bean(destroyMethod = "shutdown") // 服务停止后调用 shutdown 方法。
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单机模式。
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        // 看门狗的默认时间。
        config.setLockWatchdogTimeout(30000);
        return Redisson.create(config);
    }
}
