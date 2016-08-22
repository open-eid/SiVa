package ee.openeid.siva.sample.siva;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SivaValidationServiceErrorHandlerTest {
    private static final byte[] EMPTY_BODY = new byte[0];
    private ResponseErrorHandler errorHandler = new SivaValidationServiceErrorHandler();

    @Mock
    private ClientHttpResponse httpResponse;

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Before
    public void setUp() throws Exception {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }

    @After
    public void tearDown() throws Exception {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }

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

    @Test
    public void givenUserErrorStatusCodeWillLogWarnErrorMessage() throws Exception {
        errorHandler.handleError(new MockClientHttpResponse(EMPTY_BODY, HttpStatus.BAD_REQUEST));
        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();

        assertThat(loggingEvent.getLevel()).isEqualTo(Level.ERROR);
        assertThat(loggingEvent.getFormattedMessage()).contains("400 Bad Request");
    }

    private ClientHttpResponse createResponse(HttpStatus status) throws IOException {
        when(httpResponse.getStatusCode()).thenReturn(status);
        return httpResponse;
    }
}