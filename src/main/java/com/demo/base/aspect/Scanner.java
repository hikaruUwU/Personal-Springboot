package com.demo.base.aspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Component
public class Scanner {
    private final ApplicationContext applicationContext;


    @Autowired
    public Scanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Map<Class<?>, List<Field>> scanField(Class<? extends Annotation> type) {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);



        final Map<Class<?>, List<Field>> result = new HashMap<>();

        final String[] beans = applicationContext.getBeanDefinitionNames();

        for (String each : beans) {
            try {
                Class<?> beanClass = applicationContext.getType(each);
                if (beanClass == null) continue;

                Arrays.stream(beanClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(type)).forEach(field -> {
                    if (!result.containsKey(beanClass))
                        result.put(beanClass, new ArrayList<>());

                    result.get(beanClass).add(field);
                });

            } catch (Exception ignored) {
            }
        }
        return result;
    }

}
