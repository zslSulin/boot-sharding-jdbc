package com.mine.boot.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * RedisLockAdvice
 * 实现分布式锁的逻辑
 * @author zhangsl
 * @date 2018/12/11 16:57
 */
@Slf4j
@Component
@Aspect
public class RedisLockAdvice {

    @Resource
    private RedisDistributionLock redisDistributionLock;

    @Around("@annotation(RedisLockAnnotation)")
    public Object processAround(ProceedingJoinPoint pjp) throws Throwable {
        //获取方法上的注解对象
        String methodName = pjp.getSignature().getName();
        Class<?> classTarget = pjp.getTarget().getClass();
        Class[] par = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        Method objMethod = classTarget.getMethod(methodName, par);
        RedisLockAnnotation redisLockAnnotation = objMethod.getDeclaredAnnotation(RedisLockAnnotation.class);

        //拼装分布式锁的key
        String[] keys = redisLockAnnotation.keys();
        Object[] args = pjp.getArgs();
        Object arg = args[0];
        StringBuilder temp = new StringBuilder();
        temp.append(redisLockAnnotation.keyPrefix());
        for (String key : keys) {
            String getMethod = "get" + StringUtils.capitalize(key);
            temp.append(MethodUtils.invokeExactMethod(arg, getMethod)).append("_");
        }
        String redisKey = StringUtils.removeEnd(temp.toString(), "_");

        //执行分布式锁的逻辑
        if (redisLockAnnotation.isSpin()) {
            //阻塞锁
            int lockRetryTime = 0;
            try {
                while (!redisDistributionLock.lock(redisKey, redisLockAnnotation.expireTime())) {
                    if (lockRetryTime++ > redisLockAnnotation.retryTimes()) {
                        log.error("lock exception. key:{}, lockRetryTime:{}", redisKey, lockRetryTime);
                        throw new RuntimeException();
                    }
                    //线程阻塞
                    Thread.sleep(redisLockAnnotation.waitTime());
                }
                return pjp.proceed();
            } finally {
                redisDistributionLock.unlock(redisKey);
            }
        } else {
            //非阻塞锁
            try {
                if (!redisDistributionLock.lock(redisKey)) {
                    log.error("lock exception. key:{}", redisKey);
                    throw new RuntimeException();
                }
                return pjp.proceed();
            } finally {
                redisDistributionLock.unlock(redisKey);
            }
        }
    }
}