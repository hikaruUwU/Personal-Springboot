package com.demo.base.util.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    String key() default "COMMON";
    long permits() default 10;
    Reject value() default Reject.SUSPEND_THREAD;
}
