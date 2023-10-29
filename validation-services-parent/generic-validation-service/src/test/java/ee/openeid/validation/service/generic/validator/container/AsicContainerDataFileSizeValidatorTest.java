/*
 * Copyright 2021 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.container;

import ee.openeid.validation.service.generic.helper.TestXmlDetailUtils;
import eu.europa.esig.dss.diagnostic.jaxb.XmlContainerInfo;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlSignature;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

class AsicContainerDataFileSizeValidatorTest {

    @ParameterizedTest
    @MethodSource("listsOfDataFiles")
    void testEntryIsNeverReadIfNotDataFile(List<String> dataFiles) throws IOException {
        List<XmlMessage> signatureWarnings = new ArrayList<>();
        AsicContainerDataFileSizeValidator validator = createValidatorFor(dataFiles, Map.of("123", signatureWarnings));
        ZipEntry nonDataFileEntry = createZipEntryMockWithName("non-datafile");
        InputStream entryStream = Mockito.mock(InputStream.class);

        validator.validate(nonDataFileEntry, entryStream);

        Assertions.assertEquals(List.of(), signatureWarnings);
        assertOnlyZipEntryGetNameCalled(nonDataFileEntry);
        Mockito.verifyNoInteractions(entryStream);
    }

    private static Stream<List<String>> listsOfDataFiles() {
        return Stream.of(
                List.of(),
                List.of("data-file"),
                List.of("data-file-1", "data-file-2", "data-file-3")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10})
    void testWarningsOfEmptyDataFilesAreAddedToAllSignatures(int signatureCount) throws IOException {
        Map<String, byte[]> dataFileMappings = new LinkedHashMap<>();
        dataFileMappings.put("data-file-1", new byte[] {0});
        dataFileMappings.put("empty-data-file-2", new byte[0]);
        dataFileMappings.put("data-file-3", new byte[] {1, 2, 3});
        dataFileMappings.put("empty-data-file-4", new byte[0]);
        dataFileMappings.put("data-file-5", new byte[] {0x7F, (byte) 0xFF, (byte) 0x80, (byte) 0x8F, 0x00});
        Map<String, List<XmlMessage>> signatureMappings = new LinkedHashMap<>();
        for (int i = 0; i < signatureCount; ++i) {
            signatureMappings.put("signature-with-id-" + i, new ArrayList<>());
        }
        List<String> dataFiles = new ArrayList<>(dataFileMappings.keySet());
        AsicContainerDataFileSizeValidator validator = createValidatorFor(dataFiles, signatureMappings);

        for (int df = 0; df < dataFiles.size(); ++df) {
            String dataFileName = dataFiles.get(df);
            ZipEntry zipEntry = createZipEntryMockWithName(dataFileName);
            List<XmlMessage> expectedWarnings = dataFileMappings.entrySet().stream()
                    .limit(df + 1L) // Consider warnings only up to the current datafile
                    .filter(dataFileEntry -> dataFileEntry.getValue().length == 0)
                    .map(dataFileEntry -> String.format("Data file '%s' is empty", dataFileEntry.getKey()))
                    .map(message -> TestXmlDetailUtils.createMessage(null, message))
                    .collect(Collectors.toList());

            try (InputStream entryStream = new ByteArrayInputStream(dataFileMappings.get(dataFileName))) {
                validator.validate(zipEntry, entryStream);
            }

            assertOnlyZipEntryGetNameCalled(zipEntry);
            for (Map.Entry<String, List<XmlMessage>> signatureEntry : signatureMappings.entrySet()) {
                TestXmlDetailUtils.assertEquals(expectedWarnings, signatureEntry.getValue());
            }
        }

        for (Map.Entry<String, List<XmlMessage>> signatureEntry : signatureMappings.entrySet()) {
            TestXmlDetailUtils.assertEquals(
                    TestXmlDetailUtils.createMessageList(
                            TestXmlDetailUtils.createMessage(null, "Data file 'empty-data-file-2' is empty"),
                            TestXmlDetailUtils.createMessage(null, "Data file 'empty-data-file-4' is empty")
                    ),
                    signatureEntry.getValue()
            );
        }
    }

    @Test
    void testWarningOfEmptyDataFileIsAppendedToExistingWarnings() throws IOException {
        List<XmlMessage> signatureWarnings = TestXmlDetailUtils.createMessageList(
                TestXmlDetailUtils.createMessage("Key 1", "Existing warning 1"),
                TestXmlDetailUtils.createMessage("Key 2", "Existing warning 2")
        );
        AsicContainerDataFileSizeValidator validator = createValidatorFor(List.of("data-file"), Map.of("123", signatureWarnings));
        ZipEntry dataFileEntry = createZipEntryMockWithName("data-file");

        try (InputStream entryStream = new ByteArrayInputStream(new byte[0])) {
            validator.validate(dataFileEntry, entryStream);
        }

        assertOnlyZipEntryGetNameCalled(dataFileEntry);
        TestXmlDetailUtils.assertEquals(
                TestXmlDetailUtils.createMessageList(
                        TestXmlDetailUtils.createMessage("Key 1", "Existing warning 1"),
                        TestXmlDetailUtils.createMessage("Key 2", "Existing warning 2"),
                        TestXmlDetailUtils.createMessage(null, "Data file 'data-file' is empty")
                ),
                signatureWarnings
        );
    }

    private static AsicContainerDataFileSizeValidator createValidatorFor(List<String> dataFilesNames, Map<String, List<XmlMessage>> mappingsOfSignatureIdToWarningList) {
        XmlDiagnosticData diagnosticData = new XmlDiagnosticData();
        XmlContainerInfo containerInfo = new XmlContainerInfo();
        containerInfo.getContentFiles().addAll(dataFilesNames);
        diagnosticData.setContainerInfo(containerInfo);

        final XmlSimpleReport simpleReport = new XmlSimpleReport();
        mappingsOfSignatureIdToWarningList.forEach((signatureId, signatureWarningList) -> {
            XmlSignature simpleReportSignature = Mockito.mock(XmlSignature.class);
            Mockito.doReturn(signatureId).when(simpleReportSignature).getId();
            simpleReport.getSignatureOrTimestamp().add(simpleReportSignature);

            XmlDetails signatureAdESValidationDetails = Mockito.mock(XmlDetails.class);
            Mockito.doReturn(signatureWarningList).when(signatureAdESValidationDetails).getWarning();
            Mockito.doReturn(signatureAdESValidationDetails).when(simpleReportSignature).getAdESValidationDetails();
        });

        Reports validationReports = new Reports(diagnosticData, null, simpleReport, null);
        return new AsicContainerDataFileSizeValidator(validationReports);
    }

    private static ZipEntry createZipEntryMockWithName(String name) {
        ZipEntry zipEntryMock = Mockito.mock(ZipEntry.class);
        Mockito.doReturn(name).when(zipEntryMock).getName();
        return zipEntryMock;
    }

    private static void assertOnlyZipEntryGetNameCalled(ZipEntry zipEntryMock) {
        Mockito.verify(zipEntryMock, Mockito.atLeastOnce()).getName();
        Mockito.verifyNoMoreInteractions(zipEntryMock);
    }

}
