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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure that logging attributes are set as early as possible.
public class TraceIdLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ATTRIBUTE_NAME_REQUEST_ID = "requestId";
    private static final String TRACE_ID = "trace.id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestTraceId = MDC.get(TRACE_ID);
        boolean traceIdExists = StringUtils.isNotEmpty(requestTraceId);
        if (!traceIdExists) {
            // Use same format as Elastic APM Agent.
            requestTraceId = RandomStringUtils.random(32, "0123456789abcdef");
        }

        // Set traceId also as HttpServletRequest attribute to make it accessible for Tomcat's AccessLogValve.
        request.setAttribute(REQUEST_ATTRIBUTE_NAME_REQUEST_ID, requestTraceId);

        if (!traceIdExists) {
            MDC.put(TRACE_ID, requestTraceId);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (!traceIdExists) {
                MDC.remove(TRACE_ID);
            }
        }
    }

}
