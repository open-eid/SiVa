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

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SignaturePolicyIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The PDF-file is Ades level
     *
     * Expected Result: Signatures are invalid according to policy
     *
     * File: soft-cert-signature.pdf
     */
    @Test
    public void pdfDocumentAdesNonSscdCompliantShouldFailWithGivenPolicy() {
        post(validationRequestFor("soft-cert-signature.pdf", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.containsString("Signature/seal level do not meet the minimal level required by applied policy"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[2].content", Matchers.is("The certificate is not qualified at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[3].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is Ades level
     *
     * Expected Result: Signatures are invalid according to policy
     *
     * File: allkiri_ades.asice
     */
    @Test
    public void bdocDocumentAdesNonSscdCompliantShouldFailWithGivenPolicy() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDD4j("allkiri_ades.asice", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("Signature/seal level do not meet the minimal level required by applied policy"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is Ades signature
     *
     * Expected Result: Signatures are invalid according to policy
     *
     * File:
     */
    @Ignore //TODO: test file needed
    @Test
    public void bdocDocumentAdesSigShouldFail() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The asice is Ades signature
     *
     * Expected Result: Signatures are invalid according to policy
     *
     * File:
     */
    @Ignore //TODO: test file needed
    @Test
    public void asiceDocumentAdesSigShouldFail() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is Ades seal
     *
     * Expected Result: Signatures are invalid according to policy
     *
     * File:
     */
    @Ignore //TODO: test file needed
    @Test
    public void bdocDocumentAdesSealShouldFail() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The asice is ades seal
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File:
     */
    @Ignore //TODO: test file needed
    @Test
    public void asiceDocumentAdesSealShouldFail() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, warning is returned about signature level
     *
     * File: testAdesQC.asice
     */
    @Test
    public void asiceDocumentAdesQcSigCompliantShouldPassWithWarning() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("testAdesQC.asice", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature is not in the Qualified Electronic Signature level"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, warning is returned about signature level
     *
     * File: testAdesQC.asice
     */
    @Test
    public void bdocDocumentAdesQcSigCompliantShouldPassWithWarning() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("testAdesQC.asice", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature is not in the Qualified Electronic Signature level"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File: IB-4828_tempel_not_qscd_TS.asice
     */
    @Test
    public void asiceDocumentAdesQCCompliantSealShouldPass() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("IB-4828_tempel_not_qscd_TS.asice", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File: IB-4828_tempel_not_qscd_TM.bdoc
     */
    @Test
    public void bdocDocumentAdesQCCompliantSealShouldPass() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("IB-4828_tempel_not_qscd_TM.bdoc", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File:
     */
    @Ignore //TODO: file needed
    @Test
    public void asiceDocumentAdesQCCompliantNoTypeShouldFail() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File:
     */
    @Ignore //TODO: file needed
    @Test
    public void bdocDocumentAdesQCCompliantNoTypeShouldFail() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level signature
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void bdocDocumentQesigShouldPassWithStrictPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("Valid_ID_sig.bdoc", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level signature
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: ValidLiveSignature.asice
     */
    @Test
    public void asiceDocumentQesigShouldPassWithStrictPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("ValidLiveSignature.asice", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level seal
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: IB-4828_tempel_qscd_TM.bdoc
     */
    @Test
    public void bdocDocumentQesealShouldPassWithStrictPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("IB-4828_tempel_qscd_TM.bdoc", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The asice is QES level seal
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: IB-4828_tempel_qscd_TS.asice
     */
    @Test
    public void asiceDocumentQesealShouldPassWithStrictPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("IB-4828_tempel_qscd_TS.asice", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     */
    @Test
    public void bdocDocumentQesNoTypeShouldPassWithStrictPolicy() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv4-Signature-Policy-18
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The asice is QES level
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: EE_SER-AEX-B-LT-V-28.asice
     */
    @Test
    public void asiceDocumentQesNoTypeShouldPassWithStrictPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-28.asice", null, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3
     *
     * Title: The PDF-file is Ades level
     *
     * Expected Result: Signatures are not valid according to policy
     *
     * File: soft-cert-signature.pdf
     */
    @Test
    public void pdfDocumentAdesNonSscdCompliantShouldPassWithGivenPolicy() {
        post(validationRequestFor("soft-cert-signature.pdf", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[2].content", Matchers.is("The certificate is not qualified at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[3].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3
     *
     * Title: The bdoc is Ades level
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: allkiri_ades.asice
     */
    @Test
    public void bdocDocumentAdesNonSscdCompliantShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDD4j("allkiri_ades.asice", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3
     *
     * Title: The bdoc is ADES level signature
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: allkiri_ades.asice
     */
    @Ignore //TODO: test file needed
    @Test
    public void bdocDocumentAdesSigShouldPass() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3
     *
     * Title: The asice is Ades level signature
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File:
     */
    @Ignore //TODO: test file needed
    @Test
    public void asiceDocumentAdesSigShouldPass() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3
     *
     * Title: The bdoc is Ades seal
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File:
     */
    @Ignore //TODO: test file needed
    @Test
    public void bdocDocumentAdesSealShouldPass() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-3-polv3
     *
     * Title: The asice is Ades level seal
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File:
     */
    @Ignore //TODO: test file needed
    @Test
    public void asiceDocumentAdesSealShouldPass() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDD4j("", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, warning is returned about signature level
     *
     * File: testAdesQC.asice
     */
    @Test
    public void asiceDocumentAdesQcSicShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("testAdesQC.asice", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level signature, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, warning is returned about signature level
     *
     * File: testAdesQC.asice
     */
    @Test
    public void bdocDocumentAdesQcSigShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDD4j("testAdesQC.asice", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File: IB-4828_tempel_not_qscd_TS.asice
     */
    @Test
    public void asiceDocumentAdesQcCompliantSealShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("IB-4828_tempel_not_qscd_TS.asice", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level seal, but do not have SSCD/QSCD compliance
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File: IB-4828_tempel_not_qscd_TM.bdoc
     */
    @Test
    public void bdocDocumentAdesQCCompliantSealShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("IB-4828_tempel_not_qscd_TM.bdoc", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESEAL_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File:
     */
    @Ignore //TODO: file needed
    @Test
    public void asiceDocumentAdesQCCompliantNoTypeShouldPass() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The certificate is QC level, but do not have SSCD/QSCD compliance and type identifier
     *
     * Expected Result: Signatures are valid according to policy, no warning about the signature level
     *
     * File:
     */
    @Ignore //TODO: file needed
    @Test
    public void bdocDocumentAdesQCCompliantNoTypeShouldPass() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADES_QC"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level signature
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void bdocDocumentQesigShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("Valid_ID_sig.bdoc", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level signature
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: ValidLiveSignature.asice
     */
    @Test
    public void asiceDocumentQesigShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("ValidLiveSignature.asice", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level seal
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: IB-4828_tempel_qscd_TM.bdoc
     */
    @Test
    public void bdocDocumentQesealShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("IB-4828_tempel_qscd_TM.bdoc", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The asice is QES level seal
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: IB-4828_tempel_qscd_TS.asice
     */
    @Test
    public void asiceDocumentQesealShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("IB-4828_tempel_qscd_TS.asice", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESEAL"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The bdoc is QES level
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     */
    @Test
    public void bdocDocumentQesNoTypeShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestForDD4j("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: POLv3-Signature-Policy-18
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
     *
     * Title: The asice is QES level
     *
     * Expected Result: Signatures are valid according to policy
     *
     * File: EE_SER-AEX-B-LT-V-28.asice
     */
    @Test
    public void asiceDocumentQesNoTypeShouldPassWithGivenPolicy() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-28.asice", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_3_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_3))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_3_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Revocation-Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
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
        post(validationRequestFor("PadesProfileT.pdf", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The expected format is not found!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Revocation-Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-4-polv4
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
        post(validationRequestFor("PadesProfileLtWithCrl.pdf", VALID_SIGNATURE_POLICY_4, null))
                .then()
                .body("validationReport.validationConclusion.policy.policyDescription", Matchers.is(POLICY_4_DESCRIPTION))
                .body("validationReport.validationConclusion.policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_4))
                .body("validationReport.validationConclusion.policy.policyUrl", Matchers.is(POLICY_4_URL))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

}
