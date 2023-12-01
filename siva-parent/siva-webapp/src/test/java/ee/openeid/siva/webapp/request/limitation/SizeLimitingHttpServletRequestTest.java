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

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class SizeLimitingHttpServletRequestTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L})
    void getInputStream_WhenLimitIsBiggerThanContentLength_ReturnsServletInputStream(long contentLengthLong) throws Exception {
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        doReturn(contentLengthLong).when(httpServletRequest).getContentLengthLong();
        doReturn(servletInputStream).when(httpServletRequest).getInputStream();

        SizeLimitingHttpServletRequest limitingHttpServletRequest = new SizeLimitingHttpServletRequest(
                httpServletRequest, 1L);

        ServletInputStream result = limitingHttpServletRequest.getInputStream();

        assertEquals(SizeLimitingServletInputStream.class, result.getClass());
        verifyNoMoreInteractions(httpServletRequest);
        verifyNoInteractions(servletInputStream);
    }

    @ParameterizedTest
    @ValueSource(longs = {2L, 3L, Long.MAX_VALUE})
    void getInputStream_WhenLimitIsSmallerThanContentLength_ThrowsException(long contentLengthLong) {
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        doReturn(contentLengthLong).when(httpServletRequest).getContentLengthLong();

        SizeLimitingHttpServletRequest limitingHttpServletRequest = new SizeLimitingHttpServletRequest(
                httpServletRequest, 1L);

        RequestSizeLimitExceededException caughtException = assertThrows(
                RequestSizeLimitExceededException.class,
                limitingHttpServletRequest::getInputStream
        );

        assertEquals("Request content-length (" + contentLengthLong + " bytes) exceeds request size limit (1 bytes)",
                caughtException.getMessage());
        verifyNoMoreInteractions(httpServletRequest);
        verifyNoInteractions(servletInputStream);
    }
}
