package com.nadeex.spring.logging.mdc;

/**
 * Constants for MDC (Mapped Diagnostic Context) keys used across the logging library.
 *
 * <p>These keys match exactly what is declared in {@code logback-spring.xml} so that
 * every MDC value is automatically included in structured JSON log output.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * MDC.put(MdcKeys.CORRELATION_ID, "abc-123");
 * // or via MdcUtil:
 * MdcUtil.setCorrelationId("abc-123");
 * }</pre>
 */
public final class MdcKeys {

    private MdcKeys() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** Unique ID that ties together all log lines for a single inbound request. */
    public static final String CORRELATION_ID = "correlationId";

    /** The tenant identifier propagated from the JWT / request header. */
    public static final String TENANT_ID = "tenantId";

    /** The authenticated user identifier propagated from the JWT / request header. */
    public static final String USER_ID = "userId";

    /** Internal request ID generated per dispatch (may differ from correlationId on retries). */
    public static final String REQUEST_ID = "requestId";

    /** HTTP method of the current request (GET, POST, …). */
    public static final String METHOD = "method";

    /** Request URI of the current request. */
    public static final String URI = "uri";

    /** Elapsed time in milliseconds for the current operation. */
    public static final String DURATION_MS = "durationMs";
}

