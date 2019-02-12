package com.mine.boot.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * RedisUtils
 *
 * @author zhangsl
 * @date 2019/1/28 10:15
 */
@Component
public class RedisUtils {

    @Autowired
    private static RedisTemplate redisTemplate;

    public static RedisTemplate getRedisTemplate() {
        if (RedisUtils.redisTemplate == null) {
            RedisUtils.redisTemplate = (RedisTemplate) ApplicationContextBeanUtils.getBean("redisTemplate");
        }
        return RedisUtils.redisTemplate;
    }

    public static Object get(String key) {
        return getRedisTemplate().opsForValue().get(key);
    }

    public static void set(String key, Object value) {
        getRedisTemplate().opsForValue().set(key, value);
    }

    public static void set(String key, Object value, long timeout) {
        getRedisTemplate().opsForValue().set(key, value, timeout);
    }

    public  static void set(String key, Object value, long timeout, TimeUnit unit) {
        getRedisTemplate().opsForValue().set(key, value, timeout, unit);
    }

    public static void remove(String key) {
        getRedisTemplate().delete(key);
    }

    public static void expire(String key, long timeout, TimeUnit unit) {
        getRedisTemplate().expire(key, timeout, unit);
    }

    public static void expireAt(String key, Date date) {
        getRedisTemplate().expireAt(key, date);
    }

    public static Long getExpire(String key) {
        return getRedisTemplate().getExpire(key);
    }

    public static Long getExpire(String key, TimeUnit unit) {
        return getRedisTemplate().getExpire(key, unit);
    }

    public static void put(String key, String hashKey, Object value) {
        getRedisTemplate().opsForHash().put(key, hashKey, value);
    }

    public static Object get(String key, String hashKey) {
        return getRedisTemplate().opsForHash().get(key, hashKey);
    }

    public static Long increment(String key) {
        return getRedisTemplate().opsForValue().increment(key);
    }

    public static Long increment(String key, long delta) {
        return getRedisTemplate().opsForValue().increment(key, delta);
    }

    public static Double increment(String key, double delta) {
        return getRedisTemplate().opsForValue().increment(key, delta);
    }

}
