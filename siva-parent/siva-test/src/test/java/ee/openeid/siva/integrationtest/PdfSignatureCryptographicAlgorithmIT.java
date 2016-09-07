package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfSignatureCryptographicAlgorithmIT extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signature_cryptographic_algorithm_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


    /**
     * TestCaseID: PDF-SigCryptoAlg-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: SHA512 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with SHA512 algorithm should pass
     *
     * File: hellopades-lt-sha512.pdf
     */
    @Test
    public void documentSignedWithSha512CertificateShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha512.pdf"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: SHA1 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with SHA1 algorithm should pass
     *
     * File: hellopades-lt-sha1.pdf
     */
    @Test
    public void documentSignedWithSha1CertificateShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha1.pdf"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: ECDSA algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with ECDSA algorithm should pass
     *
     * File: hellopades-ecdsa.pdf
     */
    @Test @Ignore //TODO: current test file's signature doesn't contain ocsp
    public void documentSignedWithSha256EcdsaAlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-ecdsa.pdf"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: ECDSA224 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with ECDSA224 algorithm should pass
     *
     * File: hellopades-lt-sha256-ec224.pdf
     */
    @Test
    public void documentSignedWithSha256Ec224AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-ec224.pdf"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: ECDSA256 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with ECDSA256 algorithm should pass
     *
     * File: hellopades-lt-sha256-ec256.pdf
     */
    @Test
    public void documentSignedWithSha256Ec256AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-ec256.pdf"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: RSA1024 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA1024 algorithm should pass
     *
     * File: hellopades-lt-sha256-rsa1024.pdf
     */
    @Test @Ignore //TODO: Need new test file as the cert has expired
    public void documentSignedWithSha256Rsa1024AlgoShouldPass() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha256-rsa1024.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa1024.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-7
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: RSA1023 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA1023 algorithm should pass
     *
     * File: hellopades-lt-sha256-rsa1023.pdf
     */
    @Test @Ignore //TODO: New test file may be needed! Current one has problems with expired signer cert. This should be removed as we do not support 1023?
    public void documentSignedWithRsa1023AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa1023.pdf"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: RSA2047 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA2047 algorithm should pass
     *
     * File: hellopades-lt-sha256-rsa2047.pdf
     */
    @Test @Ignore //TODO: New test file may be needed! Current one has problems with expired signer cert.
    public void documentSignedWithRsa2047AlgoShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa2047.pdf"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-9
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: RSA2048 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA2048 algorithm should pass
     *
     * File: PdfValidSingleSignature
     */
    @Test
    public void documentSignedWithRsa2048AlgoShouldPass() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreValid(postForReport("PdfValidSingleSignature.pdf"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
