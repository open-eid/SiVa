/*
 * Copyright 2024 - 2026 Riigi Infosüsteemi Amet
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// One id per request for its log lines and its response. An id an agent already put in the MDC wins (Elastic APM
// sets trace.id itself), then an incoming X-Request-ID (the edge sets it last and is the one that logs it), then
// the trace-id of an incoming W3C traceparent; without any, one is minted in the APM format, which is a valid W3C
// trace-id. The response always carries the id as X-Request-ID, so a client can quote it.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure that logging attributes are set as early as possible.
public class TraceIdLoggingFilter extends OncePerRequestFilter {

    public static final String TRACEPARENT_HEADER = "traceparent";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";

    private static final String REQUEST_ATTRIBUTE_NAME_REQUEST_ID = "requestId";
    private static final String TRACE_ID = "trace.id";
    private static final String HEX = "0123456789abcdef";
    // version-traceid-parentid-flags; version 00 has exactly these four fields, a later version may append
    // fields, which are ignored, as the specification asks; version ff is reserved and invalid, as are an all-zero
    // trace-id and an all-zero parent-id.
    private static final Pattern TRACEPARENT = Pattern.compile(
            "^(?<version>(?!ff)[0-9a-f]{2})-(?<traceId>[0-9a-f]{32})-(?<parentId>[0-9a-f]{16})-[0-9a-f]{2}"
                    + "(?<extra>-.*)?$");
    private static final String ALL_ZEROS_TRACE_ID = "0".repeat(32);
    private static final String ALL_ZEROS_PARENT_ID = "0".repeat(16);
    // What goes into the log and back onto the wire: characters common ids are made of, no whitespace or
    // control characters, no more than 64 of them. Anything else is ignored and an id is minted instead.
    private static final Pattern REQUEST_ID = Pattern.compile("^[A-Za-z0-9._:/+=-]{1,64}$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestTraceId = MDC.get(TRACE_ID);
        boolean traceIdExists = StringUtils.isNotEmpty(requestTraceId);
        if (!traceIdExists) {
            requestTraceId = incomingTraceId(request)
                    // Use same format as Elastic APM Agent.
                    .orElseGet(() -> RandomStringUtils.random(32, HEX));
        }

        // Set traceId also as HttpServletRequest attribute to make it accessible for Tomcat's AccessLogValve.
        request.setAttribute(REQUEST_ATTRIBUTE_NAME_REQUEST_ID, requestTraceId);
        response.setHeader(REQUEST_ID_HEADER, requestTraceId);

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

    private static Optional<String> incomingTraceId(HttpServletRequest request) {
        return requestId(request.getHeader(REQUEST_ID_HEADER))
                .or(() -> traceparentTraceId(request.getHeader(TRACEPARENT_HEADER)));
    }

    private static Optional<String> traceparentTraceId(String traceparent) {
        if (traceparent == null) {
            return Optional.empty();
        }
        Matcher matcher = TRACEPARENT.matcher(traceparent.trim());
        if (!matcher.matches()
                || "00".equals(matcher.group("version")) && matcher.group("extra") != null
                || ALL_ZEROS_TRACE_ID.equals(matcher.group("traceId"))
                || ALL_ZEROS_PARENT_ID.equals(matcher.group("parentId"))) {
            return Optional.empty();
        }
        return Optional.of(matcher.group("traceId"));
    }

    private static Optional<String> requestId(String requestId) {
        if (requestId == null) {
            return Optional.empty();
        }
        String trimmed = requestId.trim();
        return REQUEST_ID.matcher(trimmed).matches() ? Optional.of(trimmed) : Optional.empty();
    }

}
