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

package ee.openeid.siva.sample.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.siva.DataFilesService;
import ee.openeid.siva.sample.siva.SivaServiceType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Observable;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DataFilesTaskRunnerTest {

    @Autowired
    private DataFilesTaskRunner dataFilesTaskRunner;

    @MockBean(name = SivaServiceType.JSON_DATAFILES_SERVICE)
    private DataFilesService jsonDataFilesService;

    @MockBean(name = SivaServiceType.SOAP_DATAFILES_SERVICE)
    private DataFilesService soapDataFilesService;

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Before
    public void setUp() throws Exception {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);

        given(jsonDataFilesService.getDataFiles(any(UploadedFile.class)))
                .willReturn(Observable.just("{}"));

        given(soapDataFilesService.getDataFiles(any(UploadedFile.class)))
                .willReturn(Observable.just("<soap></soap>"));
    }

    @After
    public void tearDown() throws Exception {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }

    @Test
    public void givenValidUploadFileReturnsDataFilesResultForAllServices() throws Exception {
        dataFilesTaskRunner.run(new UploadedFile());

        assertThat(dataFilesTaskRunner.getDataFilesResult(ResultType.JSON)).isEqualTo("{}");
        assertThat(dataFilesTaskRunner.getDataFilesResult(ResultType.SOAP)).isEqualTo("<soap></soap>");
    }

    @Test
    public void givenClearCommandReturnsEmptyMapValueAsNull() throws Exception {
        dataFilesTaskRunner.run(new UploadedFile());
        dataFilesTaskRunner.clearDataFilesResults();

        assertThat(dataFilesTaskRunner.getDataFilesResult(ResultType.JSON)).isNull();
    }

    @Test
    public void validationServiceThrowsExceptionLogMessageIsWritten() throws Exception {
        given(jsonDataFilesService.getDataFiles(any(UploadedFile.class))).willThrow(new IOException());
        dataFilesTaskRunner.run(new UploadedFile());

        verify(mockAppender).doAppend(captorLoggingEvent.capture());

        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();

        assertThat(loggingEvent.getLevel()).isEqualTo(Level.WARN);
        assertThat(loggingEvent.getFormattedMessage()).contains("Uploaded file data files extraction failed with error");
    }

}
