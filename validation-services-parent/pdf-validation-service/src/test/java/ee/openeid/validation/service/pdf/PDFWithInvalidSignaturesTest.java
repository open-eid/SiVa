package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class PDFWithInvalidSignaturesTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE = "hellopades-pades-b-sha256-auth.pdf";
    private static final String PDF_SIGNED_WITH_UNQUALIFIED_CERTIFICATE = "hellopades-lt1-lt2-parallel3.pdf";

    @Test
    public void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithNoValidSignatures() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE));
        assertNotNull(report);
        assertTrue(report.getSignaturesCount() == 1);
        assertTrue(report.getValidSignaturesCount() == 0);
    }

    @Ignore(/*TODO:*/"SignatureFormatConstraint outputs error node in wrong format, so error is not parsed correctly to report (VAL-197)")
    @Test
    public void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithOneCorrectlyFormattedError() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE));
        System.out.println(report);
        SignatureValidationData signature = report.getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertTrue(signature.getErrors().size() == 1);
        Error error = signature.getErrors().get(0);

        //check error object integrity
        assertTrue(StringUtils.isNotBlank(error.getContent()));
        assertTrue(StringUtils.isNotBlank(error.getNameId()));
        assertFalse(StringUtils.equals("EMPTY", error.getNameId()));

    }

    @Test
    public void validatingPdfSignedWithUnqualifiedCertificateReturnsReportWithError() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_SIGNED_WITH_UNQUALIFIED_CERTIFICATE));
        assertNotNull(report);
        assertNoWarnings(report.getSignatures().get(0));
        assertNoWarnings(report.getSignatures().get(1));
        Error firstSignatureError = report.getSignatures().get(0).getErrors().get(0);
        Error secondSignatureError = report.getSignatures().get(1).getErrors().get(0);
        assertEquals(firstSignatureError, secondSignatureError);
        assertEquals("BBB_XCV_CMDCIQC_ANS", firstSignatureError.getNameId());
        assertEquals("The certificate is not qualified!", firstSignatureError.getContent());
    }
}