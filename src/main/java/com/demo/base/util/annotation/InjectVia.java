package com.demo.base.util.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectVia {
    String key() default "";

    Class<? extends ResourceFinder<?>> resource() default NoInstancePlaceHolderResourceFinder.class;

    InjectType via();
}
