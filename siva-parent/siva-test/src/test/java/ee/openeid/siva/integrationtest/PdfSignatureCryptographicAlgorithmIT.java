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

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
@Ignore("5.2 version failure")
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
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
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
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: SHA1 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with SHA1 algorithm should pass
     *
     * File: hellopades-lt-sha1.pdf
     */
    @Test
    public void documentSignedWithSha1CertificateShouldFail() {
        String filename = "hellopades-lt-sha1.pdf";
        String encodedString = Base64.encodeBase64String(readFileFromTestResources(filename));

        post(validationRequestWithDocumentTypeValidKeys(encodedString, filename, null, VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1)).log().all();

    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
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
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
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
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: RSA1024 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA1024 algorithm should pass
     *
     * File: hellopades-lt-sha256-rsa1024.pdf
     */
    @Test
    public void documentSignedWithSha256Rsa1024AlgoShouldPass() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha256-rsa1024.pdf"));

        post(validationRequestWithDocumentTypeValidKeys(encodedString, "hellopades-lt-sha256-rsa1024.pdf", null, VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: RSA1023 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA1023 algorithm should fail
     *
     * File: hellopades-lt-sha256-rsa1023.pdf
     */
    @Ignore //TODO Bug fixed in DSS version 5.1. https://ec.europa.eu/cefdigital/tracker/browse/DSS-1145
    @Test
    public void documentSignedWithRsa1023AlgoShouldFail() {
        post(validationRequestFor("hellopades-lt-sha256-rsa1023.pdf"))
                .then().
                body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT")); //Specific assertions needs to be added
    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: RSA2047 algorithms (PAdES Baseline LT)
     *
     * Expected Result: Document signed with RSA2047 algorithm should pass
     *
     * File: hellopades-lt-sha256-rsa2047.pdf
     */
    @Test
    public void documentSignedWithRsa2047AlgoShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-rsa2047.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: PDF-SigCryptoAlg-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
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
