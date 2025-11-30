package com.demo.base.aspect;

import com.demo.base.util.annotation.LogRange;
import com.demo.base.util.annotation.Monitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@Log4j2
@Aspect
@Component
public class LogWrapper {
    private final ObjectMapper objectMapper;
    private final Map<Method, Monitor> annotationCache = new HashMap<>();

    private final AnnotationScanner annotationScanner;

    @Autowired
    public LogWrapper(ObjectMapper objectMapper, AnnotationScanner annotationScanner) {
        this.objectMapper = objectMapper;
        this.annotationScanner = annotationScanner;
    }

    @Pointcut("@annotation(com.demo.base.util.annotation.Monitor)")
    public void logAnnotatedMethod() {

    }

    @PostConstruct
    public void init() {
        Thread.ofVirtual().name("LogWrapperInit").unstarted(() -> {
            Map<Method, Monitor> monitorMap = annotationScanner.scanMethodAnnotation(Monitor.class);
            annotationCache.putAll(monitorMap);
        }).start();
    }

    @Around("logAnnotatedMethod()")
    public Object dynamicLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Monitor annotator = annotationCache.computeIfAbsent(method, m -> m.getAnnotation(Monitor.class));

        Set<LogRange> ranges = Arrays.stream(annotator.range()).collect(toSet());

        Level targetLevel = Level.toLevel(annotator.level().name());

        long startTime = 0;
        Object result = null;
        Throwable error = null;

        if (ranges.contains(LogRange.PRE) && log.isEnabled(targetLevel))
            log.atLevel(targetLevel).log("{} #PRE", () -> buildLogPrefix(signature, annotator, joinPoint));

        if (annotator.Timer())
            startTime = System.currentTimeMillis();

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {

            if (ranges.contains(LogRange.POST) || ranges.contains(LogRange.SUCCESS) || ranges.contains(LogRange.ERROR)) {

                if (log.isEnabled(targetLevel)) {
                    final long timerCost = annotator.Timer() ? (System.currentTimeMillis() - startTime) : 0;

                    Object finalResult = result;
                    Throwable finalError = error;
                    log.atLevel(targetLevel).log("{}", () -> buildFinalLogMessage(
                            signature, annotator, joinPoint, finalResult, finalError, timerCost, ranges
                    ));
                }
            }
        }
    }

    private String buildLogPrefix(MethodSignature signature, Monitor annotator, ProceedingJoinPoint joinPoint) {
        StringBuilder val = new StringBuilder();
        val.append(signature.getDeclaringTypeName()).append(".").append(signature.getName()).append("() >>>");

        if (annotator.Argument())
            val.append(" With Arguments ").append(safeSerializeArgs(joinPoint.getArgs()));

        return val.toString();
    }

    private String safeSerializeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            log.warn("Failed to serialize arguments for logging: {}", e.getMessage());
            return "[Serialization Error]";
        }
    }

    private String buildFinalLogMessage(
            MethodSignature signature, Monitor annotator, ProceedingJoinPoint joinPoint,
            Object result, Throwable error, long timerCost, Set<LogRange> ranges) {

        StringBuilder val = new StringBuilder(buildLogPrefix(signature, annotator, joinPoint));

        if (annotator.Timer())
            val.append(" Cost ").append(timerCost).append(" ms");

        if (error == null) {
            if (annotator.Result())
                val.append(" Return ").append(result != null ? result.toString() : "null");

            if (ranges.contains(LogRange.POST))
                val.append(" #POST");

            if (ranges.contains(LogRange.SUCCESS))
                val.append(" #SUCCESS");

        } else {
            val.append(" Error ").append(error.getMessage());

            if (ranges.contains(LogRange.ERROR))
                val.append(" #ERROR");
        }
        return val.toString();
    }
}