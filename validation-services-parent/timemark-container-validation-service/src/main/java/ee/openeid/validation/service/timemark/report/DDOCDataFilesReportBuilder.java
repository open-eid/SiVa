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

package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.report.DataFileData;
import ee.openeid.siva.validation.document.report.DataFilesReport;

import org.apache.commons.codec.binary.Base64;
import org.digidoc4j.DataFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DDOCDataFilesReportBuilder {

    private List<DataFile> docDataFiles;

    public DDOCDataFilesReportBuilder(List<DataFile> docDataFiles) {
        this.docDataFiles = docDataFiles;
    }

    public DataFilesReport build() {
        DataFilesReport dataFilesReport = new DataFilesReport();
        dataFilesReport.setDataFiles(getDataFiles());
        return dataFilesReport;
    }

    private List<DataFileData> getDataFiles() {
        if (docDataFiles == null) {
            return Collections.emptyList();
        }
        return docDataFiles.stream()
                .map(this :: createDataFileData)
                .filter(Objects :: nonNull)
                .collect(Collectors.toList());
    }

    private DataFileData createDataFileData(DataFile dataFile) {
        if (dataFile == null) {
            return null;
        }
        DataFileData dataFileData = new DataFileData();
        dataFileData.setBase64(getBase64String(dataFile));
        dataFileData.setFilename(dataFile.getName());
        dataFileData.setMimeType(dataFile.getMediaType());
        dataFileData.setSize(dataFile.getFileSize());
        return dataFileData;
    }

    private String getBase64String(DataFile dataFile) {
        return Base64.encodeBase64String(dataFile.getBytes());
    }

}
