package com.demo.base.aspect;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Log4j2
@Component
public class AnnotationScanner {
    private final ApplicationContext applicationContext;

    @Autowired
    public AnnotationScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private String getBasementPack() {
        Map<String, Object> candidates = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        return candidates.values().toArray()[0].getClass().getPackage().getName();
    }


    private Set<BeanDefinition> scan(String loc) {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(Object.class));
        return provider.findCandidateComponents(loc);
    }

    public Map<Class<?>, List<Field>> scanFieldAnnotation(Class<? extends Annotation> type) {
        Set<BeanDefinition> candidates = scan(getBasementPack());
        Map<Class<?>, List<Field>> result = new HashMap<>();

        candidates.forEach(c -> {
            try {
                Class<?> target = ClassUtils.forName(Objects.requireNonNull(c.getBeanClassName()), Thread.currentThread().getContextClassLoader());
                ReflectionUtils.doWithFields(target, field -> {
                    if (field.isAnnotationPresent(type))
                        result.computeIfAbsent(target, k -> new ArrayList<>()).add(field);
                });
            } catch (ClassNotFoundException ignored) {
            }
        });
        log.info("scanned FieldAnnotation, {} record(s) founded", result.size());

        return result;
    }

    public <T extends Annotation> Map<Method, T> scanMethodAnnotation(Class<T> type) {
        Set<BeanDefinition> candidates = scan(getBasementPack());
        Map<Method, T> resultMap = new HashMap<>();

        candidates.forEach(c -> {
            try {
                Class<?> target = ClassUtils.forName(Objects.requireNonNull(c.getBeanClassName()), Thread.currentThread().getContextClassLoader());

                ReflectionUtils.doWithMethods(target, method -> {
                    T annotation = method.getAnnotation(type);

                    if (annotation != null)
                        resultMap.put(method, annotation);
                });
            } catch (ClassNotFoundException ignored) {
            }
        });
        log.info("scanned MethodAnnotation, {} record(s) founded", resultMap.size());
        return resultMap;
    }
}