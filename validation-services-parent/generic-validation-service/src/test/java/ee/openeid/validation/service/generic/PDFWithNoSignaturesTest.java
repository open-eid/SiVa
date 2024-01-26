/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFWithNoSignaturesTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_NO_SIGNATURES = "no-signatures.pdf";
    private static final String VALIDATION_LEVEL = "ARCHIVAL_DATA";
    @Test
    void validatingPdfWithNoSignaturesReturnsReport() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_NO_SIGNATURES)).getSimpleReport();
        assertNotNull(report);
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(PDF_WITH_NO_SIGNATURES, validationConclusion.getValidatedDocument().getFilename());
        assertEquals(0, (int) validationConclusion.getValidSignaturesCount());
        assertEquals(0, (int) validationConclusion.getSignaturesCount());
        assertTrue(validationConclusion.getSignatures().isEmpty());
        assertEquals(VALIDATION_LEVEL, validationConclusion.getValidationLevel());
        assertNotNull(validationConclusion.getValidationTime());
    }

    @Test
    void assertPdfWithNoSignaturesDiagnosticData() {
        Date validationStartDate = new Date();

        ValidationDocument validationDocument = buildValidationDocument(PDF_WITH_NO_SIGNATURES);
        Reports reports = validateAndAssertReports(validationDocument);
        XmlDiagnosticData diagnosticData = reports.getDiagnosticReport().getDiagnosticData();

        assertValidationDate(validationStartDate, diagnosticData.getValidationDate());
        assertNull(diagnosticData.getContainerInfo());

        assertTrue(diagnosticData.getSignatures().isEmpty());
        assertTrue(diagnosticData.getUsedCertificates().isEmpty());
        assertTrue(diagnosticData.getTrustedLists().isEmpty());
    }

    @Test
    void validatingNullDocumentThrowsException() {
        assertThrows(
                ValidationServiceException.class, () -> validationService.validateDocument(null)
        );
    }
}
