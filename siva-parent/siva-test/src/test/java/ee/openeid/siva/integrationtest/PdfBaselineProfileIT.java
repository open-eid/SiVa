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

@Category(IntegrationTest.class)
public class PdfBaselineProfileIT extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: PDF-BaselineProfile-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-B profile signature POLv1
     *
     * Expected Result: Document validation should fail as the profile is not supported with any policy
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     */
    @Test
    public void baselineProfileBDocumentShouldFailPolv1() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-pades-b-sha256-auth.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-pades-b-sha256-auth.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-B profile signature POLv2
     *
     * Expected Result: Document validation should fail as the profile is supported with any policy
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     */
    @Test
    public void baselineProfileBDocumentShouldFailPolv2() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-pades-b-sha256-auth.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-pades-b-sha256-auth.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-T profile signature POLv1
     *
     * Expected Result: Document validation should fail with any policy
     *
     * File: pades-baseline-t-live-aj.pdf
     */
    @Test
    public void baselineProfileTDocumentShouldFailPolv1() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades-baseline-t-live-aj.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades-baseline-t-live-aj.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-T profile signature POLv2
     *
     * Expected Result: Document validation should fail with any policy
     *
     * File: pades-baseline-t-live-aj.pdf
     */
    @Test
    public void baselineProfileTDocumentShouldFailPolv2() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades-baseline-t-live-aj.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades-baseline-t-live-aj.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-LT profile signature POLv1
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void baselineProfileLTDocumentShouldPassPolv1() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-pades-lt-sha256-sign.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-pades-lt-sha256-sign.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-LT profile signature POLv2
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void baselineProfileLTDocumentShouldPassPolv2() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-pades-lt-sha256-sign.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-pades-lt-sha256-sign.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-LTA profile signature POLv1
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    public void baselineProfileLTADocumentShouldPassPolv1() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades-baseline-lta-live-aj.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades-baseline-lta-live-aj.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-LTA profile signature POLv2
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    public void baselineProfileLTADocumentShouldPassPolv2() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades-baseline-lta-live-aj.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades-baseline-lta-live-aj.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has PAdES-LT and B profile signature
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void documentWithBaselineProfilesBAndLTSignaturesShouldFail() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-b.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-b.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("signatures[1].signatureLevel", Matchers.is("QES"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].subIndication", Matchers.is(""))
                .body("signatures[1].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("signatures[1].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: PDF document message digest attribute value does not match calculate value
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-lt1-lt2-wrongDigestValue.pdf
     */
    @Test
    public void documentMessageDigestAttributeValueDoesNotMatchCalculatedValue() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-wrongDigestValue.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt1-lt2-wrongDigestValue.pdf", "pdf", ""))
                .then()
                .body("signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("signatures[1].signatureLevel", Matchers.is("QES"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[1].errors", Matchers.hasSize(4))
                .body("signatures[1].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: PDF file with a serial signature
     *
     * Expected Result: Document signed with multiple signers with serial signatures should pass
     *
     * File: hellopades-lt1-lt2-Serial.pdf
     */
    @Test
    public void documentSignedWithMultipleSignersSerialSignature() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-Serial.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt1-lt2-Serial.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
