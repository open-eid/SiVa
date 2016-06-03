package ee.openeid.siva.sample.siva;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SivaValidationServiceErrorHandlerTest {
    private SivaValidationServiceErrorHandler errorHandler = new SivaValidationServiceErrorHandler();

    @Mock
    private ClientHttpResponse httpResponse;

    @Test
    public void givenNoneErrorCodeReturnsFalse() throws Exception {
        createResponse(HttpStatus.OK);
        assertFalse(errorHandler.hasError(httpResponse));
    }

    @Test
    public void givenClientErrorCodeReturnsTrue() throws Exception {
        createResponse(HttpStatus.BAD_REQUEST);
        assertTrue(errorHandler.hasError(httpResponse));
    }

    @Test
    public void givenServerErrorReturnsTrue() throws Exception {
        createResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        assertTrue(errorHandler.hasError(httpResponse));
    }

    private ClientHttpResponse createResponse(HttpStatus status) throws IOException {
        when(httpResponse.getStatusCode()).thenReturn(status);
        return httpResponse;
    }
}