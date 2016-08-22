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
        error.setHttpStatus(statusCode);
        throw new RESTValidationProxyException(error);
    }
}
