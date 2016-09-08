package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfBaselineProfileIT extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";

    /**
     * TestCaseID: PDF-BaselineProfile-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-B profile signature
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     */
    @Test
    public void baselineProfileBDocumentShouldFail() {
        QualifiedReport report = postForReport("hellopades-pades-b-sha256-auth.pdf");
        assertAllSignaturesAreInvalid(report);
    }

    /**
     * TestCaseID: PDF-BaselineProfile-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-T profile signature
     *
     * Expected Result: Document validation should fail
     *
     * File:
     */
    @Ignore
    @Test //TODO: need test file
    public void baselineProfileTDocumentShouldFail() {
        QualifiedReport report = postForReport("some_file.pdf");
        assertInvalidWithError(report.getSignatures().get(0), "The signature format is not allowed by the validation policy constraint!");
    }

    /**
     * TestCaseID: PDF-BaselineProfile-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LT profile signature
     *
     * Expected Result: Document validation should pass
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void baselineProfileLTDocumentShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-pades-lt-sha256-sign.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LTA profile signature
     *
     * Expected Result: Document validation should pass
     *
     * File:
     */
    @Test @Ignore //TODO: need test file
    public void baselineProfileLTADocumentShouldPass() {
        assertAllSignaturesAreValid(postForReport("some_file.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF has PAdES-LT and B profile signature
     *
     * Expected Result: Document validation should fail
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void documentWithBaselineProfilesBAndLTSignaturesShouldFail() {
        QualifiedReport report = postForReport("hellopades-lt-b.pdf");
        assertSomeSignaturesAreValid(report, 1);
    }

    /**
     * TestCaseID: PDF-BaselineProfile-6
     *
     * TestType: Automated
     *
     * RequirementID:
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
                .body("signatures[1].signatureLevel", Matchers.is("AdES"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[1].errors", Matchers.hasSize(4))
                .body("signatures[1].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-7
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: PDF file with a serial signature
     *
     * Expected Result: Document signed with multiple signers with serial signatures should pass
     *
     * File: hellopades-lt1-lt2-Serial.pdf
     */
    @Test @Ignore //TODO: Warnings are not returned when validationLevel is set to ARCHIVAL_DATA (default level)
    public void documentSignedWithMultipleSignersSerialSignature() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt1-lt2-Serial.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt1-lt2-Serial.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LTA"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: PDF document signed with multiple signers parallel signature
     *
     * Expected Result: Document with parallel signatures should pass
     *
     * File: hellopades-lt1-lt2-parallel3.pdf
     */
    @Test
    public void documentSignedWithMultipleSignersParallelSignature() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt1-lt2-parallel3.pdf"));
    }

    /**
     * TestCaseID: PDF-BaselineProfile-9
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: PDF document signed with multiple signers parallel signature without Sscd
     *
     * Expected Result: Document with no qualified and without SSCD should warn
     *
     * File: hellopades-lt1-lt2-parallel3.pdf
     */
    @Test @Ignore //TODO: Warnings are not returned when validationLevel is set to ARCHIVAL_DATA (default level)
    public void ifSignerCertificateIsNotQualifiedAndWithoutSscdItIsAcceptedWithWarning() {
        QualifiedReport report = postForReport("hellopades-lt1-lt2-parallel3.pdf");
        assertHasWarning(report.getSignatures().get(0), "The certificate is not qualified!");
        assertHasWarning(report.getSignatures().get(1), "The certificate is not qualified!");
        assertAllSignaturesAreValid(report);
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
