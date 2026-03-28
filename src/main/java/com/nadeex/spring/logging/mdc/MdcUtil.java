package com.nadeex.spring.logging.mdc;

import org.slf4j.MDC;

/**
 * Utility for safely reading and writing SLF4J MDC context values.
 *
 * <p>All methods are null-safe: passing {@code null} as a value is a no-op
 * rather than a NullPointerException, which keeps filter and aspect code clean.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * MdcUtil.setCorrelationId("abc-123");
 * MdcUtil.setTenantId("acme-law");
 * MdcUtil.setUserId("user-42");
 *
 * // always clear in a finally block
 * try {
 *     chain.doFilter(request, response);
 * } finally {
 *     MdcUtil.clear();
 * }
 * }</pre>
 */
public final class MdcUtil {

    private MdcUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Puts a value into MDC. Null values are silently ignored.
     *
     * @param key   MDC key (use {@link MdcKeys} constants)
     * @param value value to store; ignored if null or blank
     */
    public static void put(String key, String value) {
        if (key != null && value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }

    /**
     * Removes a single key from MDC.
     *
     * @param key MDC key to remove
     */
    public static void remove(String key) {
        if (key != null) {
            MDC.remove(key);
        }
    }

    /**
     * Clears all MDC entries. Call this in a {@code finally} block at the
     * outermost filter to prevent MDC leaking between requests on pooled threads.
     */
    public static void clear() {
        MDC.clear();
    }

    // -------------------------------------------------------------------------
    // Named convenience setters
    // -------------------------------------------------------------------------

    /** Sets the {@code correlationId} MDC key. */
    public static void setCorrelationId(String correlationId) {
        put(MdcKeys.CORRELATION_ID, correlationId);
    }

    /** Sets the {@code tenantId} MDC key. */
    public static void setTenantId(String tenantId) {
        put(MdcKeys.TENANT_ID, tenantId);
    }

    /** Sets the {@code userId} MDC key. */
    public static void setUserId(String userId) {
        put(MdcKeys.USER_ID, userId);
    }

    /** Sets the {@code requestId} MDC key. */
    public static void setRequestId(String requestId) {
        put(MdcKeys.REQUEST_ID, requestId);
    }

    // -------------------------------------------------------------------------
    // Named convenience getters
    // -------------------------------------------------------------------------

    /** Returns the current {@code correlationId} from MDC, or {@code null} if not set. */
    public static String getCorrelationId() {
        return MDC.get(MdcKeys.CORRELATION_ID);
    }

    /** Returns the current {@code tenantId} from MDC, or {@code null} if not set. */
    public static String getTenantId() {
        return MDC.get(MdcKeys.TENANT_ID);
    }

    /** Returns the current {@code userId} from MDC, or {@code null} if not set. */
    public static String getUserId() {
        return MDC.get(MdcKeys.USER_ID);
    }
}

