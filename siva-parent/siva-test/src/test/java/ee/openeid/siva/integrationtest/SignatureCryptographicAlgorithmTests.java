package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SignatureCryptographicAlgorithmTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "signature_cryptographic_algorithm_test_files/";

    @Test
    public void documentSignedWithSha512CertificateShouldPass() {
        post(validationRequestFor("hellopades-lt-sha512.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test
    public void documentSignedWithSha1CertificateShouldPass() {
        post(validationRequestFor("hellopades-lt-sha1.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Ignore("TODO - current test file's signature doesn't contain ocsp")
    @Test
    public void documentSignedWithSha256EcdsaAlgoShouldPass() {
        post(validationRequestFor("hellopades-ecdsa.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test
    public void documentSignedWithSha256Ec224AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-ec224.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test
    public void documentSignedWithSha256Ec256AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-ec256.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithSha256Rsa1024AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-rsa1024.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa1023AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-rsa1023.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa2047AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-rsa2047.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa2048AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-rsa2048.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
