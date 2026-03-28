package com.nadeex.spring.logging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that logs a one-line summary for every inbound HTTP request
 * and its corresponding response.
 *
 * <p>Execution order: {@link Ordered#HIGHEST_PRECEDENCE}{@code + 1} — runs immediately
 * after {@link CorrelationIdFilter} so the correlation ID is already in MDC when the
 * entry/exit lines are written.</p>
 *
 * <p>Log format:</p>
 * <pre>
 * →  GET  /api/v1/users/42
 * ←  200  GET  /api/v1/users/42  (14ms)
 * </pre>
 *
 * <p>Both lines are written at {@code INFO} level. The response line is written in a
 * {@code finally} block so it always appears even when the handler throws.</p>
 */
public class RequestLoggingFilter extends OncePerRequestFilter implements Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        long start = System.currentTimeMillis();

        log.info("→  {}  {}", method, uri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            log.info("←  {}  {}  {} ({}ms)", response.getStatus(), method, uri, durationMs);
        }
    }
}

