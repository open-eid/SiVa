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

package ee.openeid.siva.webapp.contentdisposition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.util.HashMap;
import java.util.Map;

import static ee.openeid.siva.webapp.contentdisposition.ContentDispositionConstants.CONTENT_DISPOSITION_FILENAME_ATTR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ContentDispositionControllerAdviceTest {

    @InjectMocks
    private ContentDispositionControllerAdvice advice;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Test
    void supports_WhenCalled_AlwaysReturnsTrue() {
        boolean result = advice.supports(methodParameter, StringHttpMessageConverter.class);
        assertThat(result).isTrue();
        verifyNoInteractions(methodParameter);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test-file.pdf",
            "document.docx",
            "file with spaces.pdf",
            "ülitähtis-fail.pdf",
            "a",
            "file.name.with.dots.pdf",
            ".hiddenfile",
            "täst fílé.pdf",
            "",
            " "
    })
    void beforeBodyWrite_WhenFilenameAttributeExists_SetsContentDispositionHeader(String filename) {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CONTENT_DISPOSITION_FILENAME_ATTR, filename);
        doReturn(attributes).when(request).getAttributes();
        doReturn(headers).when(response).getHeaders();

        Object body = new Object();

        Object result = advice.beforeBodyWrite(
                body,
                methodParameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                request,
                response
        );

        assertThat(result).isSameAs(body);
        assertThat(headers.getContentDisposition()).isNotNull();
        assertThat(headers.getContentDisposition().getType()).isEqualTo("attachment");
        assertThat(headers.getContentDisposition().getFilename()).isEqualTo(filename);
        verifyNoMoreInteractions(request, response);
        verifyNoInteractions(methodParameter);
    }

    @Test
    void beforeBodyWrite_WhenFilenameAttributeIsNull_DoesNotSetHeader() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CONTENT_DISPOSITION_FILENAME_ATTR, null);
        doReturn(attributes).when(request).getAttributes();

        Object body = new Object();

        Object result = advice.beforeBodyWrite(
                body,
                methodParameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                request,
                response
        );

        assertThat(result).isSameAs(body);
        assertThat(headers.getFirst(HttpHeaders.CONTENT_DISPOSITION)).isNull();
        verifyNoMoreInteractions(request, response);
        verifyNoInteractions(methodParameter);
    }

    @Test
    void beforeBodyWrite_WhenFilenameAttributeDoesNotExist_DoesNotSetHeader() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> attributes = new HashMap<>();
        doReturn(attributes).when(request).getAttributes();

        Object body = new Object();

        Object result = advice.beforeBodyWrite(
                body,
                methodParameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                request,
                response
        );

        assertThat(result).isSameAs(body);
        assertThat(headers.getFirst(HttpHeaders.CONTENT_DISPOSITION)).isNull();
        verifyNoMoreInteractions(request, response);
        verifyNoInteractions(methodParameter);
    }

    @Test
    void beforeBodyWrite_WhenFilenameAttributeIsNotString_DoesNotSetHeader() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CONTENT_DISPOSITION_FILENAME_ATTR, 12345);
        doReturn(attributes).when(request).getAttributes();

        Object body = new Object();

        Object result = advice.beforeBodyWrite(
                body,
                methodParameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                request,
                response
        );

        assertThat(result).isSameAs(body);
        assertThat(headers.getFirst(HttpHeaders.CONTENT_DISPOSITION)).isNull();
        verifyNoMoreInteractions(request, response);
        verifyNoInteractions(methodParameter);
    }
}

