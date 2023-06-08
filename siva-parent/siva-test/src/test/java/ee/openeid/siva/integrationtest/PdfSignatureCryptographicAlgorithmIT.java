/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.integrationtest;

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;

@Tag("IntegrationTest")
class PdfSignatureCryptographicAlgorithmIT extends SiVaRestTests{

    @BeforeEach
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: SHA512 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with SHA512 algorithm should pass
     *
     * File: hellopades-lt-sha512.pdf
     */
    @Test
    void documentSignedWithSha512CertificateShouldPass() {
        post(validationRequestFor("hellopades-lt-sha512.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: SHA1 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with SHA1 algorithm should pass
     *
     * File: hellopades-lt-sha1.pdf
     */
    @Test
    void documentSignedWithSha1CertificateShouldFail() {
        String filename = "hellopades-lt-sha1.pdf";
        String encodedString = Base64.encodeBase64String(readFileFromTestResources(filename));

        post(validationRequestWithDocumentTypeValidKeys(encodedString, filename, null, VALID_SIGNATURE_POLICY_3))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: ECDSA224 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with ECDSA224 algorithm should fail
     *
     * File: hellopades-lt-sha256-ec224.pdf
     */
    @Test
    void documentSignedWithSha256Ec224AlgoShouldFail() {
        post(validationRequestFor("hellopades-lt-sha256-ec224.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("INDETERMINATE_QESIG"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].errors.content", Matchers.hasItem("The algorithm ECDSA with key size 224 is too small for signature creation!"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: ECDSA256 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with ECDSA256 algorithm should pass
     *
     * File: hellopades-lt-sha256-ec256.pdf
     */
    @Test
    void documentSignedWithSha256Ec256AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-ec256.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: RSA1024 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA1024 algorithm should pass
     *
     * File: hellopades-lt-sha256-rsa1024.pdf
     */
    @Test
    void documentSignedWithSha256Rsa1024AlgoShouldPass() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha256-rsa1024.pdf"));

        post(validationRequestWithDocumentTypeValidKeys(encodedString, "hellopades-lt-sha256-rsa1024.pdf", null, VALID_SIGNATURE_POLICY_3))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: RSA1023 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA1023 algorithm should fail
     *
     * File: hellopades-lt-sha256-rsa1023.pdf
     */
    @Test
    void documentSignedWithRsa1023AlgoShouldFail() {
        post(validationRequestFor("hellopades-lt-sha256-rsa1023.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("CRYPTO_CONSTRAINTS_FAILURE_NO_POE"))
                .body("signatures[0].errors.content", Matchers.hasItem("The past signature validation is not conclusive!"));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: RSA2047 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA2047 algorithm should pass
     *
     * File: hellopades-lt-sha256-rsa2047.pdf
     */
    @Test
    void documentSignedWithRsa2047AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-rsa2047.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: RSA2048 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA2048 algorithm should pass
     *
     * File: PdfValidSingleSignature
     */
    @Test
    void documentSignedWithRsa2048AlgoShouldPass() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("PdfValidSingleSignature.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
