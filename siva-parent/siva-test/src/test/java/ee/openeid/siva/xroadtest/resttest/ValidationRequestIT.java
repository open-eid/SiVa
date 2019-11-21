package ee.openeid.siva.xroadtest.resttest;

import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.xroadtest.configuration.XroadIntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;

@Category(XroadIntegrationTest.class)
public class ValidationRequestIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    static int getRequestErrorsCount(String json, String field, String message) {
        List<Map> errors = from(json).get("requestErrors.findAll { requestError -> requestError.key == '" + field + "' && requestError.message=='" + message + "' }");
        return errors.size();
    }

    public static String getFailMessageForKey(String key) {
        return key + " error or corresponding message was not in the response";
    }

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Happy path valid all input test
     *
     * Expected Result: Validation report is returned
     *
     * File: xroad-simple.asice
     */
    @Test
    public void validationRequestAllInputs() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "xroad-simple.asice", "XROAD", "POLv3"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("validatedDocument.filename", equalTo("xroad-simple.asice"))
                .body("policy.policyName", equalTo("POLv3"))
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-18
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: DocumentType parameter is XROAD
     *
     * Expected Result: Xroad validatior is used for validation and report is returned
     *
     * File: xroad-simple.asice
     */
    @Test
    public void validationRequestDocumentTypeXroad() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "xroad-simple.asice");
        jsonObject.put(DOCUMENT_TYPE, "XROAD");

        post(jsonObject.toString())
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Input random base64 string as document with xroad document type
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: not relevant
     */
    @Test
    public void validationRequestRandomInputAsXroadDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "some_pdf.asice", "xroad", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Mismatch in stated and actual document (xroad and ddoc)
     *
     * Expected Result: Error is returned
     *
     * File: igasugust1.3.ddoc
     */
    @Test
    public void xroadValidationRequestNotMatchingDocumentTypeAndActualFileDdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("igasugust1.3.ddoc"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "igasugust1.3.ddoc", "xroad", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
