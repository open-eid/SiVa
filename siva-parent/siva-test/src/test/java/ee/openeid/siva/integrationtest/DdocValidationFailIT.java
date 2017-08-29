/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

@Category(IntegrationTest.class)
public class DdocValidationFailIT extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc with single invalid signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: test1-ddoc-revoked.ddoc
     */
    @Test
    public void ddocInvalidSignature() {
        assertAllSignaturesAreInvalid(postForReport("test1-ddoc-revoked.ddoc"));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc with multiple invalid signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleInvalidSignatures.ddoc
     */
    @Test
    public void ddocInvalidMultipleSignatures() {
        assertAllSignaturesAreInvalid(postForReport("multipleInvalidSignatures.ddoc"));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc with multiple signatures both valid and invalid
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleValidAndInvalidSignatures.ddoc
     */
    @Test
    public void ddocInvalidAndValidMultipleSignatures() {
        assertSomeSignaturesAreValid(postForReport("multipleValidAndInvalidSignatures.ddoc"),2);
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc with no signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: DdocContainerNoSignature.bdoc
     */
    @Test
    public void ddocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreInvalid(postForReport("DdocContainerNoSignature.ddoc"));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc signature value has been changed (SignatureValue does not correspond to the SignedInfo block)
     *
     * Expected Result: The document should fail the validation
     *
     * File: test-inv-sig-inf.ddoc
     */
    @Test
    public void ddocSignatureValueChanged() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test-inv-sig-inf.ddoc"))
                .then()
                .body("signatures[0].errors[0].content", Matchers.containsString("Invalid signature value!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc Data file(s) don't match the hash values in Reference elements
     *
     * Expected Result: The document should fail the validation
     *
     * File: AndmefailiAtribuudidMuudetud.ddoc
     */
    @Test
    public void ddocDataFileHashMismatch() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("AndmefailiAtribuudidMuudetud.ddoc"))
                .then()
                .body("signatures[0].errors[0].content", Matchers.containsString("Bad digest for DataFile: D0"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc Baseline-BES file, no OCSP response
     *
     * Expected Result: The document should fail the validation
     *
     * File: ilma_kehtivuskinnituseta.ddoc
     */
    @Test
    public void ddocNoOCSPResponse() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("ilma_kehtivuskinnituseta.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has no OCSP confirmation!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc no non-repudiation key usage value in the certificate
     *
     * Expected Result: The document should fail the validation
     *
     * File: test-non-repu1.ddoc
     */
    @Test
    public void ddocNoNonRepudiationKey() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test-non-repu1.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Signers cert does not have non-repudiation bit set!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc Signer's certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: Belgia_kandeavaldus_LIV.ddoc
     */
    @Test
    public void ddocSignersCertNotTrusted() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("Belgia_kandeavaldus_LIV.ddoc"))
                .then()
                .body("signatures[1].errors.content", Matchers.hasItems("Signers cert not trusted, missing CA cert!"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc OCSP certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: Tundmatu_OCSP_responder.ddoc
     */
    @Test
    public void ddocOCSPNotTrusted() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("Tundmatu_OCSP_responder.ddoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].errors[2].content", Matchers.containsString("No certificate for responder: 'byName: CN=Belgium OCSP Responder"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc has unsigned data files in the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: lisatud_andmefail.ddoc
     */
    @Test
    public void ddocNonSignedFile() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("lisatud_andmefail.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Missing Reference for file: testfail2.txt"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc signed data file has been removed from the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: faileemald1.ddoc
     */
    @Test
    public void ddocFileRemoved() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("faileemald1.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Missing DataFile for signature: S0 reference #D0"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc wrong nonce
     *
     * Expected Result: The document should fail the validation
     *
     * File: OCSP nonce vale.ddoc
     */
    @Test
    public void ddocWrongOcspNonce() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("OCSP nonce vale.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Notarys digest doesn't match!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/
     *
     * Title: Ddoc with XML Entity expansion attack
     *
     * Expected Result: The document should fail the validation with error
     *
     * File: xml_expansion.ddoc
     */
    @Test
    public void ddocWithXMLEntityExpansionAttackShouldFail() {
        setTestFilesDirectory("ddoc/test/timemark/");
        post(validationRequestFor("xml_expansion.ddoc"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/
     *
     * Title: Ddoc with XML server side request forgery attack
     *
     * Expected Result: The document should fail the validation with error
     *
     * File: xml_entity.ddoc
     */
    @Test
    public void ddocWithXMLServerSideRequestForgeryAttackShouldFail() {
        setTestFilesDirectory("ddoc/test/timemark/");
        post(validationRequestFor("xml_entity.ddoc"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc no files in container
     *
     * Expected Result: The document should fail the validation
     *
     * File: KS-02_tyhi.ddoc
     */
    @Test
    public void ddocNoFilesInContainer() {
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("KS-02_tyhi.ddoc"));
        post(validationRequestWithValidKeys(encodedString, "KS-02_tyhi.ddoc", "ddoc", ""))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors.message", Matchers.hasItem(MAY_NOT_BE_EMPTY))
                .body("requestErrors.message", Matchers.hasItem(INVALID_BASE_64));
    }

    /**
     * TestCaseID: Ddoc-ValidationFail-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Ddoc XML namespace error in container
     *
     * Expected Result: The document should pass with warning
     *
     * File: ns6t3cp7.ddoc
     */
    @Test
    public void ddocNamespaceErrorShouldFail() {
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ns6t3cp7.ddoc"));
        post(validationRequestWithValidKeys(encodedString, "ns6t3cp7.ddoc", "ddoc", ""))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].warnings[0].description", Matchers.is("Bad digest for DataFile: D0 alternate digest matches!"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
