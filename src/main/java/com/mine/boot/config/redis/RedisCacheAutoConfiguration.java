package com.mine.boot.config.redis;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;

@EnableCaching
@Configuration
@AutoConfigureAfter(RedisCacheAutoConfiguration.class)
public class RedisCacheAutoConfiguration {

    @Bean("redisTemplate")
    public RedisTemplate<String, Serializable> redisCacheTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()))
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(30));
        RedisCacheManager cacheManager = RedisCacheManager
                .builder(lettuceConnectionFactory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
}
