package com.nadeex.spring.logging;

import com.nadeex.spring.common.constants.CommonConstants;
import com.nadeex.spring.logging.annotation.Loggable;
import com.nadeex.spring.logging.annotation.NoLog;
import com.nadeex.spring.logging.config.LoggingAutoConfiguration;
import com.nadeex.spring.logging.mdc.MdcKeys;
import com.nadeex.spring.logging.mdc.MdcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the nadeex-spring-logging library components.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(LoggingAutoConfiguration.class)
@DisplayName("nadeex-spring-logging")
class LoggingTest {

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clearMdc() {
        MdcUtil.clear();
    }

    // -------------------------------------------------------------------------
    // Stub wiring — controller + service used across tests
    // -------------------------------------------------------------------------

    @RestController
    @RequestMapping("/test-logging")
    static class StubController {

        private final StubService stubService;

        StubController(StubService stubService) {
            this.stubService = stubService;
        }

        @GetMapping("/hello")
        public String hello() {
            return stubService.greet("world");
        }

        @GetMapping("/no-log")
        public String noLog() {
            return stubService.sensitiveOp();
        }
    }

    @Service
    @Loggable
    static class StubService {

        public String greet(String name) {
            return "Hello, " + name;
        }

        @NoLog
        public String sensitiveOp() {
            return "secret";
        }
    }

    // -------------------------------------------------------------------------
    // CorrelationIdFilter
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("CorrelationIdFilter")
    class CorrelationIdFilterTests {

        @Test
        @DisplayName("generates X-Correlation-ID when not provided")
        void shouldGenerateCorrelationIdWhenAbsent() throws Exception {
            mockMvc.perform(get("/test-logging/hello"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(CommonConstants.HEADER_CORRELATION_ID));
        }

        @Test
        @DisplayName("echoes the client-provided X-Correlation-ID")
        void shouldEchoClientCorrelationId() throws Exception {
            String clientId = "test-correlation-id-123";

            mockMvc.perform(get("/test-logging/hello")
                            .header(CommonConstants.HEADER_CORRELATION_ID, clientId))
                    .andExpect(status().isOk())
                    .andExpect(header().string(CommonConstants.HEADER_CORRELATION_ID, clientId));
        }

        @Test
        @DisplayName("MDC is cleared after the request completes")
        void shouldClearMdcAfterRequest() throws Exception {
            mockMvc.perform(get("/test-logging/hello"))
                    .andExpect(status().isOk());

            // MDC must be empty on the test thread after the request lifecycle ends
            assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // MdcUtil unit tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("MdcUtil")
    class MdcUtilTests {

        @Test
        @DisplayName("setCorrelationId stores value under correct key")
        void shouldSetCorrelationId() {
            MdcUtil.setCorrelationId("abc-123");
            assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isEqualTo("abc-123");
        }

        @Test
        @DisplayName("setTenantId stores value under correct key")
        void shouldSetTenantId() {
            MdcUtil.setTenantId("acme-law");
            assertThat(MDC.get(MdcKeys.TENANT_ID)).isEqualTo("acme-law");
        }

        @Test
        @DisplayName("setUserId stores value under correct key")
        void shouldSetUserId() {
            MdcUtil.setUserId("user-42");
            assertThat(MDC.get(MdcKeys.USER_ID)).isEqualTo("user-42");
        }

        @Test
        @DisplayName("null value is ignored — does not throw")
        void shouldIgnoreNullValue() {
            MdcUtil.setCorrelationId(null);
            assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isNull();
        }

        @Test
        @DisplayName("blank value is ignored — does not throw")
        void shouldIgnoreBlankValue() {
            MdcUtil.setCorrelationId("   ");
            assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isNull();
        }

        @Test
        @DisplayName("clear removes all MDC entries")
        void shouldClearAllEntries() {
            MdcUtil.setCorrelationId("abc");
            MdcUtil.setTenantId("tenant");
            MdcUtil.setUserId("user");

            MdcUtil.clear();

            assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isNull();
            assertThat(MDC.get(MdcKeys.TENANT_ID)).isNull();
            assertThat(MDC.get(MdcKeys.USER_ID)).isNull();
        }

        @Test
        @DisplayName("remove deletes a single key")
        void shouldRemoveSingleKey() {
            MdcUtil.setCorrelationId("abc");
            MdcUtil.setTenantId("tenant");

            MdcUtil.remove(MdcKeys.CORRELATION_ID);

            assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isNull();
            assertThat(MDC.get(MdcKeys.TENANT_ID)).isEqualTo("tenant");
        }

        @Test
        @DisplayName("getCorrelationId returns stored value")
        void shouldGetCorrelationId() {
            MdcUtil.setCorrelationId("xyz");
            assertThat(MdcUtil.getCorrelationId()).isEqualTo("xyz");
        }
    }

    // -------------------------------------------------------------------------
    // RequestLoggingFilter
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("RequestLoggingFilter")
    class RequestLoggingFilterTests {

        @Test
        @DisplayName("request passes through without errors")
        void shouldPassThroughSuccessfully() throws Exception {
            mockMvc.perform(get("/test-logging/hello"))
                    .andExpect(status().isOk());
        }
    }
}

