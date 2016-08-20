package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationPass extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

     /***
     * TestCaseID: PDF-ValidationPass-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with certificate that is expired after signing (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that expired after signing should pass.
     *
     * File: hellopades-lt-sha256-rsa1024-not-expired.pdf
     ***/
    @Test @Ignore //TODO: new test file is needed; the current one has issues with QC / SSCD
    public void validSignaturesRemainValidAfterSigningCertificateExpires() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa1024-not-expired.pdf"));
    }

    /***
     * TestCaseID: PDF-ValidationPass-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with certificate that will expire in 7 days after signing (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that expired after signing should pass.
     *
     * File: hellopades-lt-sha256-rsa2048-7d.pdf
     ***/
    @Test @Ignore //TODO: new test file is needed; the current one has issues with QC / SSCD
    public void certificateExpired7DaysAfterDocumentSigningShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa2048-7d.pdf"));
    }

    /***
     * TestCaseID: PDF-ValidationPass-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Pdf with single valid signature
     *
     * Expected Result: Document should pass.
     *
     * File: PdfValidSingleSignature.pdf
     ***/
    @Test
    public void validSignature() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreValid(postForReport("PdfValidSingleSignature.pdf"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
