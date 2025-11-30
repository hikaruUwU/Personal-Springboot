package com.demo.base.util;

import org.springframework.stereotype.Component;

@Component
public class RequestChain{
    public static final InheritableThreadLocal<Object> local = new InheritableThreadLocal<>();
}
