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
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF has PAdES-B profile signature polv3
     *
     * Expected Result: Document validation should fail as the profile is not supported with any policy
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     */
    @Test
    public void baselineProfileBDocumentShouldFailpolv3() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-pades-b-sha256-auth.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-pades-b-sha256-auth.pdf", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF has PAdES-T profile signature polv3
     *
     * Expected Result: Document validation should fail with any policy
     *
     * File: pades-baseline-t-live-aj.pdf
     */
    @Test
    public void baselineProfileTDocumentShouldFailpolv3() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades-baseline-t-live-aj.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades-baseline-t-live-aj.pdf", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF has PAdES-LT profile signature polv3
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void baselineProfileLTDocumentShouldPasspolv3() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-pades-lt-sha256-sign.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-pades-lt-sha256-sign.pdf", VALID_SIGNATURE_POLICY_3))
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
     * TestCaseID: PDF-BaselineProfile-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF has PAdES-LT profile signature polv4
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void baselineProfileLTDocumentShouldPasspolv4() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-pades-lt-sha256-sign.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-pades-lt-sha256-sign.pdf", VALID_SIGNATURE_POLICY_4))
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
     * TestCaseID: PDF-BaselineProfile-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF has PAdES-LTA profile signature polv3
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    public void baselineProfileLTADocumentShouldPasspolv3() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades-baseline-lta-live-aj.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades-baseline-lta-live-aj.pdf", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF has PAdES-LTA profile signature polv4
     *
     * Expected Result: Document validation should pass with any policy
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    public void baselineProfileLTADocumentShouldPasspolv4() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades-baseline-lta-live-aj.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades-baseline-lta-live-aj.pdf", VALID_SIGNATURE_POLICY_4))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF has PAdES-LT and B profile signature
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void documentWithBaselineProfilesBAndLTSignaturesShouldFail() {
        post(validationRequestFor( "hellopades-lt-b.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("Signature/seal level do not meet the minimal level required by applied policy"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not a valid AdES!"))
                .body("validationReport.validationConclusion.signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("validationReport.validationConclusion.signatures[1].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[1].errors[0].content", Matchers.is("The expected format is not found!"))
                .body("validationReport.validationConclusion.signatures[1].warnings[0].content", Matchers.is("The signature/seal is not a valid AdES!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));

    }

    /**
     * TestCaseID: PDF-BaselineProfile-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: PDF document message digest attribute value does not match calculate value
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-lt1-lt2-wrongDigestValue.pdf
     */
    @Test
     public void documentMessageDigestAttributeValueDoesNotMatchCalculatedValue() {
        post(validationRequestFor("hellopades-lt1-lt2-wrongDigestValue.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("validationReport.validationConclusion.signatures[1].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[1].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[1].errors[0].content", Matchers.is("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.signatures[1].warnings", Matchers.hasSize(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
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
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt1-lt2-Serial.pdf", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
