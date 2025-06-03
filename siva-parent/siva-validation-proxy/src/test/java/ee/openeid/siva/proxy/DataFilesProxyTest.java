/*
 * Copyright 2019 - 2025 Riigi Infosüsteemi Amet
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.exception.DataFilesServiceNotFoundException;
import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.report.DataFileData;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.validation.service.DataFilesService;
import ee.openeid.validation.service.timemark.DDOCDataFilesService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class DataFilesProxyTest {

    private DataFilesProxy dataFilesProxy;

    private ApplicationContext applicationContext;

    private DataFilesServiceSpy dataFilesServiceSpy;

    @BeforeEach
    public void setUp() {
        dataFilesProxy = new DataFilesProxy();

        applicationContext = mock(ApplicationContext.class);
        dataFilesProxy.setApplicationContext(applicationContext);

        dataFilesServiceSpy = new DataFilesServiceSpy();
    }

    @Test
    void applicationContextHasNoBeanWithGivenNameThrowsException() {
        given(applicationContext.getBean(anyString())).willThrow(new NoSuchBeanDefinitionException("Bean not loaded"));
        ProxyDocument proxyDocument = mockProxyDocumentWithDocument("filename.bdoc");

        DataFilesServiceNotFoundException caughtException = assertThrows(
                DataFilesServiceNotFoundException.class, () -> {
                    dataFilesProxy.getDataFiles(proxyDocument);
                }
        );
        assertEquals("BDOCDataFilesService not found", caughtException.getMessage());
        verify(applicationContext).getBean(anyString());
    }

    @Test
    void ProxyDocumentWithDDOCDocumentTypeShouldReturnDataFilesReport() {
        when(applicationContext.getBean(DDOCDataFilesService.class.getSimpleName())).thenReturn(dataFilesServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument("filename.ddoc");
        DataFilesReport report = dataFilesProxy.getDataFiles(proxyDocument);
        assertEquals(dataFilesServiceSpy.dataFilesReport, report);
    }

    @Test
    void dataFilesProxyDoesNotDependOnRequestReportTypeAndAlwaysReturnsDataFilesReport() {
        when(applicationContext.getBean(DDOCDataFilesService.class.getSimpleName())).thenReturn(dataFilesServiceSpy);
        ProxyDocument proxyDocument = mockProxyDocumentWithDocument("filename.ddoc");

        for (ReportType reportType : ReportType.values()) {
            proxyDocument.setReportType(reportType);
            DataFilesReport report = dataFilesProxy.getDataFiles(proxyDocument);
            assertTrue(report instanceof DataFilesReport);
        }
    }

    private ProxyDocument mockProxyDocumentWithDocument(String filename) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(filename);
        return proxyDocument;
    }

    private class DataFilesServiceSpy implements DataFilesService {

        DataFilesReport dataFilesReport;

        @Override
        public DataFilesReport getDataFiles(DataFilesDocument dataFilesDocument) {
            dataFilesReport = createDummyReport();
            return dataFilesReport;
        }

        private DataFilesReport createDummyReport() {
            DataFilesReport report = new DataFilesReport();
            report.setDataFiles(createDummyDataFiles());
            return report;
        }

        private List<DataFileData> createDummyDataFiles() {
            DataFileData dataFileData = new DataFileData();
            dataFileData.setBase64("RGlnaURvY");
            dataFileData.setFilename("testName");
            dataFileData.setMimeType("text/plain");
            dataFileData.setSize(1);
            return Collections.singletonList(dataFileData);
        }

    }

}
