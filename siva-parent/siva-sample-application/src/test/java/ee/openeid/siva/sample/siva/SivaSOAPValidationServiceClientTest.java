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

package ee.openeid.siva.sample.siva;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.test.utils.TestFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.*;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;


import static ee.openeid.siva.sample.siva.SivaSOAPValidationServiceClient.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SivaSOAPValidationServiceClientTest {
    @Rule
    public TemporaryFolder testingFolder = new TemporaryFolder();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Autowired
    @Qualifier(value = "sivaSOAP")
    private ValidationService validationService;
    @MockBean
    private RestTemplate restTemplate;
    @Captor
    private ArgumentCaptor<String> validationRequestCaptor;

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
        String response = FileUtils.readFileToString(TestFileUtils.loadTestFile("/soap_response.xml")).replaceAll("\\n", System.lineSeparator());
        response = response.replaceAll("\\r\\r\\n", System.lineSeparator());

        serverMockResponse(response);
        UploadedFile uploadedFile = TestFileUtils.generateUploadFile(testingFolder, "hello.bdoc", "Valid document");

        Observable<String> validatedDocument = validationService.validateDocument("", "", uploadedFile);
        assertThat(validatedDocument.toBlocking().first()).isEqualTo(response);

        verify(restTemplate).postForObject(anyString(), validationRequestCaptor.capture(), any());
        assertThat(validationRequestCaptor.getValue()).contains("<Filename>hello.bdoc</Filename>");

    }

    @Test
    public void givenValidRequestReturnsInvalidXMLReturnsEmptyString() throws Exception {
        serverMockResponse(StringUtils.EMPTY);
        UploadedFile uploadedFile = TestFileUtils.generateUploadFile(testingFolder, "hello.bdoc", "Valid document");
        Observable<String> validatedDocument = validationService.validateDocument("", "", uploadedFile);

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
        validationService.validateDocument(null, null, null);
    }

    @Test
    public void validXmlSoapCreation() {
        String request = SivaSOAPValidationServiceClient.createXMLValidationRequest("dGVzdA==", FileType.XROAD, "filename.asice", "Simple", "POLv3");
        String expectedRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">" + LINE_SEPARATOR +
                "   <soapenv:Header/>" + LINE_SEPARATOR +
                "   <soapenv:Body>" + LINE_SEPARATOR +
                "      <soap:ValidateDocument>" + LINE_SEPARATOR +
                "         <soap:ValidationRequest>" + LINE_SEPARATOR +
                "            <Document>dGVzdA==</Document>" + LINE_SEPARATOR +
                "            <Filename>filename.asice</Filename>" + LINE_SEPARATOR +
                "            <DocumentType>XROAD</DocumentType>" + LINE_SEPARATOR +
                "            <ReportType>Simple</ReportType>" + LINE_SEPARATOR +
                "            <SignaturePolicy>POLv3</SignaturePolicy>" + LINE_SEPARATOR +
                "         </soap:ValidationRequest>" + LINE_SEPARATOR +
                "      </soap:ValidateDocument>" + LINE_SEPARATOR +
                "   </soapenv:Body>" + LINE_SEPARATOR +
                "</soapenv:Envelope>";
        Assert.assertEquals(expectedRequest, request);
    }

    private void serverMockResponse(String response) {
        when(restTemplate.postForObject(anyString(), anyString(), anyObject()))
                .thenReturn(response);
    }
}
