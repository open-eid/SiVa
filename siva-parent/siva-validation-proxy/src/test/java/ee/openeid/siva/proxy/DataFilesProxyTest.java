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

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.DataFilesServiceNotFoundException;
import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.report.DataFileData;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.validation.service.DataFilesService;
import ee.openeid.validation.service.ddoc.DDOCDataFilesService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class DataFilesProxyTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private DataFilesProxy dataFilesProxy;

    private ApplicationContext applicationContext;

    private DataFilesServiceSpy dataFilesServiceSpy;

    @Before
    public void setUp() {
        dataFilesProxy = new DataFilesProxy();

        applicationContext = mock(ApplicationContext.class);
        dataFilesProxy.setApplicationContext(applicationContext);

        dataFilesServiceSpy = new DataFilesServiceSpy();
    }

    @Test
    public void applicationContextHasNoBeanWithGivenNameThrowsException() throws Exception {
        given(applicationContext.getBean(anyString())).willThrow(new NoSuchBeanDefinitionException("Bean not loaded"));

        exception.expect(DataFilesServiceNotFoundException.class);
        exception.expectMessage("BDOCDataFilesService not found");
        ProxyDocument proxyDocument = mockProxyDocumentWithDocument("filename.bdoc");
        dataFilesProxy.getDataFiles(proxyDocument);

        verify(applicationContext).getBean(anyString());
    }

    @Test
    public void ProxyDocumentWithDDOCDocumentTypeShouldReturnDataFilesReport() throws Exception {
        when(applicationContext.getBean(DDOCDataFilesService.class.getSimpleName())).thenReturn(dataFilesServiceSpy);

        ProxyDocument proxyDocument = mockProxyDocumentWithDocument("filename.ddoc");
        DataFilesReport report = dataFilesProxy.getDataFiles(proxyDocument);
        assertEquals(dataFilesServiceSpy.dataFilesReport, report);
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
