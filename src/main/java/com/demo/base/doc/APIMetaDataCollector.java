package com.demo.base.doc;

import jakarta.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class APIMetaDataCollector implements ApplicationListener<ContextRefreshedEvent> {
    private final Map<String, ApiMetadata> apiRegistry = new ConcurrentHashMap<>();

    public ServerResponse getApiRegistry(ServerRequest ignored) {
        return ServerResponse.ok().body(apiRegistry);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        Thread.ofVirtual().name("V-APICollector").unstarted(() -> {
            RequestMappingHandlerMapping handlerMapping = event.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
            handlerMapping.getHandlerMethods().forEach((mapping, handlerMethod) -> {
                if (handlerMethod.getBeanType().isAnnotationPresent(RestController.class)) {
                    Set<String> urls = mapping.getPatternValues();
                    Set<RequestMethod> methods = mapping.getMethodsCondition().getMethods();
                    urls.forEach(url -> methods.forEach(method -> {
                        ApiMetadata metadata = new ApiMetadata(
                                url,
                                method.name(),
                                handlerMethod.getBeanType().getName() + handlerMethod.getMethod().getName() + "()",
                                Arrays.stream(handlerMethod.getMethod().getParameters()).flatMap(parameter -> {
                                    try {
                                        if (parameter.getType().isPrimitive() || isWrapperType(parameter.getType()))
                                            return Stream.of(Map.of(parameter.getName(), ""));
                                        else
                                            return Stream.of(parameter.getType().newInstance());
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }).toList(),
                                Stream.of(Object.class).flatMap(ignored -> {
                                    try {
                                        return Stream.of(handlerMethod.getMethod().getReturnType().newInstance());
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }).findFirst().orElse(null),
                                Arrays.stream(handlerMethod.getMethod().getDeclaredAnnotations())
                                        .collect(Collectors.toMap(
                                                annotation -> annotation.annotationType().getSimpleName(),
                                                this::convertAnnotationToMap,
                                                (existing, replacement) -> existing
                                        ))
                        );

                        apiRegistry.put(url, metadata);
                    }));
                }
            });
            log.info("APIMetaDataCollection finished");
        }).start();
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Boolean.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(String.class);
    }

    private Map<String, Object> convertAnnotationToMap(Annotation annotation) {
        Map<String, Object> map = new HashMap<>();
        Class<? extends Annotation> annotationType = annotation.annotationType();
        for (Method method : annotationType.getDeclaredMethods())
            try {
                Object value = method.invoke(annotation);

                if (value instanceof Collection<?> collector)
                    if (!collector.isEmpty())
                        map.put(method.getName(), value);
                    else
                        continue;

                if (value.getClass().isArray())
                    if (Array.getLength(value) > 0)
                        map.put(method.getName(), value);
                    else
                        continue;

                map.put(method.getName(), value);
            } catch (Exception ignored) {
            }

        return map;
    }
}