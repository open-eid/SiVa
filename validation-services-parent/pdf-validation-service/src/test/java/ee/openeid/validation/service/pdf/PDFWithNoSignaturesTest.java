package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Test;

import static org.junit.Assert.*;

public class PDFWithNoSignaturesTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_NO_SIGNATURES = "no-signatures.pdf";

    @Test
    public void validatingPdfWithNoSignaturesReturnsReport() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_NO_SIGNATURES));
        assertNotNull(report);
        assertEquals(PDF_WITH_NO_SIGNATURES, report.getDocumentName());
        assertTrue(report.getValidSignaturesCount() == 0);
        assertTrue(report.getSignaturesCount() == 0);
        assertTrue(report.getSignatures().isEmpty());
        assertNotNull(report.getValidationTime());
    }
}
