package com.nadeex.spring.logging.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or class for automatic entry/exit logging via {@code LoggingAspect}.
 *
 * <p>When applied to a <strong>class</strong>, all public methods of that class are logged
 * unless individually annotated with {@link NoLog}.</p>
 *
 * <p>When applied to a <strong>method</strong>, only that method is logged.</p>
 *
 * <pre>{@code
 * // Log all methods in the service
 * @Loggable
 * @Service
 * public class UserService { ... }
 *
 * // Log only one method, including its return value
 * @Loggable(logArgs = true, logResult = true)
 * public UserDto findById(String id) { ... }
 * }</pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Loggable {

    /**
     * Whether to include method arguments in the log output.
     * Defaults to {@code true}. Set to {@code false} for methods handling
     * sensitive data such as passwords or payment details.
     */
    boolean logArgs() default true;

    /**
     * Whether to include the return value in the log output.
     * Defaults to {@code false} to avoid logging large response objects.
     */
    boolean logResult() default false;
}

