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

package ee.openeid.siva.validation.validator;

import eu.europa.esig.dss.model.DSSException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaxSizeHttpClientResponseHandlerTest {

    private static final int MAX_SIZE = 16;

    private final MaxSizeHttpClientResponseHandler handler = new MaxSizeHttpClientResponseHandler(MAX_SIZE);

    @Test
    void handleResponse_WhenBodyIsUnderLimit_ReturnsBody() throws Exception {
        byte[] body = new byte[MAX_SIZE - 1];

        assertThat(handler.handleResponse(response(body)), is(body));
    }

    @Test
    void handleResponse_WhenBodyIsExactlyLimit_ReturnsBody() throws Exception {
        byte[] body = new byte[MAX_SIZE];

        assertThat(handler.handleResponse(response(body)), is(body));
    }

    @Test
    void handleResponse_WhenBodyIsOverLimit_ThrowsDSSException() {
        ClassicHttpResponse response = response(new byte[MAX_SIZE + 1]);

        assertThrows(DSSException.class, () -> handler.handleResponse(response));
    }

    private static ClassicHttpResponse response(byte[] body) {
        BasicClassicHttpResponse response = new BasicClassicHttpResponse(HttpStatus.SC_OK);
        response.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_OCTET_STREAM));
        return response;
    }
}
