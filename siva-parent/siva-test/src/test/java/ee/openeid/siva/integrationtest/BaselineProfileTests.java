package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.integrationtest.report.simple.SimpleReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BaselineProfileTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "baseline_profile_test_files/";

    @Test
    public void baselineProfileBDocumentShouldFail() {
        SimpleReport report = postForSimpleReport("hellopades-pades-b-sha256-auth.pdf");
        assertAllSignaturesAreInvalid(report);
    }

    @Ignore
    @Test // need non-plugtest test file
    public void baselineProfileTDocumentShouldFail() {
        SimpleReport report = postForSimpleReport("some_file.pdf");
        assertInvalidWithError(report, "BBB_VCI_ISFC_ANS_1", "The signature format is not allowed by the validation policy constraint!");
    }

    @Test
    public void baselineProfileLTDocumentShouldPass() {
        assertAllSignaturesAreValid(postForSimpleReport("hellopades-pades-lt-sha256-sign.pdf"));
    }

    @Ignore
    @Test // need non-plugtest test file
    public void baselineProfileLTADocumentShouldPass() {
        assertAllSignaturesAreValid(postForSimpleReport("some_file.pdf"));
    }

    @Test
    public void documentWithBaselineProfilesBAndLTASignaturesShouldPass() {
        SimpleReport report = postForSimpleReport("hellopades-lt-b.pdf");
        assertSomeSignaturesAreValid(report, 1);
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentMessageDigestAttributeValueDoesNotMatchCalculatedValue() {
        assertAllSignaturesAreInvalid(postForSimpleReport("hellopades-lt1-lt2-wrongDigestValue.pdf"));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithMultipleSignersSerialSignature() {
        assertAllSignaturesAreValid(postForSimpleReport("hellopades-lt1-lt2-Serial.pdf"));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithMultipleSignersParallelSignature() {
        assertAllSignaturesAreValid(postForSimpleReport("hellopades-lt1-lt2-parallel3.pdf"));
    }

    @Test
    public void ifSignerCertificateIsNotQualifiedAndWithoutSscdItIsRejected() {
        SimpleReport report = postForSimpleReport("hellopades-lt1-lt2-parallel3.pdf");
        assertInvalidWithError(report, "BBB_XCV_CMDCIQC_ANS", "The certificate is not qualified!");
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
