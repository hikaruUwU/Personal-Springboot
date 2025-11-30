package com.demo.base.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {
    LogRange[] range() default {LogRange.POST};
    LogLevel level();
    boolean Timer() default true;
    boolean Argument() default true;
    boolean Result() default true;
}

