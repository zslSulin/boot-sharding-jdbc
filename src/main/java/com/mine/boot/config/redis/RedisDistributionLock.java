package com.mine.boot.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * RedisDistributionLock
 * 锁的实现
 * @author zhangsl
 * @date 2018/12/11 15:26
 */
@Slf4j
public class RedisDistributionLock {

    //key的TTL,一天
    private static final int finalDefaultTTLwithKey = 24 * 3600;

    //锁默认超时时间,20秒
    private static final long defaultExpireTime = 20 * 1000;

    private static final boolean SUCCESS = true;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplateForGeneralize;

    /**
     * 加锁,锁默认超时时间20秒
     * @param resource
     * @return
     */
    public boolean lock(String resource) {
        return this.lock(resource, defaultExpireTime);
    }

    /**
     * 加锁,同时设置锁超时时间
     * @param key
     * @param expireTime
     * @return
     */
    public boolean lock(String key, long expireTime) {
        log.debug("redis lock debug, start. key:[{}], expireTime:[{}]", key, expireTime);
        long now = Instant.now().toEpochMilli();
        long lockExpireTime = now + expireTime;

        //setnx
        Boolean executeResult = redisTemplateForGeneralize.opsForValue().setIfAbsent(key, String.valueOf(lockExpireTime));
        log.debug("redis lock debug, setnx. key:[{}], expireTime:[{}], executeResult:[{}]", key, expireTime, executeResult);

        //取锁成功,为key设置expire
        if (executeResult) {
            redisTemplateForGeneralize.expire(key, finalDefaultTTLwithKey, TimeUnit.SECONDS);
            return true;
        } else {
            Object valueFromRedis = this.getKeyWithRetry(key, 3);
            //避免获取锁失败,同时对方锁释放后造成NPE
            if (valueFromRedis != null) {
                long oldExpireTime = Long.parseLong((String) valueFromRedis);
                log.debug("redis lock debug, key already seted. key:[{}], oldExpireTime:[{}]", key, oldExpireTime);
                //锁过期时间小于当前时间,锁已经超时,重新取锁
                if (oldExpireTime <= now) {
                    log.debug("redis lock debug, lock time expired. key:[{}], oldExpireTime:[{}], now:[{}]", key, oldExpireTime, now);
                    String valueFromRedis2 = redisTemplateForGeneralize.opsForValue().getAndSet(key, String.valueOf(lockExpireTime));
                    long currentExpireTime = Long.parseLong(valueFromRedis2);
                    //判断currentExpireTime与oldExpireTime是否相等
                    if (currentExpireTime == oldExpireTime) {
                        log.debug("redis lock debug, getSet. key:[{}], currentExpireTime:[{}], oldExpireTime:[{}], lockExpireTime:[{}]", key, currentExpireTime, oldExpireTime, lockExpireTime);
                        redisTemplateForGeneralize.expire(key, finalDefaultTTLwithKey, TimeUnit.SECONDS);
                        return true;
                    } else {
                        //不相等,取锁失败
                        return false;
                    }
                }
            } else {
                log.warn("redis lock,lock have been release. key:[{}]", key);
                return false;
            }
        }
        return false;
    }

    private Object getKeyWithRetry(String key, int retryTimes) {
        int failTime = 0;
        while (failTime < retryTimes) {
            try {
                return redisTemplateForGeneralize.opsForValue().get(key);
            } catch (Exception e) {
                failTime++;
                if (failTime >= retryTimes) {
                    throw e;
                }
            }
        }
        return null;
    }

    /**
     * 解锁
     * @param key
     * @return
     */
    public boolean unlock(String key) {
        log.debug("redis unlock debug, start. resource:[{}].", key);
        redisTemplateForGeneralize.delete(key);
        return true;
    }
}
