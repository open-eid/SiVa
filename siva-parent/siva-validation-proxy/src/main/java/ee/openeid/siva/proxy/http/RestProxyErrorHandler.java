/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.proxy.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class RestProxyErrorHandler implements ResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProxyErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        final HttpStatus.Series series = clientHttpResponse.getStatusCode().series();
        return HttpStatus.Series.CLIENT_ERROR == series
                || HttpStatus.Series.SERVER_ERROR == series;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        HttpStatus statusCode = clientHttpResponse.getStatusCode();
        String statusText = clientHttpResponse.getStatusText();
        String responseBody = IOUtils.toString(clientHttpResponse.getBody());

        LOGGER.error("Response error: {} {}", statusCode, statusText);
        LOGGER.error("Response body: {}", responseBody);

        RESTProxyError error = new ObjectMapper().readValue(responseBody, RESTProxyError.class);
        if (error.getKey() != null) {
            throw new RESTValidationProxyRequestException(error.getKey(), error.getMessage(), statusCode);
        }
        throw new RESTValidationProxyException(error.getMessage(), statusCode);
    }
}
