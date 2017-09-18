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
import org.junit.Test;

public class SignaturePolicyIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
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
        post(validationRequestWithValidKeys(encodedString, "soft-cert-signature.pdf", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
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
        post(validationRequestWithValidKeys(encodedString, "soft-cert-signature.pdf", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_5_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_5))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_5_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationConclusion.signatures[0].subIndication", Matchers.is("CHAIN_CONSTRAINTS_FAILURE"))
                .body("validationConclusion.signatures[0].errors[0].content", Matchers.containsString("The certificate is not qualified!"))
                .body("validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
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
        post(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationConclusion.signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
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
        post(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_5_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_5))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_5_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationConclusion.signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
     *
     * Title: The PDF-file is missing an OCSP or CRL
     *
     * Expected Result: Signatures are invalid according to policy
     *
     * File: PadesProfileT.pdf
     */
    @Test
    public void pdfDocumentWithoutRevocationInfoShouldFail() {
        setTestFilesDirectory("signature_policy_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PadesProfileT.pdf"));
        post(validationRequestWithValidKeys(encodedString, "PadesProfileT.pdf", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_5_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_5))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_5_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-T"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationConclusion.signatures[0].errors[0].content", Matchers.is("The expected format is not found!"))
                .body("validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
     *
     * Title: The PDF-file with included CRL
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: PadesProfileLtWithCrl.pdf
     */
    @Test
    public void pdfDocumentWithCrlAsRevocationInfoShouldPass() {
        setTestFilesDirectory("signature_policy_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PadesProfileLtWithCrl.pdf"));
        post(validationRequestWithValidKeys(encodedString, "PadesProfileLtWithCrl.pdf", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_5_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_5))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_5_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv4
     *
     * Title: The PDF-file with AdesQC signature level with corresponding policy
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: PadesTestAdesQC.pdf
     */
    @Test
    public void pdfDocumentWithAdesQcSignatureShouldPass() {
        setTestFilesDirectory("signature_policy_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PadesTestAdesQC.pdf"));
        post(validationRequestWithValidKeys(encodedString, "PadesTestAdesQC.pdf", VALID_SIGNATURE_POLICY_4))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv5
     *
     * Title: The PDF-file with AdesQC signature level with stricter policy
     *
     * Expected Result: Signatures are invalid according to policy
     *
     * File: PadesTestAdesQC.pdf
     */
    @Test
    public void pdfDocumentWithAdesQcSignatureShouldFail() {
        setTestFilesDirectory("signature_policy_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PadesTestAdesQC.pdf"));
        post(validationRequestWithValidKeys(encodedString, "PadesTestAdesQC.pdf", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_5_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_5))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_5_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationConclusion.signatures[0].subIndication", Matchers.is("CHAIN_CONSTRAINTS_FAILURE"))
                .body("validationConclusion.signatures[0].errors[0].content", Matchers.is("The certificate is not supported by QSCD!"))
                .body("validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-Signature-Policy-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv4
     *
     * Title: The PDF-file is not AdesQC level and misses SSCD/QSCD compliance
     *
     * Expected Result: Signatures are not valid according to policy
     *
     * File: soft-cert-signature.pdf
     */
    @Test
    public void pdfDocumentAdesNonSscdCompliantShouldFailWithAdesQcPolicy() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("soft-cert-signature.pdf"));
        post(validationRequestWithValidKeys(encodedString, "soft-cert-signature.pdf", VALID_SIGNATURE_POLICY_4))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationConclusion.signatures[0].subIndication", Matchers.is("CHAIN_CONSTRAINTS_FAILURE"))
                .body("validationConclusion.signatures[0].errors[0].content", Matchers.containsString("The certificate is not qualified!"))
                .body("validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
     *
     * Title: The bdoc is not QES level and misses SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: allkiri_ades.asice
     */
    @Test
    public void bdocDocumentAdesNonSscdCompliantShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("allkiri_ades.asice"));
        post(validationRequestWithValidKeys(encodedString, "allkiri_ades.asice", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
     *
     * Title: The bdoc is not QES level and misses SSCD/QSCD compliance
     *
     * Expected Result: Signatures are not valid according to policy
     *
     * File: allkiri_ades.asice
     */
    @Test
    public void bdocDocumentAdesNonSscdCompliantShouldFailWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("allkiri_ades.asice"));
        post(validationRequestWithValidKeys(encodedString, "allkiri_ades.asice", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_5_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_5))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_5_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-LT"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationConclusion.signatures[0].subIndication", Matchers.is("CHAIN_CONSTRAINTS_FAILURE"))
                .body("validationConclusion.signatures[0].errors[0].content", Matchers.is("The certificate is not qualified!"))
                .body("validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
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
        post(validationRequestWithValidKeys(encodedString, "Valid_ID_sig.bdoc", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-Signature-Policy-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv3
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
        post(validationRequestWithValidKeys(encodedString, "Valid_ID_sig.bdoc", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.policy.policyDescription", Matchers.is(POLICY_5_DESCRIPTION))
                .body("validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_5))
                .body("validationConclusion.policy.policyUrl", Matchers.is(POLICY_5_URL))
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationConclusion.signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

}
