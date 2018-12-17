package com.mine.boot.config.redis;

import java.lang.annotation.*;

/**
 * LockParamAnnotation
 *
 * @author zhangsl
 * @date 2018/12/14 17:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Documented
public @interface LockParamAnnotation {
    /**
     * 参数的域，用于表明参数的业务场景，例如orderSn等
     *
     * @return
     */
    String value() default "";
}