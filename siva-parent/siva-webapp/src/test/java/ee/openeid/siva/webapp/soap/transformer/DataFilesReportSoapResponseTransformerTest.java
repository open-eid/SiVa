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

package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.webapp.soap.DataFilesReport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataFilesReportSoapResponseTransformerTest {

    DataFilesReportSoapResponseTransformer transformer = new DataFilesReportSoapResponseTransformer();

    @Test
    public void qualifiedReportIsCorrectlyTransformedToSoapResponseReport() {
        ee.openeid.siva.validation.document.report.DataFilesReport dataFilesReport = createMockedDataFilesReport();
        DataFilesReport responseReport = transformer.toSoapResponse(dataFilesReport);

        assertEquals(dataFilesReport.getDataFiles().get(0).getBase64(), responseReport.getDataFiles().getDataFile().get(0).getBase64());
        assertEquals(dataFilesReport.getDataFiles().get(0).getFilename(), responseReport.getDataFiles().getDataFile().get(0).getFilename());
        assertEquals(dataFilesReport.getDataFiles().get(0).getMimeType(), responseReport.getDataFiles().getDataFile().get(0).getMimeType());
        assertEquals(dataFilesReport.getDataFiles().get(0).getSize(), responseReport.getDataFiles().getDataFile().get(0).getSize());
    }

    private ee.openeid.siva.validation.document.report.DataFilesReport createMockedDataFilesReport() {
        ee.openeid.siva.validation.document.report.DataFilesReport report = new ee.openeid.siva.validation.document.report.DataFilesReport();
        report.setDataFiles(createMockedDataFiles());
        return report;
    }

    private List<ee.openeid.siva.validation.document.report.DataFileData> createMockedDataFiles() {
        List<ee.openeid.siva.validation.document.report.DataFileData> dataFiles = new ArrayList<>();
        ee.openeid.siva.validation.document.report.DataFileData dataFile = new ee.openeid.siva.validation.document.report.DataFileData();
        dataFile.setSize(1);
        dataFile.setMimeType("text/plain");
        dataFile.setFilename("readme");
        dataFile.setBase64("RGlnaURvYyBpcyB");
        dataFiles.add(dataFile);
        return dataFiles;
    }
}
