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
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFWithInvalidSignaturesTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE = "hellopades-pades-b-sha256-auth.pdf";

    @Test
    @Disabled(/*TODO:*/"java.lang.OutOfMemoryError: Java heap space may occur")
    void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithNoValidSignatures() {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE)).getSimpleReport();
        assertNotNull(report);
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(1, (int) validationConclusion.getSignaturesCount());
        assertEquals(0, (int) validationConclusion.getValidSignaturesCount());
    }

    @Disabled(/*TODO:*/"SignatureFormatConstraint outputs error node in wrong format, so error is not parsed correctly to report (VAL-197)")
    @Test
    void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithOneCorrectlyFormattedError() {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE)).getSimpleReport();
        System.out.println(report);
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertEquals(1, signature.getErrors().size());
        Error error = signature.getErrors().get(0);

        //check error object integrity
        assertTrue(StringUtils.isNotBlank(error.getContent()));

    }

    @Test
    void assertPdfSignedWithInvalidSignatureDiagnosticData() {
        Date validationStartDate = new Date();

        ValidationDocument validationDocument = buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE);
        Reports reports = validateAndAssertReports(validationDocument);
        XmlDiagnosticData diagnosticData = reports.getDiagnosticReport().getDiagnosticData();

        assertValidationDate(validationStartDate, diagnosticData.getValidationDate());
        assertNull(diagnosticData.getContainerInfo());

        assertSame(1, diagnosticData.getSignatures().size());
        XmlSignature signature = diagnosticData.getSignatures().get(0);
        assertEquals(validationDocument.getName(), signature.getSignatureFilename());
        assertNull(signature.getErrorMessage());
        assertEquals(SignatureLevel.PAdES_BASELINE_B, signature.getSignatureFormat());
        assertEquals("1.2.840.113549.1.7.1", signature.getContentType());

        ZonedDateTime expectedDateTimeInUTC = ZonedDateTime.of(2015, 7, 9, 6, 15, 51, 0, ZoneId.of("UTC"));

        assertEquals(expectedDateTimeInUTC.toInstant(), signature.getClaimedSigningTime().toInstant());

        assertSame(2, diagnosticData.getUsedCertificates().size());
        assertSame(2, signature.getCertificateChain().size());

        assertSame(2, diagnosticData.getTrustedLists().size());
        assertTrue(diagnosticData.getTrustedLists().stream().anyMatch(tl -> "EE".equals(tl.getCountryCode())));

        assertNotNull(diagnosticData.getTrustedLists());
        assertTrue(diagnosticData.getTrustedLists().stream().anyMatch(tl -> "EU".equals(tl.getCountryCode())));

        assertSame(1, reports.getDiagnosticReport().getValidationConclusion().getSignatures().size());

        SignatureValidationData signatureValidationData = reports.getDiagnosticReport().getValidationConclusion().getSignatures().get(0);
        assertSubjectDNPresent(signatureValidationData, "36706020210", "SINIVEE,VEIKO,36706020210");
    }

}
