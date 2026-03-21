# Implementation Guide — nadeex-spring-logging

Implement the following classes using IntelliJ IDEA + GitHub Copilot.

**Prerequisite**: `nadeex-spring-common` must be published (v0.1.0) as this library uses `CommonConstants`.

---

## Package: `annotation`

### `@Loggable`
Custom annotation for method/class-level logging.

```
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Loggable {
    boolean logArgs() default true;
    boolean logReturn() default false;
    String level() default "INFO";
}
```

### `@NoLog`
Marks methods to skip from logging (e.g. containing passwords).

```
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLog {}
```

---

## Package: `aspect`

### `LoggingAspect`
AOP aspect using `@Around` advice on methods annotated with `@Loggable` or in classes annotated with `@Loggable`.

**Behavior**:
- On entry: log method name + args (if `logArgs=true`)
- On exit: log method name + return value (if `logReturn=true`) + execution time in ms
- On exception: log method name + exception message at ERROR level, then rethrow
- Skip if method has `@NoLog`

Use `ProceedingJoinPoint`. Use `SLF4J` logger (`LoggerFactory.getLogger(targetClass)`).

---

### `PerformanceLoggingAspect`
AOP aspect measuring execution time for all `@Loggable` methods.

**Behavior**:
- Measure execution time in milliseconds
- If > configured threshold (default 1000ms): log WARN with method name + duration
- Configurable via `@ConfigurationProperties(prefix = "nadeex.logging")`

---

## Package: `filter`

### `CorrelationIdFilter`
`OncePerRequestFilter`. Must run before other filters (order = `Ordered.HIGHEST_PRECEDENCE`).

**Behavior**:
1. Check if `X-Correlation-ID` header present; if not, generate a UUID
2. Put in MDC as `correlationId` (key from `MdcKeys.CORRELATION_ID`)
3. Set `X-Correlation-ID` in response header
4. Extract `X-Tenant-ID` header → put in MDC as `tenantId`
5. Extract `X-User-ID` header → put in MDC as `userId`
6. Call `filterChain.doFilter()`
7. **Always** clear MDC in `finally` block

---

### `RequestLoggingFilter`
`OncePerRequestFilter`. Logs HTTP requests and responses.

**Behavior**:
1. Record start time
2. Log: `→ {METHOD} {URI} from {remoteAddr}`
3. Proceed with filter chain
4. Log: `← {METHOD} {URI} {statusCode} ({durationMs}ms)`

Conditional: only active if `nadeex.logging.request-logging=true`.

---

## Package: `mdc`

### `MdcKeys` (final utility class)
Constants for MDC keys:
- `CORRELATION_ID = "correlationId"`
- `TENANT_ID = "tenantId"`
- `USER_ID = "userId"`
- `REQUEST_ID = "requestId"`

### `MdcUtil` (final utility class)
Helper methods:
- `static void put(String key, String value)` — `MDC.put(key, value)` (null-safe)
- `static String get(String key)` — `MDC.get(key)`
- `static void remove(String key)` — `MDC.remove(key)`
- `static void clear()` — `MDC.clear()`
- `static String getOrGenerate(String key)` — returns existing value or generates UUID

---

## Package: `config`

### `LoggingProperties`
`@ConfigurationProperties(prefix = "nadeex.logging")`:
- `boolean requestLogging = true`
- `boolean responseBodyLogging = false`
- `long slowMethodThresholdMs = 1000`
- `boolean logMethodArgs = true`
- `boolean logReturnValue = false`

### `LoggingAutoConfiguration`
`@Configuration @EnableConfigurationProperties(LoggingProperties.class)`

Registers as beans:
- `CorrelationIdFilter` — always
- `RequestLoggingFilter` — conditional on `nadeex.logging.request-logging=true`
- `LoggingAspect` — conditional on `@ConditionalOnClass(ProceedingJoinPoint.class)`
- `PerformanceLoggingAspect` — conditional on AOP being present

---

## Testing

- Test `CorrelationIdFilter` with `MockHttpServletRequest` — verify MDC is set and cleared
- Test `LoggingAspect` with a test Spring context — verify log output
- Test `MdcUtil` methods directly
- Target 80%+ coverage

---

## Important: MDC Thread Safety

MDC is thread-local. When using `@Async`, virtual threads, or Kafka listeners, you must copy the MDC context manually. Add a note in documentation about this.
