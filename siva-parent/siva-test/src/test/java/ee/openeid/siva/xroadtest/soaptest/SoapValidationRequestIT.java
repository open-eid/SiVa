package ee.openeid.siva.xroadtest.soaptest;


import ee.openeid.siva.soaptest.SiVaSoapTests;
import ee.openeid.siva.xroadtest.configuration.XroadIntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import static ee.openeid.siva.integrationtest.TestData.SOAP_ERROR_RESPONSE_PREFIX;

@Category(XroadIntegrationTest.class)
public class SoapValidationRequestIT extends SiVaSoapTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Soap-XroadValidationRequest-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: X-road file, not existing value in signaturePolicy
     *
     * Expected Result: Error is returned
     *
     * File: xroad-simple.asice
     */
    @Test
    public void soapXroadValidationRequestWrongSignaturePolicy() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestForDocumentExtended(encodedString, "xroad-simple.asice", "XROAD", INVALID_SIGNATURE_POLICY))
                .then()
                .rootPath(SOAP_ERROR_RESPONSE_PREFIX)
                .statusCode(HttpStatus.OK.value())
                .body("faultcode", Matchers.is(CLIENT_FAULT))
                .body("faultstring", Matchers.is("Invalid signature policy: " + INVALID_SIGNATURE_POLICY + "; Available abstractPolicies: [" + VALID_SIGNATURE_POLICY_3 + "]"));
    }

    /**
     * TestCaseID: Soap-XroadValidationRequest-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Mismatch in documentType and actual document (xroad and ddoc)
     *
     * Expected Result: Error is returned
     *
     * File: igasugust1.3.ddoc
     */
    @Test
    public void soapXroadValidationRequestNotMatchingDocumentTypeAndActualFileDdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("igasugust1.3.ddoc"));
        post(validationRequestForDocumentExtended(encodedString, "igasugust1.3.ddoc", "XROAD", "POLv3"))
                .then()
                .rootPath(SOAP_ERROR_RESPONSE_PREFIX)
                .statusCode(HttpStatus.OK.value())
                .body("faultcode", Matchers.is(CLIENT_FAULT))
                .body("faultstring", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }


    /**
     * TestCaseID: Soap-XroadValidationRequest-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Mismatch in documentType and actual document (pdf and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void soapXroadValidationRequestNotMatchingDocumentTypeAndActualFileBdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestForDocumentExtended(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "XROAD", VALID_SIGNATURE_POLICY_3))
                .then()
                .rootPath(SOAP_ERROR_RESPONSE_PREFIX)
                .statusCode(HttpStatus.OK.value())
                .body("faultcode", Matchers.is(CLIENT_FAULT))
                .body("faultstring", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Soap-XroadValidationRequest-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Mismatch in documentType and actual document (bdoc and pdf)
     *
     * Expected Result: Error is returned
     *
     * File: PdfValidSingleSignature.pdf
     */
    @Test
    public void soapXroadValidationRequestNotMatchingDocumentTypeAndActualFilePdf() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PdfValidSingleSignature.pdf"));
        post(validationRequestForDocumentExtended(encodedString, "PdfValidSingleSignature.pdf", "XROAD", "POLv3"))
                .then()
                .rootPath(SOAP_ERROR_RESPONSE_PREFIX)
                .statusCode(HttpStatus.OK.value())
                .body("faultcode", Matchers.is(CLIENT_FAULT))
                .body("faultstring", Matchers.is(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
