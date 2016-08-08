package ee.openeid.siva.sample.siva;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;
import ee.openeid.siva.sample.test.utils.TestFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SivaSOAPValidationServiceClientTest {
    @Autowired
    @Qualifier(value = "sivaSOAP")
    private ValidationService validationService;

    @MockBean
    private SivaRESTWebServiceConfigurationProperties properties;

    @MockBean
    private RestTemplate restTemplate;

    @Rule
    public TemporaryFolder testingFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Captor
    private ArgumentCaptor<String> validationRequestCaptor;

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
    public void givenValidRequestWillReturnSOAPValidationReport() throws Exception {
        String response = FileUtils.readFileToString(TestFileUtils.loadTestFile("/soap_response.xml"));

        serverMockResponse(response);
        UploadedFile uploadedFile = TestFileUtils.generateUploadFile(testingFolder, "hello.bdoc", "Valid document");

        Observable<String> validatedDocument = validationService.validateDocument(uploadedFile);
        assertThat(validatedDocument.toBlocking().first()).isEqualTo(response);

        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());
        assertThat(validationRequestCaptor.getValue()).contains("<Filename>hello.bdoc</Filename>");
        assertThat(validationRequestCaptor.getValue()).contains("<DocumentType>BDOC</DocumentType>");
    }

    @Test
    public void givenValidRequestReturnsInvalidXMLReturnsEmptyString() throws Exception {
        serverMockResponse(StringUtils.EMPTY);
        UploadedFile uploadedFile = TestFileUtils.generateUploadFile(testingFolder, "hello.bdoc", "Valid document");
        Observable<String> validatedDocument = validationService.validateDocument(uploadedFile);

        assertThat(validatedDocument.toBlocking().first()).isEqualTo(StringUtils.EMPTY);
        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.WARN);
        assertThat(loggingEvent.getMessage()).contains("XML Parsing error:");
    }

    @Test
    public void givenNullUploadFileWillThrowException() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("File not found");
        validationService.validateDocument(null);
    }

    private void serverMockResponse(String response) {
        when(restTemplate.postForObject(anyString(), anyString(), anyObject()))
                .thenReturn(response);
    }
}