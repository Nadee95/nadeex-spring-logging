package com.nadeex.spring.logging.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Suppresses automatic logging for a specific method inside a {@link Loggable}-annotated class.
 *
 * <p>Has no effect when the class is not annotated with {@link Loggable}.</p>
 *
 * <pre>{@code
 * @Loggable
 * @Service
 * public class UserService {
 *
 *     public UserDto findById(String id) { ... }   // logged
 *
 *     @NoLog  // sensitive — password hash is an argument
 *     public void changePassword(String userId, String rawPassword) { ... }
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLog {
}

