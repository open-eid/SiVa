package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class DdocValidationFail extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with single invalid signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: test1-ddoc-revoked.ddoc
     ***/
    @Test
    public void ddocInvalidSignature() {
        assertAllSignaturesAreInvalid(postForReport("test1-ddoc-revoked.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with multiple invalid signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleInvalidSignatures.ddoc
     ***/
    @Test
    public void ddocInvalidMultipleSignatures() {
        assertAllSignaturesAreInvalid(postForReport("multipleInvalidSignatures.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with multiple signatures both valid and invalid
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleValidAndInvalidSignatures.ddoc
     ***/
    @Test
    public void ddocInvalidAndValidMultipleSignatures() {
        assertSomeSignaturesAreValid(postForReport("multipleValidAndInvalidSignatures.ddoc"),2);
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with no signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: DdocContainerNoSignature.bdoc
     ***/
    @Test
    public void ddocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreInvalid(postForReport("DdocContainerNoSignature.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc signature value has been changed (SignatureValue does not correspond to the SignedInfo block)
     *
     * Expected Result: The document should fail the validation
     *
     * File: test-inv-sig-inf.ddoc
     ***/
    @Test
    public void ddocSignatureValueChanged() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test-inv-sig-inf.ddoc"))
                .then()
                .body("signatures[0].errors[0].content", Matchers.containsString("Invalid signature value!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc Data file(s) don't match the hash values in Reference elements
     *
     * Expected Result: The document should fail the validation
     *
     * File: AndmefailiAtribuudidMuudetud.ddoc
     ***/
    @Test
    public void ddocDataFileHashMismatch() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("AndmefailiAtribuudidMuudetud.ddoc"))
                .then()
                .body("signatures[0].errors[0].content", Matchers.containsString("Bad digest for DataFile: D0"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-7
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc Baseline-BES file, no OCSP response
     *
     * Expected Result: The document should fail the validation
     *
     * File: ilma_kehtivuskinnituseta.ddoc
     ***/
    @Test
    public void ddocNoOCSPResponse() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("ilma_kehtivuskinnituseta.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has no OCSP confirmation!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc no non-repudiation key usage value in the certificate
     *
     * Expected Result: The document should fail the validation
     *
     * File: test-non-repu1.ddoc
     ***/
    @Test
    public void ddocNoNonRepudiationKey() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("test-non-repu1.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Signers cert does not have non-repudiation bit set!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc Signer's certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: Belgia_kandeavaldus_LIV.ddoc
     ***/
    @Test //TODO: If this test returns failure it could be because of hardcoded signatures position in returned report. Expectation is that this position is always same.
    public void ddocSignersCertNotTrusted() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("Belgia_kandeavaldus_LIV.ddoc"))
                .then()
                .body("signatures[1].errors.content", Matchers.hasItems("Signers cert not trusted, missing CA cert!"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-9
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc OCSP certificate is not trusted
     *
     * Expected Result: The document should fail the validation
     *
     * File: Tundmatu_OCSP_responder.ddoc
     ***/
    @Test //TODO: The error message content is not checked currently as it is really sloppy
    public void ddocOCSPNotTrusted() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("Tundmatu_OCSP_responder.ddoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-10
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc has unsigned data files in the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: lisatud_andmefail.ddoc
     ***/
    @Test
    public void ddocNonSignedFile() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("lisatud_andmefail.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Missing Reference for file: testfail2.txt"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-11
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc signed data file has been removed from the container
     *
     * Expected Result: The document should fail the validation
     *
     * File: faileemald1.ddoc
     ***/
    @Test
    public void ddocFileRemoved() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("faileemald1.ddoc"))
                .then()
                .body("signatures[0].errors.content", Matchers.hasItems("Missing DataFile for signature: S0 reference #D0"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
