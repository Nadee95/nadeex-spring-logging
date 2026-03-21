# Contributing to Nadeex Spring Logging

Thank you for contributing!

## Commit Message Format

- `feat(aspect): Add performance threshold alerting to LoggingAspect`
- `fix(filter): Fix correlation ID not propagated to async threads`

## Code Style

- Use Lombok
- Thread-safe implementations (MDC is thread-local, be careful with async)
- 80%+ test coverage

## Testing

```bash
./gradlew test
```
