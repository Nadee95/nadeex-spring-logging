package com.nadeex.spring.logging.config;

import com.nadeex.spring.logging.aspect.LoggingAspect;
import com.nadeex.spring.logging.filter.CorrelationIdFilter;
import com.nadeex.spring.logging.filter.RequestLoggingFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;

/**
 * Spring Boot auto-configuration for the nadeex logging library.
 *
 * <p>Registers three components when the application is a servlet-based web app:</p>
 * <ol>
 *   <li>{@link CorrelationIdFilter} — populates MDC with correlation/tenant/user IDs</li>
 *   <li>{@link RequestLoggingFilter} — logs request entry and response summary</li>
 *   <li>{@link LoggingAspect} — logs method entry/exit for {@code @Loggable} methods</li>
 * </ol>
 *
 * <p>All three beans are {@code @ConditionalOnMissingBean} — consuming applications
 * can override any of them by declaring their own bean of the same type.</p>
 */
@AutoConfiguration
@EnableAspectJAutoProxy
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CorrelationIdFilter correlationIdFilter() {
        return new CorrelationIdFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistration(
            CorrelationIdFilter filter) {

        FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilterRegistration(
            RequestLoggingFilter filter) {

        FilterRegistrationBean<RequestLoggingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}

