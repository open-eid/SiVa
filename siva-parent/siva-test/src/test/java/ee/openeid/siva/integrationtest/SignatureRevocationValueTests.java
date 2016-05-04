package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.integrationtest.report.simple.SimpleReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SignatureRevocationValueTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "signature_revocation_value_test_files/";

    @Test
    public void documentWithOcspOver15MinDelayShouldPassWithoutErrors() {
        assertAllSignaturesAreValid(postForSimpleReport("hellopades-lt-sha256-ocsp-15min1s.pdf"));
    }


    @Test @Ignore //TODO: warnings are not included in report currently, turn this test on when or if they are included
    public void documentWithOcspOver15MinDelayShouldHaveCorrectWarningInReport() {
        SimpleReport report = postForSimpleReport("hellopades-lt-sha256-ocsp-15min1s.pdf");
        assertHasWarning(report, "ADEST_IOTNLABST_ANS", "The validation failed, because OCSP is too long after the best-signature-time!");
    }

    @Test
    public void documentWithOcspOver24hDelayShouldFail() {
        assertAllSignaturesAreInvalid(postForSimpleReport("hellopades-lt-sha256-ocsp-28h.pdf"));
    }

    @Test
    public void documentWithOcspOver24hDelayShouldDailWithCorrectErrorInReport() {
        SimpleReport report = postForSimpleReport("hellopades-lt-sha256-ocsp-28h.pdf");
        assertInvalidWithError(report, "ADEST_IOTNLABST_ANS", "The validation failed, because OCSP is too long after the best-signature-time!");
    }

    @Test
    @Ignore("VAL-98 File size limit exeeded with thsi file")
    public void documentWithNoOcspNorCrlInSignatureShouldFail() {
        assertAllSignaturesAreInvalid(postForSimpleReport("hellopades-lta-no-ocsp.pdf"));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithOcspTimeValueBeforeBestSignatureTimeShouldFail() {
        SimpleReport report = postForSimpleReport("hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf");
        assertInvalidWithError(report, "ADEST_IOABST_ANS", "The validation failed, because OCSP is before the best-signature-time!");
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
