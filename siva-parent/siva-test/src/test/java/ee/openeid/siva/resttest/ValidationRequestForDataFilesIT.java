package ee.openeid.siva.resttest;

import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.resttest.ValidationRequestIT;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.Map;

import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static ee.openeid.siva.resttest.ValidationRequestIT.getFailMessageForKey;
import static ee.openeid.siva.resttest.ValidationRequestIT.getRequestErrorsCount;

/**
 * Created by julia on 20.04.2017.
 */

@Category(IntegrationTest.class)
public class ValidationRequestForDataFilesIT extends SiVaRestTests {
    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


    /**
     * TestCaseID: Validation-Request-For-Data-Files-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface
     *
     * Title: Input empty values
     *
     * Expected Result: Errors are returned stating the missing values
     *
     * File: not relevant
     */

     @Test
    public void validationRequestForDataFilesEmptyInputs(){
        String json = postForDataFiles(dataFilesRequestInvalidValues("", "")).asString();

        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(json, DOCUMENT_TYPE, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, MAY_NOT_BE_EMPTY)==1);

        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(json, DOCUMENT_TYPE, INVALID_DOCUMENT_TYPE_DDOC)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, INVALID_BASE_64)==1);
    }

    /**
     * TestCaseID: Validation-Request-For-Data-Files-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface
     *
     * Title: Totally empty request body is sent.
     *
     * Expected Result: Errors are given.
     *
     * File: not relevant
     */
    @Test
    public void validationRequestForDataFilesEmptyBody() {
        String response = postForDataFiles(new JSONObject().toString()).thenReturn().body().asString();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+response+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT_TYPE, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT_TYPE, INVALID_DOCUMENT_TYPE_DDOC)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64)==1);
    }
    /**
     * TestCaseID: Validation-Request-For-Data-Files-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface
     *
     * Title: Additional elements are added
     *
     * Expected Result: Added elements ignored, no error messages and  additional elements in response.
     *
     * File: susisevad1_3.ddoc
     */
    @Test
    public void validationRequestForDataFilesMoreKeysThanExpected() {
        JSONObject jsonObject = new JSONObject(dataFilesRequest("susisevad1_3.ddoc"));
        jsonObject.put("ExtraOne", "RandomValue");
        jsonObject.put("ExtraTwo", "AnotherValue");
        System.out.println(jsonObject);
        postForDataFiles(jsonObject.toString())
                .then()
                .body("dataFiles[0].size", Matchers.is(35))
                .body("dataFiles[0].base64",Matchers.is("VGVzdDENClRlc3QyDQpUZfB0Mw0KS2H+bWFhciAuLi4uDQo=\n"))
                .body("dataFiles[0].ExtraOne", Matchers.is(Matchers.nullValue()))
                .body("dataFiles[0].ExtraTwo", Matchers.is(Matchers.nullValue()))
                .body("requestErrors", Matchers.is(Matchers.nullValue()));

    }

    /**
     * TestCaseID: Validation-Request-For-Data-Files-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface
     *
     * Title: Mandatory element is deleted.
     *
     * Expected Result: Errors returned stating the missing element.
     *
     * File: susisevad1_3.ddoc
     */
    @Test
    public void validationRequestForDataFilesLessKeysThanExpected() {

        JSONObject jsonObject = new JSONObject(dataFilesRequest("susisevad1_3.ddoc"));
        jsonObject.remove("document");
        String response =  postForDataFiles(jsonObject.toString()).asString();
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY)==1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64)==1);


    }


    /**
     * TestCaseID: Validation-Request-For-Data-Files-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface
     *
     * Title: Document Type is changed in request to BDOC.
     *
     * Expected Result: Error is returned.
     *
     * File: susisevad1_3.ddoc
     */
    @Test
    public void validationRequestForDataFilesDocumentTypeSetToBdoc() {

        JSONObject jsonObject = new JSONObject(dataFilesRequest("susisevad1_3.ddoc"));
        jsonObject.put("documentType", "BDOC");
        String response =  postForDataFiles(jsonObject.toString()).asString();
        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT_TYPE, INVALID_DOCUMENT_TYPE_DDOC)==1);


           }

    //document type PDF
    /**
     * TestCaseID: Validation-Request-For-Data-Files-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-request-interface
     *
     * Title: Document Type is changed in request to PDF.
     *
     * Expected Result: Error is returned.
     *
     * File: susisevad1_3.ddoc
     */
    @Test
    public void validationRequestForDataFilesDocumentTypeSetToPdf() {

        JSONObject jsonObject = new JSONObject(dataFilesRequest("susisevad1_3.ddoc"));
        jsonObject.put("documentType", "PDF");
        String response =  postForDataFiles(jsonObject.toString()).asString();
        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT_TYPE, INVALID_DOCUMENT_TYPE_DDOC)==1);

    }


        @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
