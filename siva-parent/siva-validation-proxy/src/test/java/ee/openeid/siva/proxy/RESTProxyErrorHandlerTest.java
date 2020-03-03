/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.http.RESTValidationProxyException;
import ee.openeid.siva.proxy.http.RESTValidationProxyRequestException;
import ee.openeid.siva.proxy.http.RestProxyErrorHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RESTProxyErrorHandlerTest {

    private RestProxyErrorHandler restProxyErrorHandler;

    @Mock
    private ClientHttpResponse httpResponse;

    @Before
    public void setUp() {
        restProxyErrorHandler = new RestProxyErrorHandler();
    }

    @Test
    public void errorHandlerDoesNotHaveErrorWhenGivenHttpResponseHasNoError() throws Exception{
        when(httpResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        assertFalse(restProxyErrorHandler.hasError(httpResponse));
    }

    @Test
    public void errorHandlerHasErrorWhenGivenClientErrorCode() throws Exception {
        when(httpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        assertTrue(restProxyErrorHandler.hasError(httpResponse));
    }

    @Test
    public void errorHandlerHasErrorWhenGivenServerErrorCode() throws Exception{
        when(httpResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        assertTrue(restProxyErrorHandler.hasError(httpResponse));
    }

    @Test
    public void errorHandlerThrowsRESTValidationProxyExceptionWithInfoFromResponseBodyWhenHandlingError() throws Exception {
        createBadRequestResponseWithBody();
        try {
            restProxyErrorHandler.handleError(httpResponse);
        } catch (RESTValidationProxyRequestException e) {
            assertEquals("some key", e.getErrorKey());
            assertEquals("some message", e.getMessage());
            assertTrue(HttpStatus.BAD_REQUEST == e.getHttpStatus());
        }
    }

    @Test
    public void errorHandlerThrowsExceptionWithoutErrorKeyWhenNoKeyIsProvidedInErroneousResponse() throws Exception {
        createInternalServerErrorResponseWithBody();
        try {
            restProxyErrorHandler.handleError(httpResponse);
        } catch (RESTValidationProxyException e) {
            assertEquals("This is just some message", e.getMessage());
            assertTrue(HttpStatus.INTERNAL_SERVER_ERROR == e.getHttpStatus());
        }
    }

    private void createInternalServerErrorResponseWithBody() throws IOException {
        createResponseWithBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "{\"message\":\"This is just some message\"}");
    }

    private void createBadRequestResponseWithBody() throws IOException {
        createResponseWithBody(HttpStatus.BAD_REQUEST, "Bad Request", "{\"key\":\"some key\",\"message\":\"some message\"}");
    }

    private void createResponseWithBody(HttpStatus httpStatus, String statusText, String body) throws IOException {
        when(httpResponse.getStatusCode()).thenReturn(httpStatus);
        when(httpResponse.getStatusText()).thenReturn(statusText);
        when(httpResponse.getBody()).thenReturn(new ByteArrayInputStream(body.getBytes()));
    }
}
