# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure
- Build configuration with Gradle Kotlin DSL
- CI/CD workflows with GitHub Actions
- `logback-spring.xml` with JSON and plain-text profiles

## [0.1.0] - TBD

### Added
- `@Loggable` annotation for method-level logging
- `@NoLog` annotation to skip logging
- `LoggingAspect` — AOP around advice for method entry/exit/error logging
- `PerformanceLoggingAspect` — logs execution time
- `RequestLoggingFilter` — logs HTTP request/response details
- `CorrelationIdFilter` — generates/propagates `X-Correlation-ID`
- `MdcKeys` — MDC key constants
- `MdcUtil` — MDC helper methods
- `LoggingAutoConfiguration` — Spring Boot auto-configuration
