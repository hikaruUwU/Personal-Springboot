package com.demo.base.aspect;

import com.demo.base.util.annotation.Monitor;
import com.demo.base.util.annotation.LogRange;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Aspect
@Component
public class LogWrapper {
    @Pointcut("@annotation(com.demo.base.util.annotation.Monitor)")
    public void logAnnotatedMethod() {
    }

    @Around("logAnnotatedMethod()")
    public Object dynamicLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Monitor annotator = signature.getMethod().getAnnotation(Monitor.class);
        List<LogRange> effector = List.of(annotator.range());

        StringBuilder val = new StringBuilder(signature.getDeclaringTypeName() + "." + signature.getName() + "() >>>");

        if (annotator.Argument())
            val.append(" With Arguments ").append(Arrays.toString(joinPoint.getArgs()));

        if (effector.contains(LogRange.PRE))
            log.atLevel(Level.getLevel(annotator.level().name())).log("{} called #PRE", val);

        Object result = null;
        Throwable error = null;
        long timer = 0;

        if (annotator.Timer())
            timer = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            if (annotator.Timer())
                val.append(" Cost ").append(System.currentTimeMillis() - timer).append(" ms");

            if (annotator.Result())
                val.append(" Return ").append(result != null ? result.toString() : "null");

            if (error == null) {

                if (effector.contains(LogRange.POST))
                    log.atLevel(Level.getLevel(annotator.level().name())).log("{} #POST", val);

                if (effector.contains(LogRange.SUCCESS))
                    log.atLevel(Level.getLevel(annotator.level().name())).log("{} #SUCCESS", val);

            } else {

                val.append(" Error ").append(error.getMessage());

                if (effector.contains(LogRange.ERROR))
                    log.atLevel(Level.getLevel(annotator.level().name())).log("{} #ERROR", val);
            }
        }
    }
}