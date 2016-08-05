package ee.openeid.siva.integrationtest;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

public class SignaturePolicyTests extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "signature_policy_test_files/";

    /**
     * TestCaseID: PDF-SigPol-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: validation should fail with error when signature policy is set to a non-existing one
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test
    public void pdfDocumentWithNonExistingSignaturePolicyInRequestShouldReturnErrorResponse() {
        post(validationRequestFor("hellopades-lt-sha256-ocsp-28h.pdf", "BLA"))
                .then()
                .body("requestErrors[0].key", Matchers.is("signaturePolicy"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid signature policy"));
    }

    /**
     * TestCaseID: PDF-SigPol-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: Document with over 24h delay should fail when signature policy is set to "EE"
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test @Ignore //TODO: With DigiDoc4J 1.0.4 update this error functionality is no longer implemented
    public void pdfDocumentWithOcspOver24hDelayWithEEPolicyShouldFailWithCorrectErrorInReport() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", "EE");
        assertAllSignaturesAreInvalid(report);
    }

    /**
     * TestCaseID: PDF-SigPol-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: Document with over 24h delay should pass when signature policy is set to "EU"
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test
    public void pdfDocumentWithOcspOver24hDelayWithEUPolicyShouldPassWithoutErrors() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", "EU");
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: PDF-SigPol-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: Document with over 24h delay should fail when signature policy is not set or empty, because it defaults to "EE"
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test @Ignore //TODO: With DigiDoc4J 1.0.4 update this error functionality is no longer implemented
    public void ifSignaturePolicyIsNotSetOrEmptyPdfDocumentShouldGetValidatedAgainstEEPolicy() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", null);
        assertAllSignaturesAreInvalid(report);

        report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", "");
        assertAllSignaturesAreInvalid(report);
    }

    /**
     * TestCaseID: PDF-SigPol-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LT and B profile signature
     *
     * Expected Result: 1 of 2 signatures' should pass when signature policy is set to "EE"
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void pdfDocumentWithBaselineProfilesBAndLTSignaturesValidatedAgainstEEPolicyOnlyLTShouldPass() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", "EE");
        assertSomeSignaturesAreValid(report, 1);
    }

    /**
     * TestCaseID: PDF-SigPol-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LT and B profile signature
     *
     * Expected Result: 2 of 2 signatures' validation should pass when signature policy is set to "EU"
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void pdfDocumentWithBaselineProfilesBAndLTSignaturesValidatedAgainstEUPolicyBothShouldPass() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", "EU");
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: BDOC-SigPol-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Valid_ID_sig.bdoc
     *
     * Expected Result: validation should fail with error when signature policy is set to a non-existing one
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void bdocDocumentWithNonExistingSignaturePolicyInRequestShouldReturnErrorResponse() {
        post(validationRequestFor("Valid_ID_sig.bdoc", "BLA"))
                .then()
                .body("requestErrors[0].key", Matchers.is("signaturePolicy"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid signature policy"));
    }

    /**
     * TestCaseID: BDOC-SigPol-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Valid_ID_sig.bdoc
     *
     * Expected Result: Document should pass when signature policy is set to "EE"
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void bdocDocumentWithEESignaturePolicyInRequestShouldPass() {
        QualifiedReport report = postForReport("Valid_ID_sig.bdoc", "EE");
        assertAllSignaturesAreValid(report);
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }

}
