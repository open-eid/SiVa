package ee.openeid.siva.integrationtest;

import ee.openeid.siva.validation.document.report.QualifiedReport;
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

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "signature_policy_test_files/";

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
     * File:
     */
    @Test @Ignore //TODO: Need proper file
    public void pdfDocumentAdesNonSscdCompliantShouldPassWithGivenPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-parallel3.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa1024.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
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
     * File:
     */
    @Test @Ignore //TODO: Need proper file
    public void pdfDocumentAdesNonSscdCompliantShouldFailWithGivenPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-parallel3.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa1024.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_2_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_2))
                .body("policy.policyUrl", Matchers.is(POLICY_2_URL))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
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
     * File:
     */
    @Test @Ignore //TODO: Need proper file
    public void pdfDocumentQesSscdCompliantShouldPassWithAnyPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-parallel3.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa1024.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
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
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
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
     * File:
     */
    @Test @Ignore //TODO: Need proper file
    public void pdfDocumentQesSscdCompliantShouldPassWithStrictPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-parallel3.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa1024.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
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
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
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
     * File:
     */
    @Test @Ignore //TODO: Need proper file
    public void bdocDocumentAdesNonSscdCompliantShouldPassWithGivenPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources(""));
        post(validationRequestWithValidKeys(encodedString, "", "bdoc", VALID_SIGNATURE_POLICY_1))
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
     * File:
     */
    @Test @Ignore //TODO: Need proper file
    public void bdocDocumentAdesNonSscdCompliantShouldFailWithGivenPolicy() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources(""));
        post(validationRequestWithValidKeys(encodedString, "", "bdoc", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("policy.policyDescription", Matchers.is(POLICY_2_DESCRIPTION))
                .body("policy.policyName", Matchers.is(VALID_SIGNATURE_POLICY_2))
                .body("policy.policyUrl", Matchers.is(POLICY_2_URL))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
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
     * Expected Result: Signatures are valid according to policy ("weaker" policy is used)
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void bdocDocumentQesSscdCompliantShouldPassWithAnyPolicy() {
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

    /**
     * TestCaseID: Signature-Policy-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: Document with over 24h delay should fail when signature policy is set to strict
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test @Ignore //TODO: With DigiDoc4J 1.0.4 update this error functionality is no longer implemented
    public void pdfDocumentWithOcspOver24hDelayWithEEPolicyShouldFailWithCorrectErrorInReport() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreInvalid(report);
    }

    /**
     * TestCaseID: Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: Document with over 24h delay should pass when signature policy is set to "EU"
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test
    public void pdfDocumentWithOcspOver24hDelayWithEUPolicyShouldPassWithoutErrors() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", VALID_SIGNATURE_POLICY_2);
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: Signature-Policy-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/interface_description/#validation-request-interface
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: Document with over 24h delay should fail when signature policy is not set or empty, because it defaults to "EE"
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test @Ignore //TODO: With DigiDoc4J 1.0.4 update this error functionality is no longer implemented
    public void ifSignaturePolicyIsNotSetOrEmptyPdfDocumentShouldGetValidatedAgainstEEPolicy() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", null);
        assertAllSignaturesAreInvalid(report);

        report = postForReport("hellopades-lt-sha256-ocsp-28h.pdf", "");
        assertAllSignaturesAreInvalid(report);
    }

    /**
     * TestCaseID: Signature-Policy-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: The PDF has LT and B profile signatures
     *
     * Expected Result: 1 of 2 signatures' should pass when signature policy is set to "EE"
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void pdfDocumentWithBaselineProfilesBAndLTSignaturesValidatedAgainstEEPolicyOnlyLTShouldPass() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", VALID_SIGNATURE_POLICY_2);
        assertSomeSignaturesAreValid(report, 1);
    }

    /**
     * TestCaseID: Signature-Policy-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The PDF has LT and B profile signatures
     *
     * Expected Result: 2 of 2 signatures' validation should pass when signature policy is set to "EU"
     *
     * File: hellopades-lt-b.pdf
     */
    @Test @Ignore //TODO: VAL-331 changed constraint for polv1 to accept only BASELINE_LT & BASELINE_LTA signature formats
    public void pdfDocumentWithBaselineProfilesBAndLTSignaturesValidatedAgainstEUPolicyBothShouldPass() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: Signature-Policy-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-1-polv1
     *
     * Title: The PDF has LT and B profile signatures
     *
     * Expected Result: 2 of 2 signatures' validation should pass when signature policy is set to "eU"
     *
     * File: hellopades-lt-b.pdf
     */
    @Test @Ignore //TODO: VAL-331 changed constraint for polv1 to accept only BASELINE_LT & BASELINE_LTA signature formats
    public void testPdfDocumentSignaturePolicyCaseInsensitivity() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        QualifiedReport report = postForReport("hellopades-lt-b.pdf", SMALL_CASE_VALID_SIGNATURE_POLICY_1);
        assertAllSignaturesAreValid(report);
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
