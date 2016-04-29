package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SignatureRevocationValueTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "signature_revocation_value_test_files/";

    @Test
    public void documentWithOcspOver15MinDelayShouldPassButWarn() {
        /* TODO: implement finding errors by id from json
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-15min1s.pdf"))).
                andReturn().body().asString();
        assertEquals(
                "The validation failed, because OCSP is too long after the best-signature-time!",
                findWarningById("ADEST_IOTNLABST_ANS", detailedReport(httpBody)));
        */

        post(validationRequestFor("hellopades-lt-sha256-ocsp-15min1s.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test
    public void documentWithOcspOver24hDelayShouldFail() {
        /* TODO: implement finding errors by id from json
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-28h.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "The validation failed, because OCSP is too long after the best-signature-time!",
                findErrorById("ADEST_IOTNLABST_ANS", detailedReport(httpBody)));
        */
        post(validationRequestFor("hellopades-lt-sha256-ocsp-28h.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(0));
    }

    @Test
    @Ignore("VAL-98 File size limit exeeded with thsi file")
    public void documentWithNoOcspNorCrlInSignatureShouldFail() {
        post(validationRequestFor("hellopades-lta-no-ocsp.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(0));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithOcspTimeValueBeforeBestSignatureTimeShouldFail() {
        /* TODO: implement finding errors by id from json
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "The validation failed, because OCSP is before the best-signature-time!",
                findErrorById("ADEST_IOABST_ANS", detailedReport(httpBody)));
        */

        post(validationRequestFor("hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(0));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
