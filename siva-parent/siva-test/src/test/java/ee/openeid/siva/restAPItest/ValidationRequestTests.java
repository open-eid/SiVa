package ee.openeid.siva.restAPItest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;


@Category(IntegrationTest.class)
public class ValidationRequestTests extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


    /***
     *
     * TestCaseID: ValidationRequest-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Input random base64 string as document with bdoc document type
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestRandomInputAsBdocDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "bdoc", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Input random base64 string as document with pdf document type
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestRandomInputAsPdfDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "some_pdf.pdf", "pdf", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-3
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Input random base64 string as document with ddoc document type
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File:
     *
     ***/
    @Test
    public void ValidationRequestRandomInputAsDdocDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "some_pdf.ddoc", "ddoc", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-4
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Input a request with empty values
     *
     * Expected Result: Errors are returned stating the missing values
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestEmptyInputs() {
        String json = post(validationRequestWithValidKeys("", "", "", "")).asString();

        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(json, DOCUMENT_TYPE, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(REPORT_TYPE), getRequestErrorsCount(json, REPORT_TYPE, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(json, FILENAME, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, MAY_NOT_BE_EMPTY)==1);

        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(json, DOCUMENT_TYPE, INVALID_DOCUMENT_TYPE)==1);
        assertTrue(getFailMessageForKey(REPORT_TYPE), getRequestErrorsCount(json, REPORT_TYPE, INVALID_REPORT_TYPE)==1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(json, FILENAME, INVALID_FILENAME)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, INVALID_BASE_64)==1);
    }

    private int getRequestErrorsCount(String json, String field, String message) {
        List<Map> errors = from(json).get("requestErrors.findAll { requestError -> requestError.key == '"+field+"' && requestError.message=='"+message+"' }");
        return errors.size();
    }

    private String getFailMessageForKey(String key) {
        return key + " error or corresponding message was not in the response";
    }

    /***
     *
     * TestCaseID: ValidationRequest-5
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request with not base64 string as document
     *
     * Expected Result: Error is returned stating encoding problem
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestNonBase64Input() {
        String encodedString = ",:";
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "bdoc", "simple"))
                .then()
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_BASE_64));
    }

    /***
     *
     * TestCaseID: ValidationRequest-6
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of wrong report type as input
     *
     * Expected Result: Error is returned stating wrong report type
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestInvalidReportType() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "bdoc", "Complicated"))
                .then()
                .body("requestErrors[0].key", Matchers.is(REPORT_TYPE))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_REPORT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-7
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of valid (but not simple) report type as input
     *
     * Expected Result: Correct report is returned (currently simple report is returned in all cases)
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test //todo revisit this test when decision has been made regarding other types of reports than simple
    public void ValidationRequestValidReportType() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "bdoc", "diagnosticdata"))
                .then()
                .body("documentName",equalTo("Valid_IDCard_MobID_signatures.bdoc"));
    }

    /***
     *
     * TestCaseID: ValidationRequest-8
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of wrong document type as input
     *
     * Expected Result: Correct error code is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestInvalidDocumentType() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "cdoc", "simple"))
                .then()
                .body("requestErrors[0].key", Matchers.is(DOCUMENT_TYPE))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-9
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (bdoc and pdf)
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestNotMatchingDocumentTypeAndActualFile() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "pdf", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-10
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (asice and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: TS-11_23634_TS_2_timestamps.asice
     *
     ***/
    @Test @Ignore //TODO: this needs evaluation what should actually happen. X-Road will be using asice which will not validate in this sequence.
    public void ValidationRequestNotMatchingDocumentTypeAndActualFileAsice() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TS-11_23634_TS_2_timestamps.asice"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "dDOC", "simple"))
                .then()
                .body("requestErrors[0].key", Matchers.is(DOCUMENT_TYPE))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_DOCUMENT_TYPE));

    }

    /***
     *
     * TestCaseID: ValidationRequest-11
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of case insensitivity in document type
     *
     * Expected Result: Report is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestCaseChangeDocumentType() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "BdOc", "simple"))
                .then()
                .body("documentName",equalTo("Valid_IDCard_MobID_signatures.bdoc"));
    }

    /***
     *
     * TestCaseID: ValidationRequest-12
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of filename value (filename do not match the actual file)
     *
     * Expected Result: The same filename is returned as sent in the request
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestWrongFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "TotallyRandomFilename.exe", "BdOC", "simple"))
                .then()
                .body("documentName",equalTo("TotallyRandomFilename.exe"));
    }

    /***
     *
     * TestCaseID: ValidationRequest-13
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request has invalid character in filename
     *
     * Expected Result: Correct error code is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestInvalidFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "*.exe", "BDOC", "simple"))
                .then()
                .body("requestErrors[0].key", Matchers.is(FILENAME))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_FILENAME));
    }


    /***
     *
     * TestCaseID: ValidationRequest-14
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request has invalid key on document position
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestInvalidDocumentKey() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        String response = post(validationRequestForExtended("Document", encodedString,
                FILENAME, "*.exe", DOCUMENT_TYPE, "BDOC", REPORT_TYPE, "simple")).asString();

        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64) == 1);
    }


    /***
     *
     * TestCaseID: ValidationRequest-15
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request has wrong key on report type position
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestInvalidReportTypeKey() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        String repsonse = post(validationRequestForExtended(DOCUMENT, encodedString,
                FILENAME, "Valid_IDCard_MobID_signatures.bdoc", DOCUMENT_TYPE, "BDOC", "WrongreportType", "simple")).asString();

        assertTrue(getFailMessageForKey(REPORT_TYPE), getRequestErrorsCount(repsonse, REPORT_TYPE, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(REPORT_TYPE), getRequestErrorsCount(repsonse, REPORT_TYPE, INVALID_REPORT_TYPE) == 1);

    }

    /***
     *
     * TestCaseID: ValidationRequest-16
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request has XML as document type (special case, XML is similar to ddoc)
     *
     * Expected Result: Error is given
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestXmlDocument() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "xml", "simple"))
                .then()
                .body("requestErrors[0].key", Matchers.is(DOCUMENT_TYPE))
                .body("requestErrors[0].message", Matchers.is(INVALID_DOCUMENT_TYPE));

    }

    /***
     *
     * TestCaseID: ValidationRequest-17
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request has long (38784 characters) in filename field
     *
     * Expected Result: Report is returned with the same filename
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestLongFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));

        String filename = "ngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenangFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonngFilenamengFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonngFilngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonmeToBeInsertedLonngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonFilenameToBeInsertedLonngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLoneInsertedLonngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLonngFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInsertedLongFilenameToBeInserted.bdoc";

        post(validationRequestWithValidKeys(encodedString, filename, "bdoc", "simple"))
                .then()
                .body("documentName",equalTo(filename));
    }

    /***
     *
     * TestCaseID: ValidationRequest-18
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Totally empty request body is sent
     *
     * Expected Result: Error is given
     *
     * File: None
     *
     ***/
    @Test
    public void ValidationRequestEmptyBody() {
        String response = post(new JSONObject().toString()).thenReturn().body().asString();

        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT_TYPE, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(REPORT_TYPE), getRequestErrorsCount(response, REPORT_TYPE, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY)==1);

        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT_TYPE, INVALID_DOCUMENT_TYPE)==1);
        assertTrue(getFailMessageForKey(REPORT_TYPE), getRequestErrorsCount(response, REPORT_TYPE, INVALID_REPORT_TYPE)==1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_FILENAME)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64)==1);
    }

    /***
     *
     * TestCaseID: ValidationRequest-19
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request with more parameters than expected is sent
     *
     * Expected Result: Error is given or extra parameters are ignored?
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestMoreKeysThanExpected() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "Valid_IDCard_MobID_signatures.bdoc");
        jsonObject.put(DOCUMENT_TYPE, "bdoc");
        jsonObject.put(REPORT_TYPE, "simple");
        jsonObject.put("ExtraOne", "RandomValue");
        jsonObject.put("ExtraTwo", "AnotherValue");
        post(jsonObject.toString())
                .then()
                .body("documentName",equalTo("Valid_IDCard_MobID_signatures.bdoc"));

    }

    /***
     *
     * TestCaseID: ValidationRequest-20
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request with special chars is sent
     *
     * Expected Result: Validation report is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void ValidationRequestUnusualChars() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "ÕValid_IDCard_MobID_signatures.bdocÄÖÜ", "bdoc", "simple"))
                .then()
                .body("documentName",equalTo("ÕValid_IDCard_MobID_signatures.bdocÄÖÜ"));
    }

    /***
     *
     * TestCaseID: ValidationRequest-21
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (ddoc and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void DdocValidationRequestNotMatchingDocumentTypeAndActualFileBdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "ddoc", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-22
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (ddoc and pdf)
     *
     * Expected Result: Error is returned
     *
     * File: PdfValidSingleSignature.pdf
     *
     ***/
    @Test
    public void DdocValidationRequestNotMatchingDocumentTypeAndActualFilePdf() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PdfValidSingleSignature.pdf"));
        post(validationRequestWithValidKeys(encodedString, "PdfValidSingleSignature.pdf", "ddoc", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-23
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (bdoc and pdf)
     *
     * Expected Result: Error is returned
     *
     * File: PdfValidSingleSignature.pdf
     *
     ***/
    @Test
    public void BdocValidationRequestNotMatchingDocumentTypeAndActualFilePdf() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PdfValidSingleSignature.pdf"));
        post(validationRequestWithValidKeys(encodedString, "PdfValidSingleSignature.pdf", "bdoc", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-24
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (bdoc and ddoc)
     *
     * Expected Result: Error is returned
     *
     * File: igasugust1.3.ddoc
     *
     ***/
    @Test @Ignore //TODO: VAL-196
    public void BdocValidationRequestNotMatchingDocumentTypeAndActualFileDdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("igasugust1.3.ddoc"));
        post(validationRequestWithValidKeys(encodedString, "igasugust1.3.ddoc", "bdoc", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-25
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (pdf and ddoc)
     *
     * Expected Result: Error is returned
     *
     * File: igasugust1.3.ddoc
     *
     ***/
    @Test
    public void PdfValidationRequestNotMatchingDocumentTypeAndActualFileDdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("igasugust1.3.ddoc"));
        post(validationRequestWithValidKeys(encodedString, "igasugust1.3.ddoc", "pdf", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /***
     *
     * TestCaseID: ValidationRequest-26
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (pdf and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test
    public void PdfValidationRequestNotMatchingDocumentTypeAndActualFileBdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "pdf", "simple"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}