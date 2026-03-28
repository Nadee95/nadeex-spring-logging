package com.nadeex.spring.logging.aspect;

import com.nadeex.spring.logging.annotation.Loggable;
import com.nadeex.spring.logging.annotation.NoLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AOP aspect that intercepts methods annotated with {@link Loggable} (or inside a
 * {@link Loggable}-annotated class) and emits structured entry/exit/error log lines.
 *
 * <p>Methods annotated with {@link NoLog} are skipped even when the enclosing class
 * carries {@link Loggable}.</p>
 *
 * <p>Log level strategy:</p>
 * <ul>
 *   <li>Entry and exit → {@code DEBUG} (cheap, only visible when debug is enabled)</li>
 *   <li>Exception → {@code ERROR} with full stack trace, then re-thrown unchanged</li>
 * </ul>
 */
@Aspect
public class LoggingAspect {

    /**
     * Intercepts any method that is itself annotated {@code @Loggable} OR lives inside
     * a class annotated {@code @Loggable}.
     */
    @Around("@annotation(com.nadeex.spring.logging.annotation.Loggable) " +
            "|| @within(com.nadeex.spring.logging.annotation.Loggable)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Skip methods explicitly suppressed with @NoLog
        if (method.isAnnotationPresent(NoLog.class)) {
            return joinPoint.proceed();
        }

        // Resolve the @Loggable annotation — method-level takes priority over class-level
        Loggable loggable = method.getAnnotation(Loggable.class);
        if (loggable == null) {
            loggable = joinPoint.getTarget().getClass().getAnnotation(Loggable.class);
        }

        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = method.getName();

        logEntry(log, methodName, joinPoint.getArgs(), loggable);

        long start = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            logExit(log, methodName, result, durationMs, loggable);
            return result;
        } catch (Exception ex) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[{}] threw {} after {}ms: {}",
                    methodName,
                    ex.getClass().getSimpleName(),
                    durationMs,
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void logEntry(Logger log, String methodName, Object[] args, Loggable loggable) {
        if (!log.isDebugEnabled()) {
            return;
        }
        if (loggable.logArgs() && args != null && args.length > 0) {
            log.debug("[{}] → args: {}", methodName, Arrays.toString(args));
        } else {
            log.debug("[{}] →", methodName);
        }
    }

    private void logExit(Logger log, String methodName, Object result, long durationMs, Loggable loggable) {
        if (!log.isDebugEnabled()) {
            return;
        }
        if (loggable.logResult()) {
            log.debug("[{}] ← {}ms result: {}", methodName, durationMs, result);
        } else {
            log.debug("[{}] ← {}ms", methodName, durationMs);
        }
    }
}

