package com.demo.base.util.annotation;

import jakarta.annotation.Nullable;

import java.io.Serializable;

public interface ResourceFinder<T> {
    @Nullable T getEntity(@Nullable final Serializable id);
}
