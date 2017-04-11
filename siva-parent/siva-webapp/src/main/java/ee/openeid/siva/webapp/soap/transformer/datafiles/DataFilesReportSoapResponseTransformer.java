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

package ee.openeid.siva.webapp.soap.transformer.datafiles;

import ee.openeid.siva.validation.document.report.DataFileData;
import ee.openeid.siva.webapp.soap.DataFile;
import ee.openeid.siva.webapp.soap.DataFilesReport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataFilesReportSoapResponseTransformer {

    public DataFilesReport toSoapResponse(ee.openeid.siva.validation.document.report.DataFilesReport report) {
        DataFilesReport responseReport = new DataFilesReport();
        responseReport.setDataFiles(toSoapResponseDataFiles(report.getDataFiles()));
        return responseReport;
    }

    private DataFilesReport.DataFiles toSoapResponseDataFiles(List<DataFileData> dataFiles) {
        DataFilesReport.DataFiles responseDataFiles = new DataFilesReport.DataFiles();
        dataFiles.stream()
                .map(this::mapDataFile)
                .forEach(dataFile -> responseDataFiles.getDataFile().add(dataFile));
        return responseDataFiles;
    }

    private DataFile mapDataFile(DataFileData dataFileData) {
        DataFile dataFile = new DataFile();
        dataFile.setBase64(dataFileData.getBase64());
        dataFile.setFileName(dataFileData.getFileName());
        dataFile.setMimeType(dataFileData.getMimeType());
        dataFile.setSize(dataFileData.getSize());
        return dataFile;
    }
}
