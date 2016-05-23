package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BaselineProfileTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";

    /**
     * TestCaseID: PDF-BaselineProfile-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-B profile signature
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     */
    @Test
    public void baselineProfileBDocumentShouldFail() {
        QualifiedReport report = postForReport("hellopades-pades-b-sha256-auth.pdf");
        assertAllSignaturesAreInvalid(report);
    }

    /**
     * TestCaseID: PDF-BaselineProfile-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-T profile signature
     *
     * Expected Result: Document validation should fail
     *
     * File: some_file.pdf
     */
    @Ignore
    @Test // need non-plugtest test file
    public void baselineProfileTDocumentShouldFail() {
        QualifiedReport report = postForReport("some_file.pdf");
        assertInvalidWithError(report, "BBB_VCI_ISFC_ANS_1", "The signature format is not allowed by the validation policy constraint!");
    }

    /**
     * TestCaseID: PDF-BaselineProfile-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LT profile signature
     *
     * Expected Result: Document validation should pass
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void baselineProfileLTDocumentShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-pades-lt-sha256-sign.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LTA profile signature
     *
     * Expected Result: Document validation should pass
     *
     * File: some_file.pdf
     */
    @Ignore
    @Test // need non-plugtest test file
    public void baselineProfileLTADocumentShouldPass() {
        assertAllSignaturesAreValid(postForReport("some_file.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LTA profile signature
     *
     * Expected Result: Document validation should pass
     *
     * File: some_file.pdf
     */
    @Test
    public void documentWithBaselineProfilesBAndLTASignaturesShouldPass() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf");
        assertSomeSignaturesAreValid(report, 1);
    }

    /**
     * TestCaseID: PDF-BaselineProfile-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: PDF document message digest attribute value does not match calculate value
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-lt1-lt2-wrongDigestValue.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentMessageDigestAttributeValueDoesNotMatchCalculatedValue() {
        assertAllSignaturesAreInvalid(postForReport("hellopades-lt1-lt2-wrongDigestValue.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-7
     *
     * TestType: Automated
     *
     * RequirementID: DSS-SIG-MULTISIG
     *
     * Title: PDF file with a serial signature
     *
     * Expected Result: Document signed with multiple signers with serial signatures should pass
     *
     * File: hellopades-lt1-lt2-Serial.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithMultipleSignersSerialSignature() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt1-lt2-Serial.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: PDF document signed with multiple signers parallel signature
     *
     * Expected Result: Document with parallel signatures should pass
     *
     * File: hellopades-lt1-lt2-parallel3.pdf
     */
    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithMultipleSignersParallelSignature() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt1-lt2-parallel3.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-9
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: PDF document signed with multiple signers parallel signature without Sscd
     *
     * Expected Result: Document with no qualified and without SSCD should fail
     *
     * File: hellopades-lt1-lt2-parallel3.pdf
     */

    @Test
    public void ifSignerCertificateIsNotQualifiedAndWithoutSscdItIsRejected() {
        QualifiedReport report = postForReport("hellopades-lt1-lt2-parallel3.pdf");
        assertInvalidWithError(report, "BBB_XCV_CMDCIQC_ANS", "The certificate is not qualified!");
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
