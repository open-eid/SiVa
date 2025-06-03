/*
 * Copyright 2024 - 2025 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.webapp.request.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TraceIdLoggingFilterTest {

    @InjectMocks
    private TraceIdLoggingFilter filter;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;

    @ParameterizedTest
    @ValueSource(strings = {"a", "abcd012345", " "})
    void doFilterInternal_WhenGivenTraceIdInMdc_RequestIdIsStoredAsRequestAttribute(String correlationId) throws Exception {
        try (MDC.MDCCloseable traceIdScope = MDC.putCloseable("trace.id", correlationId)) {
            filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        }

        verify(httpServletRequest).setAttribute("requestId", correlationId);
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        verifyNoMoreInteractions(httpServletRequest, filterChain);
        verifyNoInteractions(httpServletResponse);
    }

    @Test
    void doFilterInternal_WhenEmptyTraceIdInMdc_RequestIdIsGeneratedAndStoredAsRequestAttribute() throws Exception {
        try (MDC.MDCCloseable traceIdScope = MDC.putCloseable("trace.id", "")) {
            filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        }

        verify(httpServletRequest).setAttribute(eq("requestId"), anyString());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        verifyNoMoreInteractions(httpServletRequest, filterChain);
        verifyNoInteractions(httpServletResponse);
    }

    @Test
    void doFilterInternal_WhenNoCorrelationIdInMdc_RequestIdIsGeneratedAndStoredAsRequestAttribute() throws Exception {
        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(httpServletRequest).setAttribute(eq("requestId"), anyString());
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        verifyNoMoreInteractions(httpServletRequest, filterChain);
        verifyNoInteractions(httpServletResponse);
    }

}
