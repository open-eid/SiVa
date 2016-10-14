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

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SignaturePolicyIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The PDF-file is not QES level and misses SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: soft-cert-signature.pdf
     */
    @Test
    public void pdfDocumentAdesNonSscdCompliantShouldPassWithGivenPolicy() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("soft-cert-signature.pdf"));
        post(validationRequestWithValidKeys(encodedString, "soft-cert-signature.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_1_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_1))
                .body("policy.policyUrl", Matchers.is(POLICY_1_URL))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The PDF-file is not QES level and misses SSCD/QSCD compliance
     *
     * Expected Result: Signatures are not valid according to policy
     *
     * File: soft-cert-signature.pdf
     */
    @Test
    public void pdfDocumentAdesNonSscdCompliantShouldFailWithGivenPolicy() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("soft-cert-signature.pdf"));
        post(validationRequestWithValidKeys(encodedString, "soft-cert-signature.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_2_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_2))
                .body("policy.policyUrl", Matchers.is(POLICY_2_URL))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("CHAIN_CONSTRAINTS_FAILURE"))
                .body("signatures[0].errors[0].content", Matchers.containsString("The certificate is not qualified!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The PDF-file is QES level and has SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy ("weaker" policy is used)
     *
     * File: pades_lt_two_valid_sig.pdf
     */
    @Test
    public void pdfDocumentQesSscdCompliantShouldPassWithAnyPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_two_valid_sig.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_1_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_1))
                .body("policy.policyUrl", Matchers.is(POLICY_1_URL))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The PDF-file is QES level and has SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: pades_lt_two_valid_sig.pdf
     */
    @Test
    public void pdfDocumentQesSscdCompliantShouldPassWithStrictPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_two_valid_sig.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_2_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_2))
                .body("policy.policyUrl", Matchers.is(POLICY_2_URL))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The bdoc is not QES level and misses SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     */
    @Test
    public void bdocDocumentAdesNonSscdCompliantShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_1_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_1))
                .body("policy.policyUrl", Matchers.is(POLICY_1_URL))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The bdoc is not QES level and misses SSCD/QSCD compliance
     *
     * Expected Result: Signatures are not valid according to policy
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     */
    @Test
    public void bdocDocumentAdesNonSscdCompliantShouldFailWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc", "bdoc", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_2_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_2))
                .body("policy.policyUrl", Matchers.is(POLICY_2_URL))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("CHAIN_CONSTRAINTS_FAILURE"))
                .body("signatures[0].errors[0].content", Matchers.is("The certificate is not supported by SSCD!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The bdoc is QES level and has SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy ("non-strict" policy is used)
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void bdocDocumentQesSscdCompliantShouldPassWithAnyPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_ID_sig.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_ID_sig.bdoc", "bdoc", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_1_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_1))
                .body("policy.policyUrl", Matchers.is(POLICY_1_URL))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The bdoc is QES level and has SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void bdocDocumentQesSscdCompliantShouldPassWithStrictPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_ID_sig.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_ID_sig.bdoc", "bdoc", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_2_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_2))
                .body("policy.policyUrl", Matchers.is(POLICY_2_URL))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
