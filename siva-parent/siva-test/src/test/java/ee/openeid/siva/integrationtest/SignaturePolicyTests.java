package ee.openeid.siva.integrationtest;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Ignore;
import org.junit.Test;

public class SignaturePolicyTests extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "signature_policy_test_files/";

    /**
     * TestCaseID: Signature-Policy-1
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
     * TestCaseID: Signature-Policy-2
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
     * TestCaseID: Signature-Policy-2
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
     * TestCaseID: Signature-Policy-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has LT and B profile signatures
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
     * TestCaseID: Signature-Policy-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has LT and B profile signatures
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
     * TestCaseID: Signature-Policy-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has LT and B profile signatures
     *
     * Expected Result: 2 of 2 signatures' validation should pass when signature policy is set to "eU"
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void testPdfDocumentSignaturePolicyCaseInsensitvity() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", "eU");
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: Signature-Policy-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with conformant EE signature
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

    /**
     * TestCaseID: Signature-Policy-7
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with conformant EE signature
     *
     * Expected Result: Document should pass when signature policy is set to "ee"
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void testBdocDocumentSignaturePolicyCaseInsensitvity() {
        QualifiedReport report = postForReport("Valid_ID_sig.bdoc", "ee");
        assertAllSignaturesAreValid(report);
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }

}
