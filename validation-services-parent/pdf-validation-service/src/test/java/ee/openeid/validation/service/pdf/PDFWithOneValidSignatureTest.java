package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PDFWithOneValidSignatureTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_VALID_SIGNATURE = "hellopades-pades-lt-sha256-sign.pdf";

    @Test
    public void validatingWithValidPdfShouldReturnQualifiedReportPojo() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        assertNotNull(report);
    }

    @Test
    public void validationReportForValidPdfShouldHaveEqualSignatureCountAndValidSignatureCount() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        assertEquals(PDF_WITH_ONE_VALID_SIGNATURE, report.getDocumentName());
        assertTrue(report.getValidSignaturesCount() == 1);
        assertTrue(report.getSignaturesCount() == 1);
        System.out.println(report.toString());
    }

    @Test
    public void validationReportShouldHaveSameDocumentNameWithValidationRequest() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        assertEquals(PDF_WITH_ONE_VALID_SIGNATURE, report.getDocumentName());
    }

    @Test
    public void whenValidatingValidPDFThenDateTimesShouldBeCorrectlyParsed() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        SignatureValidationData signature = report.getSignatures().get(0);
        assertEquals("2015-07-09T07:00:48Z", signature.getClaimedSigningTime());
    }

    @Test
    public void validatedSignatureShouldHaveCorrectId() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        SignatureValidationData signature = report.getSignatures().get(0);
        assertEquals("id-65dc6b043effc2542519162d271ad4f9780e552845d04b66868301a5cf0ed8ba", signature.getId());
    }

    @Test
    public void validatedSignatureShouldHaveFormatAndLevel() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        SignatureValidationData signature = report.getSignatures().get(0);
        assertEquals("QES", signature.getSignatureLevel());
        assertEquals("PAdES_BASELINE_LT", signature.getSignatureFormat());
    }

    @Test
    public void validationResultForValidPDFShouldHaveCorrectSignatureScopeForPDF() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        SignatureScope scope = report.getSignatures().get(0).getSignatureScopes().get(0);

        assertEquals("The document byte range: [0, 14153, 52047, 491]", scope.getContent());
        assertEquals("PdfByteRangeSignatureScope", scope.getScope());
        assertEquals("PDF previous version #1", scope.getName());
    }

    @Test
    public void validationResultForValidPDFShouldNotHaveErrorsOrWarnings() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_VALID_SIGNATURE));
        report.getSignatures().forEach(this::assertNoErrorsOrWarnings);
    }
}
