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
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.Warning;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import org.junit.Ignore;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class PDFWithOneValidSignatureTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_VALID_SIGNATURE = "hellopades-pades-lt-sha256-sign.pdf";
    private static final String PDF_SIGNED_WITH_UNQUALIFIED_CERTIFICATE = "hellopades-lt1-lt2-parallel3.pdf";

    @Test
    public void validatingWithValidPdfShouldReturnValidationReportPojo() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        assertNotNull(report);
    }

    @Test
    public void validationReportForValidPdfShouldHaveEqualSignatureCountAndValidSignatureCount() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(PDF_WITH_ONE_VALID_SIGNATURE, validationConclusion.getValidatedDocument().getFilename());
        assertTrue(validationConclusion.getValidSignaturesCount() == 1);
        assertTrue(validationConclusion.getSignaturesCount() == 1);
    }

    @Test
    public void whenValidatingValidPDFThenDateTimesShouldBeCorrectlyParsed() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("2015-07-09T07:00:48Z", signature.getClaimedSigningTime());
        assertEquals("2015-07-09T07:00:55Z", signature.getInfo().getBestSignatureTime());
    }

    @Test
    public void validatedSignatureShouldHaveCorrectId() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("S-202E914B96AC6CF210472EF75C9FA4D3C0D5D028FDBBEBE52CB18AA0D3DC1761", signature.getId());
    }

    @Test
    public void validatedSignatureShouldHaveFormatAndLevel() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("QESIG", signature.getSignatureLevel());
        assertEquals("PAdES_BASELINE_LT", signature.getSignatureFormat());
    }

    @Test
    public void validationResultForValidPDFShouldHaveCorrectSignatureScopeForPDF() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureScope scope = report.getValidationConclusion().getSignatures().get(0).getSignatureScopes().get(0);

        assertEquals("The document byte range: [0, 14153, 52047, 491]", scope.getContent());
        assertEquals("PARTIAL", scope.getScope());
        assertEquals("PDF previous version #1", scope.getName());
    }

    @Test
    public void validationResultForValidPDFShouldNotHaveErrorsOrWarnings() {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        report.getValidationConclusion().getSignatures().forEach(this::assertNoErrors);
    }

    @Test
    public void validationResultForPdfShouldContainNull() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        assertEquals(null, report.getValidationConclusion().getSignatureForm());
    }

    @Test
    @Ignore //TODO: Warnings are not returned when validationLevel is set to ARCHIVAL_DATA (default level)
    public void validatingPdfSignedWithUnqualifiedCertificateReturnsReportWithoutErrorsButWithWarning() throws Exception {
        SimpleReport report = validateAndAssertReports(
                buildValidationDocument(PDF_SIGNED_WITH_UNQUALIFIED_CERTIFICATE)).getSimpleReport();
        assertNotNull(report);
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        List<Warning> firstSignatureWarnings = validationConclusion.getSignatures().get(0).getWarnings();
        List<Warning> secondSignatureWarnings = validationConclusion.getSignatures().get(1).getWarnings();

        assertEquals("The certificate is not supported by SSCD!", firstSignatureWarnings.get(0).getContent());
        assertEquals("The certificate is not qualified!", firstSignatureWarnings.get(1).getContent());
        assertEquals("The certificate is not supported by SSCD!", secondSignatureWarnings.get(0).getContent());
        assertEquals("The certificate is not qualified!", secondSignatureWarnings.get(1).getContent());

        assertSubjectDNPresent(report.getValidationConclusion().getSignatures().get(0), "36706020210", "SINIVEE,VEIKO");
    }

    @Test
    public void assertPdfSignedWithOneValidSignatureDiagnosticData() {
        Date validationStartDate = new Date();

        ValidationDocument validationDocument = buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE);
        Reports reports = validateAndAssertReports(validationDocument);
        XmlDiagnosticData diagnosticData = reports.getDiagnosticReport().getDiagnosticData();

        assertValidationDate(validationStartDate, diagnosticData.getValidationDate());
        assertNull(diagnosticData.getContainerInfo());

        assertSame(1, diagnosticData.getSignatures().size());
        XmlSignature signature = diagnosticData.getSignatures().get(0);
        assertEquals(validationDocument.getName(), signature.getSignatureFilename());
        assertNull(signature.getErrorMessage());
        assertEquals(SignatureLevel.PAdES_BASELINE_LT, signature.getSignatureFormat());
        assertEquals("1.2.840.113549.1.7.1", signature.getContentType());

        ZonedDateTime expectedDateTimeInUTC = ZonedDateTime.of(2015, 7, 9, 7, 0, 48, 0, ZoneId.of("UTC"));
        assertEquals(expectedDateTimeInUTC.toInstant(), signature.getDateTime().toInstant());
        assertTrue(signature.getStructuralValidation().isValid());
        assertTrue(signature.getBasicSignature().isSignatureIntact());
        assertTrue(signature.getBasicSignature().isSignatureValid());
        assertTrue(signature.getSigningCertificate().isAttributePresent());
        assertTrue(signature.getSigningCertificate().isDigestValuePresent());
        assertTrue(signature.getSigningCertificate().isDigestValueMatch());
        assertTrue(signature.getSigningCertificate().isIssuerSerialMatch());

        assertSame(4, diagnosticData.getUsedCertificates().size());
        assertSame(2, signature.getCertificateChain().size());

        assertSame(1, diagnosticData.getTrustedLists().size());
        assertEquals("EE", diagnosticData.getTrustedLists().get(0).getCountryCode());

        assertNotNull(diagnosticData.getListOfTrustedLists());
        assertEquals("EU", diagnosticData.getListOfTrustedLists().getCountryCode());

        assertSubjectDNPresent(reports.getDiagnosticReport().getValidationConclusion().getSignatures().get(0), "36706020210", "SINIVEE,VEIKO,36706020210");
    }
}
