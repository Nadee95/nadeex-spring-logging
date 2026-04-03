# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-04-03

### Added
- `@Loggable` — method-level annotation to enable automatic entry/exit/error logging via AOP
- `@NoLog` — method-level annotation to suppress logging on specific methods
- `LoggingAspect` — AOP `@Around` advice; logs method name, arguments, return value, execution time, and exceptions
- `CorrelationIdFilter` — servlet filter that reads or generates `X-Correlation-ID` and stores it in MDC for log correlation
- `RequestLoggingFilter` — servlet filter that logs HTTP method, URI, query string, status code, and response time
- `MdcKeys` — constants for MDC key names (`correlationId`, `tenantId`, `userId`, `requestPath`, `requestMethod`)
- `MdcUtil` — helper methods for type-safe MDC `put`, `get`, `remove`, and `clear`
- `LoggingAutoConfiguration` — Spring Boot auto-configuration; registers filters and aspect as beans automatically
- `logback-spring.xml` — JSON (logstash-logback-encoder) profile for structured log output; plain-text profile for local development
- Depends on `nadeex-spring-common:0.1.0` for `CommonConstants` header name constants
- Gradle Kotlin DSL build with `java-library` + `maven-publish` plugins
- Local Maven and GitHub Packages publishing
- JaCoCo test coverage reporting
- GitHub Actions CI and publish workflows
