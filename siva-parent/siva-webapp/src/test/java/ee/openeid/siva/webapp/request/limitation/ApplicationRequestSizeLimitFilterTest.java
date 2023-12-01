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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ApplicationRequestSizeLimitFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @ParameterizedTest
    @MethodSource("dataSizeProvider")
    void doFilterInternal_WhenApplicationRequestSizeLimitFilterIsSet_WrapperIsInjectedToFilterChain(DataSize maxRequestSizeLimit) throws Exception {
        HttpRequestLimitConfigurationProperties requestLimitProperties = new HttpRequestLimitConfigurationProperties();
        requestLimitProperties.setMaxRequestSizeLimit(maxRequestSizeLimit);
        ArgumentCaptor<SizeLimitingHttpServletRequest> argumentCaptor = ArgumentCaptor.forClass(SizeLimitingHttpServletRequest.class);

        ApplicationRequestSizeLimitFilter limitFilter = new ApplicationRequestSizeLimitFilter(requestLimitProperties);

        limitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(argumentCaptor.capture(), eq(response));

        SizeLimitingHttpServletRequest capturedRequest = argumentCaptor.getValue();

        assertEquals(maxRequestSizeLimit.toBytes(), capturedRequest.getMaximumAllowedReadLimit());
        verifyNoMoreInteractions(filterChain);
        verifyNoInteractions(request, response);
    }

    private static Stream<DataSize> dataSizeProvider() {
        return Stream.of(
                DataSize.ofBytes(1024),
                DataSize.ofKilobytes(10240),
                DataSize.ofMegabytes(1024)
        );
    }
}
