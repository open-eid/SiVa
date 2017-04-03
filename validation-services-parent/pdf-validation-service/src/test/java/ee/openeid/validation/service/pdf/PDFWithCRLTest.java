package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.validation.service.pdf.configuration.PDFSignaturePolicyProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PDFWithCRLTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_CRL_REV_ONLY = "TestFileSignatureTSCRL.pdf";

    @Test
    public void pdfWithCRLRevocationDataOnlyShouldPassWhenCrlRevocationIsAllowedInPolicy() throws Exception {
        setPolicyCrlRevocationAllowed(true);
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_CRL_REV_ONLY));
        assertEquals(new Long(1), new Long(report.getValidSignaturesCount()));
    }

    @Test
    public void pdfWithCRLRevocationDataOnlyShouldFailIfNotSpecificallyAllowedInPolicy() throws Exception {
        setPolicyCrlRevocationAllowed(false);
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_CRL_REV_ONLY));
        assertEquals(new Long(0), new Long(report.getValidSignaturesCount()));
        assertEquals("TOTAL-FAILED", report.getSignatures().get(0).getIndication());
        assertEquals("Signing certificate revocation source is not trusted",
                report
                .getSignatures()
                .get(0)
                .getErrors()
                .get(0)
                .getContent());
    }

    private void setPolicyCrlRevocationAllowed(boolean allowCrlRevocation) {
        PDFSignaturePolicyProperties policySettings = new PDFSignaturePolicyProperties();
        policySettings.initPolicySettings();
        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        signaturePolicyService.getPolicy("").setAllowCrlRevocationSource(allowCrlRevocation);
        validationService.setSignaturePolicyService(signaturePolicyService);
    }
}
