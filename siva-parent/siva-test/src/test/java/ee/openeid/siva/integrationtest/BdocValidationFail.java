package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class BdocValidationFail extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with single invalid signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc
     ***/
    @Test
    public void bdocInvalidSingleSignature() {
        QualifiedReport report = postForReport("IB-3960_bdoc2.1_TSA_SignatureValue_altered.bdoc");
        assertAllSignaturesAreInvalid(report);
        assertTrue(report.getSignatures().get(0).getErrors().size() == 2);
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with multiple invalid signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocMultipleSignaturesInvalid.bdoc
     ***/
    @Test
    public void bdocInvalidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertAllSignaturesAreInvalid(postForReport("BdocMultipleSignaturesInvalid.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with multiple signatures both valid and invalid
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc
     ***/
    @Test
    public void bdocInvalidAndValidMultipleSignatures() {
        setTestFilesDirectory("bdoc/test/timemark/");
        assertSomeSignaturesAreValid(postForReport("BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc"),2);
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with no signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: BdocContainerNoSignature.bdoc
     ***/
    @Test
    public void bdocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreInvalid(postForReport("BdocContainerNoSignature.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with invalid mimetype in manifest
     *
     * Expected Result: document malformed error should be returned
     *
     * File: 23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc
     ***/
    @Test
    public void bdocMalformedBdocWithInvalidMimetypeInManifest() {
        post(validationRequestFor("23147_weak-warning-sha1-invalid-mimetype-in-manifest.bdoc"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));


    }

    /***
     * TestCaseID: Bdoc-ValidationFail-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice with wrong slash character ('\') in data file mime-type value
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-V-33.asice
     ***/
    @Test @Ignore //TODO: request returns error, needs investigation
    public void bdocInvalidMimeTypeChars() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-33.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems(""))
                .body("signatures[0].errors.content", Matchers.hasItems(""))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-7
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice time-stamp does not correspond to the SignatureValue element
     *
     * Expected Result: The document should fail the validation
     *
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     ***/
    @Test
    public void bdocInvalidTimeStampDontMatchSigValue() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("TS-02_23634_TS_wrong_SignatureValue.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("GENERIC"))
                .body("signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_CV_ISI_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /***
     * TestCaseID: Bdoc-ValidationFail-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Asice No non-repudiation key usage value in the certificate
     *
     * Expected Result: The document should fail the validation
     *
     * File: EE_SER-AEX-B-LT-I-43..asice
     ***/
    @Test
    public void bdocInvalidNonRepudiationKey() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-I-43.asice"))
                .then()
                .body("signatures[0].errors.nameId", Matchers.hasItems("BBB_XCV_ISCGKU_ANS"))
                .body("signatures[0].errors.content", Matchers.hasItems("The signer's certificate has not expected key-usage!"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("validSignaturesCount", Matchers.is(0));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
