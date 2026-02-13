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

import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Optional;

import static ee.openeid.siva.webapp.contentdisposition.ContentDispositionConstants.CONTENT_DISPOSITION_FILENAME_ATTR;

@ControllerAdvice
public class ContentDispositionControllerAdvice implements ResponseBodyAdvice<@NonNull Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {

        Optional
                .ofNullable(request.getAttributes().get(CONTENT_DISPOSITION_FILENAME_ATTR))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(filename -> setContentDispositionHeader(response, filename));

        return body;
    }

    private static void setContentDispositionHeader(ServerHttpResponse response, String filename) {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename)
                .build();
        response.getHeaders().setContentDisposition(contentDisposition);
    }
}
