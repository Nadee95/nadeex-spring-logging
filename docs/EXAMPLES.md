# Examples — nadeex-spring-logging

## Zero Configuration

```yaml
# application.yml — optional, shown with defaults
nadeex:
  logging:
    request-logging: true
    slow-method-threshold-ms: 1000
    log-method-args: true
    log-return-value: false
```

## @Loggable on a Service

```java
@Service
@Loggable
public class CaseService {

    public CaseDto createCase(CreateCaseRequest request) {
        // Logs: "Entering createCase with args: [...]"
        // Logs: "Exiting createCase in 45ms"
        return caseRepository.save(mapper.toEntity(request));
    }

    @NoLog  // Don't log this
    public void updateConfidentialNotes(String caseId, String notes) {
        // ...
    }
}
```

## Correlation ID in Logs

Every request automatically gets a correlation ID:

```
→ POST /api/v1/cases from 10.0.0.1
{
  "timestamp": "2026-03-20T10:30:00.000Z",
  "level": "INFO",
  "correlationId": "a1b2c3d4-...",
  "tenantId": "acme-law",
  "message": "Entering createCase with args: [...]"
}
← POST /api/v1/cases 201 (123ms)
```

## Manual MDC Usage

```java
MdcUtil.put(MdcKeys.USER_ID, authenticatedUserId);

try {
    // All log statements in this scope include userId
    log.info("Processing case assignment");
} finally {
    MdcUtil.remove(MdcKeys.USER_ID);
}
```

## Propagating MDC to Async Threads

```java
// When using @Async or CompletableFuture
Map<String, String> mdcContext = MDC.getCopyOfContextMap();

CompletableFuture.runAsync(() -> {
    MDC.setContextMap(mdcContext);
    try {
        // MDC now available in async thread
        processAsync();
    } finally {
        MDC.clear();
    }
});
```

## Dev Profile — Readable Logs

With `spring.profiles.active=dev`, logs are plain text (not JSON):
```
2026-03-20 10:30:00.123 [http-nio-8080-exec-1] INFO  [a1b2c3d4] [acme-law] c.l.u.service.CaseService - Entering createCase
```

## Production Profile — JSON Logs

Without `dev` profile, logs are structured JSON (ready for ELK/Loki ingestion).
