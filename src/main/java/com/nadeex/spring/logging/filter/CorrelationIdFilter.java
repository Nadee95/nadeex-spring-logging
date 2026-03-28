package com.nadeex.spring.logging.filter;

import com.nadeex.spring.common.constants.CommonConstants;
import com.nadeex.spring.logging.mdc.MdcUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that ensures every request carries a correlation ID in MDC and
 * in the response header.
 *
 * <p>Execution order: {@link Ordered#HIGHEST_PRECEDENCE} — runs before every
 * other filter so that all downstream log lines include the correlation ID.</p>
 *
 * <p>Logic per request:</p>
 * <ol>
 *   <li>Read {@code X-Correlation-ID} from the request; generate a UUID if absent.</li>
 *   <li>Read {@code X-Tenant-ID} and {@code X-User-ID} and set them into MDC.</li>
 *   <li>Write all MDC keys via {@link MdcUtil}.</li>
 *   <li>Add {@code X-Correlation-ID} to the response header.</li>
 *   <li>Proceed with the filter chain.</li>
 *   <li>Clear MDC in a {@code finally} block to prevent context leaking to the
 *       next request on pooled threads.</li>
 * </ol>
 */
public class CorrelationIdFilter extends OncePerRequestFilter implements Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String correlationId = resolveCorrelationId(request);
            String tenantId = request.getHeader(CommonConstants.HEADER_TENANT_ID);
            String userId = request.getHeader(CommonConstants.HEADER_USER_ID);

            MdcUtil.setCorrelationId(correlationId);
            MdcUtil.setTenantId(tenantId);
            MdcUtil.setUserId(userId);

            // Echo the correlation ID back so clients can trace responses
            response.setHeader(CommonConstants.HEADER_CORRELATION_ID, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            // Always clear MDC — prevents data leaking to the next request on the same thread
            MdcUtil.clear();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String resolveCorrelationId(HttpServletRequest request) {
        String incomingId = request.getHeader(CommonConstants.HEADER_CORRELATION_ID);
        if (incomingId != null && !incomingId.isBlank()) {
            return incomingId;
        }
        return UUID.randomUUID().toString();
    }
}

