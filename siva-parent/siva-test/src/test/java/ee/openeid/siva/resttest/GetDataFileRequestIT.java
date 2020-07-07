/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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
package ee.openeid.siva.resttest;

import static ee.openeid.siva.resttest.ValidationRequestIT.getFailMessageForKey;
import static ee.openeid.siva.resttest.ValidationRequestIT.getRequestErrorsCount;
import static org.junit.Assert.assertTrue;

import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(IntegrationTest.class)
public class GetDataFileRequestIT extends SiVaRestTests {
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
     * TestCaseID: Get-Data-Files-Request-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Input empty values
     *
     * Expected Result: Errors are returned stating the missing values
     *
     * File: not relevant
     **/
    @Test
    public void testGetDataFileRequestEmptyInputs() {
        String json = postForDataFiles(dataFilesRequestInvalidValues("", "")).asString();
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(json, FILENAME, INVALID_DATA_FILE_FILENAME) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, INVALID_BASE_64) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Totally empty request body is sent
     *
     * Expected Result: Errors are given
     *
     * File: not relevant
     **/
    @Test
    public void testGetDataFileRequestEmptyInputsEmptyBody() {
        String response = postForDataFiles(new JSONObject().toString()).thenReturn().body().asString();
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_DATA_FILE_FILENAME) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Order of elements is changed in request
     *
     * Expected Result: Order of  elements is ignored, no error messages in response
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestChangedOrderOfKeys() {
        String invalidRequest = "{\"document\":\"" + Base64.encodeBase64String(readFileFromTestResources("test_file.ddoc")) + "\",\"filename\":\"test_file.ddoc\"}";
        postForDataFiles(invalidRequest)
                .then()
                .body("dataFiles[0].filename", Matchers.is("test.txt"))
                .body("dataFiles[0].mimeType", Matchers.is("application/octet-stream"))
                .body("dataFiles[0].base64", Matchers.is("VGVzdCBhbmQgc29tZSBvdGhlciB0ZXN0"))
                .body("dataFiles[0].size", Matchers.is(24));
    }

    /**
     * TestCaseID: Get-Data-Files-Request-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Additional elements are added
     *
     * Expected Result: Added elements ignored, no error messages and  additional elements in response
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestMoreKeysThanExpected() {
        JSONObject jsonObject = new JSONObject(dataFilesRequest("test_file.ddoc"));
        jsonObject.put("ExtraOne", "RandomValue");
        jsonObject.put("ExtraTwo", "AnotherValue");
        postForDataFiles(jsonObject.toString())
                .then()
                .body("dataFiles[0].size", Matchers.is(24))
                .body("dataFiles[0].base64", Matchers.is("VGVzdCBhbmQgc29tZSBvdGhlciB0ZXN0"));
    }

    /**
     * TestCaseID: Get-Data-Files-Request-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Mandatory element 'documentType' is duplicated, duplicated element with bdoc value
     *
     * Expected Result: Error message is returned
     *
     * File: test_fail.ddoc
     **/
    @Test
    public void testGetDataFileRequestDuplicatedKey() {
        String invalidRequest = "{\"documentType\":\"DDOC\",\"documentType\":\"BDOC\",\"document\":\"" + Base64.encodeBase64String(readFileFromTestResources("test_file.ddoc")) + "\"}";
        String response = postForDataFiles(invalidRequest).asString();
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_DATA_FILE_FILENAME) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Mandatory element 'document' is deleted
     *
     * Expected Result: Errors returned stating the missing element
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestDocumentElementRemoved() {
        JSONObject jsonObject = new JSONObject(dataFilesRequest("test_file.ddoc"));
        jsonObject.remove("document");
        String response = postForDataFiles(jsonObject.toString()).asString();
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Low case for document type is used
     *
     * Expected Result: Data file is returned, no error messages
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestDocumentTypeLowCase() {
        JSONObject jsonObject = new JSONObject(dataFilesRequest("test_file.ddoc"));
        jsonObject.put("documentType", "ddoc");
        postForDataFiles(jsonObject.toString())
                .then()
                .body("dataFiles[0].filename", Matchers.is("test.txt"))
                .body("dataFiles[0].mimeType", Matchers.is("application/octet-stream"))
                .body("dataFiles[0].base64", Matchers.is("VGVzdCBhbmQgc29tZSBvdGhlciB0ZXN0"))
                .body("dataFiles[0].size", Matchers.is(24));
    }

    /**
     * TestCaseID: Get-Data-Files-Request-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Document Type is changed in request to BDOC, actual is DDOC
     *
     * Expected Result: Error is returned
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestDocumentTypeSetToBdoc() {
        String response = postForDataFiles(dataFilesRequestExtended("test_file.ddoc", "test.BDOC")).asString();
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_DATA_FILE_FILENAME) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Document Type is changed in request to PDF, actual is DDOC
     *
     * Expected Result: Error is returned
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestDocumentTypeSetToPdf() {
        String response = postForDataFiles(dataFilesRequestExtended("test_file.ddoc", "test.PDF")).asString();
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_DATA_FILE_FILENAME) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Document Type is changed in request to unsupported format (JPG), actual is DDOC
     *
     * Expected Result: Error is returned
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestDocumentTypeSetToUnsupportedFormat() {
        String response = postForDataFiles(dataFilesRequestExtended("test_file.ddoc", "test.JPG")).asString();
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_DATA_FILE_FILENAME) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Document Type is changed in request to XROAD format, actual is DDOC
     *
     * Expected Result: Error is returned
     *
     * File: test_file.ddoc
     **/
    @Test
    public void testGetDataFileRequestDocumentTypeSetToXroad() {
        String response = postForDataFiles(dataFilesRequestExtended("test_file.ddoc", "test.XROAD")).asString();
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_DATA_FILE_FILENAME) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Document Type is changed in request to DDOC format, actual is BDOC
     *
     * Expected Result: Error is returned
     *
     * File: BDOC-TS.bdoc
     **/
    @Test
    public void testGettDataFileRequestBdocDocumentTypeSetToDdoc() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String response = postForDataFiles(dataFilesRequestExtended("BDOC-TS.bdoc", "test.DDOC")).asString();
        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT, DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Document Type is changed in request to DDOC format, actual is PDF
     *
     * Expected Result: Error is returned
     *
     * File: PdfValidSingleSignature.pdf
     **/
    @Test
    public void testGetDataFileRequestPdfDocumentTypeSetToDdoc() {
        setTestFilesDirectory("document_format_test_files/");
        String response = postForDataFiles(dataFilesRequestExtended("PdfValidSingleSignature.pdf", "test.DDOC")).asString();
        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT, DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE) == 1);
    }

    /**
     * TestCaseID: Get-Data-Files-Request-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: For Unsupported format Document Type is changed in request to DDOC format, actual is PNG
     *
     * Expected Result: Error is returned
     *
     * File: Picture.png
     **/
    @Test
    public void testGetDataFileRequestUnsupportedDocumentTypeSetToDdoc() {
        setTestFilesDirectory("document_format_test_files/");
        String response = postForDataFiles(dataFilesRequestExtended("Picture.png", "test.DDOC")).asString();
        assertTrue(getFailMessageForKey(DOCUMENT_TYPE), getRequestErrorsCount(response, DOCUMENT, DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE) == 1);
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
