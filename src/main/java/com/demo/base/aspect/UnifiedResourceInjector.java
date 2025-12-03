package com.demo.base.aspect;

import com.demo.base.util.annotation.InjectKey;
import com.demo.base.util.annotation.InjectVia;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.util.SoftHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class UnifiedResourceInjector {
    private final ApplicationContext applicationContext;
    private static final Map<Method, Object[]> REFLECT_CACHE = new SoftHashMap<>();
    private static final Map<Class<?>, Field> INJECT_KEY_CACHE = new SoftHashMap<>();

    private final AnnotationScanner annotationScanner;

    @Autowired
    public UnifiedResourceInjector(ApplicationContext applicationContext, AnnotationScanner annotationScanner) {
        this.applicationContext = applicationContext;
        this.annotationScanner = annotationScanner;
    }

    @PostConstruct
    public void init() {
        Thread.ofVirtual().name("V-@InjectInit").unstarted(() -> {
            Map<Class<?>, List<Field>> classListMap = annotationScanner.scanFieldAnnotation(InjectKey.class);
            classListMap.forEach((key, value) -> INJECT_KEY_CACHE.put(key, value.getFirst()));
        }).start();
    }

    @Around("execution(* *.*(.., @com.demo.base.util.annotation.InjectVia (*), ..))")
    public Object resourceInject(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Object[] cachedData = REFLECT_CACHE.computeIfAbsent(method, m -> {
            Class<?>[] pTypes = m.getParameterTypes();
            Annotation[][] pAnnotations = m.getParameterAnnotations();
            return new Object[]{pAnnotations, pTypes};
        });

        Annotation[][] parameterAnnotations = (Annotation[][]) cachedData[0];
        Class<?>[] parameterTypes = (Class<?>[]) cachedData[1];

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();


        Object[] args = pjp.getArgs().clone();

        for (int i = 0; i < args.length; i++) {
            InjectVia viaSource = null;

            for (Annotation annotation : parameterAnnotations[i])
                if (annotation instanceof InjectVia) {
                    viaSource = (InjectVia) annotation;
                    break;
                }

            if (viaSource == null) continue;

            Object injectedValue = switch (viaSource.via()) {
                case SESSION ->
                        (attributes != null) ? attributes.getRequest().getSession(true).getAttribute(viaSource.key()) : null;
                case NULL_INSTANCE -> null;
                case APPLICATION_CONTEXT -> applicationContext.getBean(viaSource.key());
                case REPOSITORY -> performRepositoryInjection(args[i], viaSource, parameterTypes[i]);
            };

            if (injectedValue != args[i])
                args[i] = injectedValue;
        }

        return pjp.proceed(args);
    }

    private Object performRepositoryInjection(Object entityInstance, InjectVia viaSource, Class<?> targetType) throws IllegalAccessException {
        if (entityInstance == null) return null;

        Class<?> entityClass = entityInstance.getClass();

        Field keyField = INJECT_KEY_CACHE.computeIfAbsent(entityClass, c -> {
            for (Field field : c.getDeclaredFields())
                if (field.isAnnotationPresent(InjectKey.class))
                    return field;
            return null;
        });

        if (keyField == null) return entityInstance;

        keyField.setAccessible(true);

        Object rawId = keyField.get(entityInstance);

        if (!(rawId instanceof Serializable inputId)) return entityInstance;

        Object freshEntity = applicationContext.getBean(viaSource.resource()).getEntity(inputId);

        if (freshEntity == null) return entityInstance;

        if (targetType.isAssignableFrom(freshEntity.getClass()))
            return freshEntity;

        return entityInstance;
    }
}