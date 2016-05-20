package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SignatureCryptographicAlgorithmTests extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "signature_cryptographic_algorithm_test_files/";

    @Test
    public void documentSignedWithSha512CertificateShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha512.pdf"));
    }

    @Test
    public void documentSignedWithSha1CertificateShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha1.pdf"));
    }

    @Ignore("TODO - current test file's signature doesn't contain ocsp")
    @Test
    public void documentSignedWithSha256EcdsaAlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-ecdsa.pdf"));
    }

    @Test
    public void documentSignedWithSha256Ec224AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-ec224.pdf"));
    }

    @Test
    public void documentSignedWithSha256Ec256AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-ec256.pdf"));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithSha256Rsa1024AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa1024.pdf"));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa1023AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa1023.pdf"));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa2047AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa2047.pdf"));
    }

    @Test @Ignore("TODO - a new test file is needed; the current one has issues with QC / SSCD")
    public void documentSignedWithRsa2048AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa2048.pdf"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
