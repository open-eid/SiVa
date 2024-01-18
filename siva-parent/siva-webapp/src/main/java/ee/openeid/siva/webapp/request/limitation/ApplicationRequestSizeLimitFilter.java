/*
 * Copyright 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.request.limitation;

import ee.openeid.siva.webapp.configuration.HttpRequestLimitConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ConditionalOnProperty(prefix = "siva.http.request", name = "max-request-size-limit")
public class ApplicationRequestSizeLimitFilter extends OncePerRequestFilter {

    private final long maxRequestSizeLimit;

    public ApplicationRequestSizeLimitFilter(HttpRequestLimitConfigurationProperties requestLimitProperties) {
        maxRequestSizeLimit = requestLimitProperties.getMaxRequestSizeLimit().toBytes();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SizeLimitingHttpServletRequest sizeLimitingHttpServletRequest = new SizeLimitingHttpServletRequest(request, maxRequestSizeLimit);
        filterChain.doFilter(sizeLimitingHttpServletRequest, response);
    }
}
