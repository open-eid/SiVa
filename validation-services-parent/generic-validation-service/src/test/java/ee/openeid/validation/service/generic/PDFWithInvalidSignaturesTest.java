/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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
import eu.europa.esig.dss.jaxb.diagnostic.DiagnosticData;
import eu.europa.esig.dss.jaxb.diagnostic.XmlSignature;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PDFWithInvalidSignaturesTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE = "hellopades-pades-b-sha256-auth.pdf";

    @Test
    @Ignore("java.lang.OutOfMemoryError: Java heap space may occur")
    public void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithNoValidSignatures() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE)).getSimpleReport();
        assertNotNull(report);
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertTrue(validationConclusion.getSignaturesCount() == 1);
        assertTrue(validationConclusion.getValidSignaturesCount() == 0);
    }

    @Ignore(/*TODO:*/"SignatureFormatConstraint outputs error node in wrong format, so error is not parsed correctly to report (VAL-197)")
    @Test
    public void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithOneCorrectlyFormattedError() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE)).getSimpleReport();
        System.out.println(report);
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertTrue(signature.getErrors().size() == 1);
        Error error = signature.getErrors().get(0);

        //check error object integrity
        assertTrue(StringUtils.isNotBlank(error.getContent()));

    }

    @Test
    public void assertPdfSignedWithInvalidSignatureDiagnosticData() {
        Date validationStartDate = new Date();

        ValidationDocument validationDocument = buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE);
        Reports reports = validateAndAssertReports(validationDocument);
        DiagnosticData diagnosticData = reports.getDiagnosticReport().getDiagnosticData();

        assertValidationDate(validationStartDate, diagnosticData.getValidationDate());
        assertNull(diagnosticData.getContainerInfo());

        assertSame(1, diagnosticData.getSignatures().size());
        XmlSignature signature = diagnosticData.getSignatures().get(0);
        assertEquals(validationDocument.getName(), signature.getSignatureFilename());
        assertNull(signature.getErrorMessage());
        assertEquals("PAdES-BASELINE-B", signature.getSignatureFormat());
        assertEquals("application/pdf", signature.getContentType());

        ZonedDateTime expectedDateTimeInUTC = ZonedDateTime.of(2015, 7, 9, 6, 15, 51, 0, ZoneId.of("UTC"));
        assertEquals(expectedDateTimeInUTC.toInstant(), signature.getDateTime().toInstant());
        assertTrue(signature.getStructuralValidation().isValid());
        assertTrue(signature.getBasicSignature().isSignatureIntact());
        assertTrue(signature.getBasicSignature().isSignatureValid());
        assertTrue(signature.getSigningCertificate().isAttributePresent());
        assertTrue(signature.getSigningCertificate().isDigestValuePresent());
        assertTrue(signature.getSigningCertificate().isDigestValueMatch());
        assertTrue(signature.getSigningCertificate().isIssuerSerialMatch());

        assertSame(2, diagnosticData.getUsedCertificates().size());
        assertSame(2, signature.getCertificateChain().size());

        assertSame(1, diagnosticData.getTrustedLists().size());
        assertEquals("EE", diagnosticData.getTrustedLists().get(0).getCountryCode());

        assertNotNull(diagnosticData.getListOfTrustedLists());
        assertEquals("EU", diagnosticData.getListOfTrustedLists().getCountryCode());

        Assert.assertSame(1, reports.getDiagnosticReport().getValidationConclusion().getSignatures().size());

        SignatureValidationData signatureValidationData = reports.getDiagnosticReport().getValidationConclusion().getSignatures().get(0);
        assertSubjectDNPresent(signatureValidationData, "36706020210", "SINIVEE,VEIKO,36706020210");
    }

}
