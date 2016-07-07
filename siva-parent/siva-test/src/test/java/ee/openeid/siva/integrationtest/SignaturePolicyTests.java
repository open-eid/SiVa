package ee.openeid.siva.integrationtest;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Test;

public class SignaturePolicyTests extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "pdf/signature_policy_test_files/";


    /**
     * TestCaseID: PDF-SigPol-1
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
    @Test
    public void documentWithOcspOver24hDelayWithEEPolicyShouldFailWithCorrectErrorInReport() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", "EE");
        assertAllSignaturesAreInvalid(report);
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
     * Expected Result: Document with over 24h delay should pass when signature policy is set to "EU"
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test
    public void documentWithOcspOver24hDelayWithEUPolicyShouldPassWithoutErrors() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", "EU");
        assertAllSignaturesAreValid(report);
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
     * Expected Result: Document with over 24h delay should fail when signature policy is not set or empty, because it defaults to "EE"
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test
    public void ifSignaturePolicyIsNotSetOrEmptyDocumentShouldGetValidatedAgainstEEPolicy() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", null);
        assertAllSignaturesAreInvalid(report);

        report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", "");
        assertAllSignaturesAreInvalid(report);
    }

    /**
     * TestCaseID: PDF-SigPol-4
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
    public void documentWithBaselineProfilesBAndLTSignaturesValidatedAgainstEEPolicyOnlyLTShouldPass() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", "EE");
        assertSomeSignaturesAreValid(report, 1);
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
     * Expected Result: 2 of 2 signatures' validation should pass when signature policy is set to "EU"
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void documentWithBaselineProfilesBAndLTSignaturesValidatedAgainstEUPolicyBothShouldPass() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", "EU");
        assertAllSignaturesAreValid(report);
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }

}
