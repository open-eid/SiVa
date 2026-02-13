/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.interceptor;

import ee.openeid.siva.webapp.contentdisposition.WithContentDisposition;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import static ee.openeid.siva.webapp.contentdisposition.ContentDispositionConstants.CONTENT_DISPOSITION_FILENAME_ATTR;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ContentDispositionInterceptorTest {

    @InjectMocks
    private ContentDispositionInteceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    void preHandle_WhenMethodHasAnnotation_ThenSetRequestAttribute() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(
                new TestController(),
                TestController.class.getMethod("annotatedMethod")
        );

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        verify(request).setAttribute(
                CONTENT_DISPOSITION_FILENAME_ATTR,
                "testfail.xml"
        );
        verifyNoMoreInteractions(request);
        verifyNoInteractions(response);
    }

    @Test
    void preHandle_WhenAnnotationIsMissing_ThenDoNotSetRequestAttribute() throws Exception {
        HandlerMethod handlerMethod = new HandlerMethod(
                new TestController(),
                TestController.class.getMethod("nonAnnotatedMethod")
        );

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        verifyNoInteractions(request, response);
    }

    @Test
    void preHandle_WhenHandlerIsNotHandlerMethod_ThenReturnTrueAndDoNotSetAttribute() {
        Object handler = new Object();

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        verifyNoInteractions(request, response);
    }

    static class TestController {

        @WithContentDisposition(filename = "testfail.xml")
        public void annotatedMethod() {}

        public void nonAnnotatedMethod() {}
    }

}
