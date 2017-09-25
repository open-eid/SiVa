/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.validation.document.report.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PDFWithOneValidSignatureTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_VALID_SIGNATURE = "hellopades-pades-lt-sha256-sign.pdf";
    private static final String PDF_SIGNED_WITH_UNQUALIFIED_CERTIFICATE = "hellopades-lt1-lt2-parallel3.pdf";

    @Test
    public void validatingWithValidPdfShouldReturnValidationReportPojo() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        assertNotNull(report);
    }

    @Test
    public void validationReportForValidPdfShouldHaveEqualSignatureCountAndValidSignatureCount() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(PDF_WITH_ONE_VALID_SIGNATURE, validationConclusion.getValidatedDocument().getFilename());
        assertTrue(validationConclusion.getValidSignaturesCount() == 1);
        assertTrue(validationConclusion.getSignaturesCount() == 1);
    }

    @Test
    public void validationReportShouldHaveSameDocumentNameWithValidationRequest() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        assertEquals(PDF_WITH_ONE_VALID_SIGNATURE, report.getValidationConclusion().getValidatedDocument().getFilename());
    }

    @Test
    public void whenValidatingValidPDFThenDateTimesShouldBeCorrectlyParsed() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("2015-07-09T07:00:48Z", signature.getClaimedSigningTime());
        assertEquals("2015-07-09T07:00:55Z", signature.getInfo().getBestSignatureTime());
    }

    @Test
    public void validatedSignatureShouldHaveCorrectId() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("id-65dc6b043effc2542519162d271ad4f9780e552845d04b66868301a5cf0ed8ba", signature.getId());
    }

    @Test
    public void validatedSignatureShouldHaveFormatAndLevel() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureValidationData signature = report.getValidationConclusion().getSignatures().get(0);
        assertEquals("QESIG", signature.getSignatureLevel());
        assertEquals("PAdES-BASELINE-LT", signature.getSignatureFormat());
    }

    @Test
    public void validationResultForValidPDFShouldHaveCorrectSignatureScopeForPDF() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        SignatureScope scope = report.getValidationConclusion().getSignatures().get(0).getSignatureScopes().get(0);

        assertEquals("The document byte range: [0, 14153, 52047, 491]", scope.getContent());
        assertEquals("PdfByteRangeSignatureScope", scope.getScope());
        assertEquals("PDF previous version #1", scope.getName());
    }

    @Test
    public void validationResultForValidPDFShouldNotHaveErrorsOrWarnings() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        report.getValidationConclusion().getSignatures().forEach(this::assertNoErrorsOrWarnings);
    }

    @Test
    public void validationResultForPdfShouldContainNull() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE)).getSimpleReport();
        assertEquals(null, report.getValidationConclusion().getSignatureForm());
    }

    @Test
    @Ignore //TODO: Warnings are not returned when validationLevel is set to ARCHIVAL_DATA (default level)
    public void validatingPdfSignedWithUnqualifiedCertificateReturnsReportWithoutErrorsButWithWarning() throws Exception {
        SimpleReport report = validationService.validateDocument(
                buildValidationDocument(PDF_SIGNED_WITH_UNQUALIFIED_CERTIFICATE)).getSimpleReport();
        assertNotNull(report);
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        List<Warning> firstSignatureWarnings = validationConclusion.getSignatures().get(0).getWarnings();
        List<Warning> secondSignatureWarnings = validationConclusion.getSignatures().get(1).getWarnings();

        assertEquals("The certificate is not supported by SSCD!", firstSignatureWarnings.get(0).getContent());
        assertEquals("The certificate is not qualified!", firstSignatureWarnings.get(1).getContent());
        assertEquals("The certificate is not supported by SSCD!", secondSignatureWarnings.get(0).getContent());
        assertEquals("The certificate is not qualified!", secondSignatureWarnings.get(1).getContent());
    }
}
