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

package ee.openeid.siva.resttest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class ValidationRequestIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

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
     * TestCaseID: ValidationRequest-Parameters-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Happy path valid mandatory input test
     *
     * Expected Result: Validation report is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestAllRequiredInputs() {
        post(validationRequestFor("Valid_IDCard_MobID_signatures.bdoc"))
                .then()
                .body("validationReport.validationConclusion.validatedDocument.filename", equalTo("Valid_IDCard_MobID_signatures.bdoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(2));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
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
                .then()
                .body("validationReport.validationConclusion.validatedDocument.filename", equalTo("xroad-simple.asice"))
                .body("validationReport.validationConclusion.policy.policyName", equalTo("POLv3"))
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Totally empty request body is sent
     *
     * Expected Result: Error is given
     *
     * File: not relevant
     */
    @Test
    public void validationRequestEmptyBody() {
        String response = post(new JSONObject().toString()).thenReturn().body().asString();

        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_FILENAME) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64) == 1);
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Input empty values
     *
     * Expected Result: Errors are returned stating the missing values
     *
     * File: not relevant
     */
    @Test
    public void validationRequestEmptyInputs() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, "");
        jsonObject.put(FILENAME, "");
        jsonObject.put(SIGNATURE_POLICY, "");
        jsonObject.put(REPORT_TYPE, "");

        String json = post(jsonObject.toString()).thenReturn().body().asString();

        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(json, FILENAME, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(json, FILENAME, INVALID_FILENAME) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, INVALID_BASE_64) == 1);
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Request with more parameters than expected is sent
     *
     * Expected Result: Error is given or extra parameters are ignored?
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestMoreKeysThanExpected() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "Valid_IDCard_MobID_signatures.bdoc");
        jsonObject.put(SIGNATURE_POLICY, VALID_SIGNATURE_POLICY_3);
        jsonObject.put("ExtraOne", "RandomValue");
        jsonObject.put("ExtraTwo", "AnotherValue");
        post(jsonObject.toString())
                .then()
                .body("validationReport.validationConclusion.validatedDocument.filename", equalTo("Valid_IDCard_MobID_signatures.bdoc"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Request has invalid keys (capital letters)
     *
     * Expected Result: Error is returned stating wrong values
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestInvalidDocumentKey() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        String response = post(validationRequestForExtended("DOCUMENT", encodedString,
                "FILENAME", "*.exe", SIGNATURE_POLICY, VALID_SIGNATURE_POLICY_3)).asString();

        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64) == 1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(response, FILENAME, INVALID_FILENAME) == 1);
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Document parameter is missing
     *
     * Expected Result: Errors are returned stating missing Document values
     *
     * File: not relevant
     */
    @Test
    public void validationRequestDocumentParameterMissing() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, "");
        jsonObject.put(FILENAME, "randomString.asice");

        String json = post(jsonObject.toString()).thenReturn().body().asString();

        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(DOCUMENT), getRequestErrorsCount(json, DOCUMENT, INVALID_BASE_64) == 1);
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Filename parameter is missing
     *
     * Expected Result: Errors are returned stating the missing values
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestFilenameParameterMissing() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "");

        String json = post(jsonObject.toString()).thenReturn().body().asString();

        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(json, FILENAME, MAY_NOT_BE_EMPTY) == 1);
        assertTrue(getFailMessageForKey(FILENAME), getRequestErrorsCount(json, FILENAME, INVALID_FILENAME) == 1);
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Happy path valid file encoding format
     *
     * Expected Result: Validation report is returned
     *
     * File: xroad-simple.asice
     */
    @Test
    public void validationRequestCorrectBase64() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", null))
                .then()
                .body("validationReport.validationConclusion.validatedDocument.filename", equalTo("Valid_IDCard_MobID_signatures.bdoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(2));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Happy path valid file encoding format
     *
     * Expected Result: Validation report is returned
     *
     * File: xroad-simple.asice
     */
    @Test
    public void validationRequestFaultyBase64() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys("a" + encodedString, "Valid_IDCard_MobID_signatures.bdoc", null))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Request with not base64 string as document
     *
     * Expected Result: Error is returned stating encoding problem
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestNonBase64Input() {
        String encodedString = ",:";
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "POLv3"))
                .then()
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_BASE_64));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Filename field should be case insensitive
     *
     * Expected Result: Validation report is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void ValidationRequestCaseInsensitiveFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bDoC", null))
                .then()
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(2));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Filename field should ignore spaces
     *
     * Expected Result: Validation report is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void ValidationRequestSpaceInFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid IDCard Mob ID_signatures .bDoC", null))
                .then()
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(2));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Request has invalid character in filename
     *
     * Expected Result: Correct error code is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestInvalidFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "*.exe", "POLv3"))
                .then()
                .body("requestErrors[0].key", Matchers.is(FILENAME))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_FILENAME));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Filename is in allowed length
     *
     * Expected Result: Report is returned with the same filename
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestMaxFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));

        String filename = StringUtils.repeat("a", 250) + ".bdoc";

        post(validationRequestWithValidKeys(encodedString, filename, "POLv3"))
                .then()
                .body("validationReport.validationConclusion.validatedDocument.filename", equalTo(filename));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Filename is over allowed length
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestTooLongFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));

        String filename = StringUtils.repeat("a", 261) + ".bdoc";

        post(validationRequestWithValidKeys(encodedString, filename, "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(FILENAME))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_FILENAME_SIZE));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: DocumentType parameter is missing
     *
     * Expected Result: Document type is selected automatically and validation report is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestDocumentTypeMissing() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "Valid_IDCard_MobID_signatures.bdoc");
        jsonObject.put(SIGNATURE_POLICY, "POLv3");

        post(jsonObject.toString())
                .then()
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(2));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-18
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
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
                .then()
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-19
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: DocumentType parameter is XROAD
     *
     * Expected Result: Xroad validatior is used for validation and report is returned
     *
     * File: xroad-simple.asice
     */
    @Test
    public void validationRequestDocumentTypeInvalid() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "xroad-simple.asice");
        jsonObject.put(DOCUMENT_TYPE, "NotXroad");

        post(jsonObject.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT_TYPE))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-20
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy parameter is missing, default is used
     *
     * Expected Result: Validation report is returned with POLv4
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestDefaultPolicy() {
        post(validationRequestFor("Valid_IDCard_MobID_signatures.bdoc"))
                .then()
                .body("validationReport.validationConclusion.policy.policyName", equalTo("POLv4"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-21
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy is set as POLv3
     *
     * Expected Result: Validation report is returned with POLv3
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestPOLv3() {
        post(validationRequestFor("Valid_IDCard_MobID_signatures.bdoc", "POLv3", null))
                .then()
                .body("validationReport.validationConclusion.policy.policyName", equalTo("POLv3"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-22
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy is set as POLv4
     *
     * Expected Result: Validation report is returned with POLv4
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestPOLv4() {
        post(validationRequestFor("Valid_IDCard_MobID_signatures.bdoc", "POLv4", null))
                .then()
                .body("validationReport.validationConclusion.policy.policyName", equalTo("POLv4"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-23
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy has not allowed value
     *
     * Expected Result: error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestInvalidPolicy() {
        post(validationRequestFor("Valid_IDCard_MobID_signatures.bdoc", INVALID_SIGNATURE_POLICY, null))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(SIGNATURE_POLICY))
                .body("requestErrors[0].message", Matchers.is("Invalid signature policy: " + INVALID_SIGNATURE_POLICY + "; Available abstractPolicies: [" + VALID_SIGNATURE_POLICY_3 + ", " + VALID_SIGNATURE_POLICY_4 + "]"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-24
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter is missing
     *
     * Expected Result: Simple report is returned as default
     *
     * File: TS-11_23634_TS_2_timestamps.asice
     */
    @Test
    public void validationRequestDefaultReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TS-11_23634_TS_2_timestamps.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "TS-11_23634_TS_2_timestamps.asice");

        post(jsonObject.toString())
                .then()
                .body("validationReport.validationProcess", isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-25
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter is missing
     *
     * Expected Result: Simple report is returned as default
     *
     * File: TS-11_23634_TS_2_timestamps.asice
     */
    @Test
    public void validationRequestSimpleReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TS-11_23634_TS_2_timestamps.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "TS-11_23634_TS_2_timestamps.asice");
        jsonObject.put(REPORT_TYPE, "Simple");

        post(jsonObject.toString())
                .then()
                .body("validationReport.validationProcess", isEmptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-26
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter is missing
     *
     * Expected Result: Simple report is returned as default
     *
     * File: TS-11_23634_TS_2_timestamps.asice
     */
    @Test
    public void validationRequestDetailedReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TS-11_23634_TS_2_timestamps.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "TS-11_23634_TS_2_timestamps.asice");
        jsonObject.put(REPORT_TYPE, "Detailed");

        post(jsonObject.toString())
                .then()
                .body("validationReport.validationProcess.qmatrixBlock.signatureAnalysis[0].signatureQualification", equalTo("QESIG"))
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-27
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter is invalid
     *
     * Expected Result: Error is returned
     *
     * File: xroad-simple.asice
     */
    @Test
    public void validationRequestReportTypeInvalid() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TS-11_23634_TS_2_timestamps.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "TS-11_23634_TS_2_timestamps.asice");
        jsonObject.put(REPORT_TYPE, "NotValid");
        post(jsonObject.toString())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(REPORT_TYPE))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_REPORT_TYPE));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-28
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Filename is under allowed length
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestTooShortFilename() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));

        String filename = "";

        post(validationRequestWithValidKeys(encodedString, filename, "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

                .body("requestErrors[0].key", Matchers.is(FILENAME))
                .body("requestErrors[0].message", Matchers.isOneOf(INVALID_FILENAME, INVALID_FILENAME_SIZE, MAY_NOT_BE_EMPTY))
                .body("requestErrors[1].key", Matchers.is(FILENAME))
                .body("requestErrors[1].message", Matchers.isOneOf(INVALID_FILENAME, INVALID_FILENAME_SIZE, MAY_NOT_BE_EMPTY))
                .body("requestErrors[2].key", Matchers.is(FILENAME))
                .body("requestErrors[2].message", Matchers.isOneOf(INVALID_FILENAME, INVALID_FILENAME_SIZE, MAY_NOT_BE_EMPTY));


    }

    /**
     * TestCaseID: ValidationRequest-Parameters-29
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy is under allowed length
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestTooShortSignaturePolicy() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        String signaturePolicy = "";

        post(validationRequestWithValidKeys(encodedString, "filename.bdoc", signaturePolicy))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(SIGNATURE_POLICY))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_POLICY_SIZE));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-30
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestTooLongSignaturePolicy() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        String signaturePolicy = StringUtils.repeat("a", 101);

        post(validationRequestWithValidKeys(encodedString, "filename.bdoc", signaturePolicy))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(SIGNATURE_POLICY))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_POLICY_SIZE));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Mismatch in stated and actual document (bdoc and pdf)
     *
     * Expected Result: Error is returned
     *
     * File: PdfValidSingleSignature.pdf
     */
    @Test
    public void bdocValidationRequestNotMatchingDocumentTypeAndActualFilePdf() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("PdfValidSingleSignature.pdf"));
        post(validationRequestWithValidKeys(encodedString, "PdfValidSingleSignature.bdoc", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Input random base64 string as bdoc
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: not relevant
     */
    @Test
    public void validationRequestRandomInputAsBdocDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.bdoc", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Acceptance of ASICE as BDOC document type
     *
     * Expected Result: Asice files are handled the same as bdoc
     *
     * File: bdoc21-TS.asice
     */
    @Test
    public void validationRequestDocumentTypeBdocAndFileAsice() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("bdoc21-TS.asice"));
        post(validationRequestWithValidKeys(encodedString, "bdoc21-TS.bdoc", "POLv3"))
                .then()
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Input random base64 string as pdf
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validationRequestRandomInputAsPdfDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "some_pdf.pdf", "POLv3"))
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("message", Matchers.containsString("Document format not recognized/handled"));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Mismatch in stated and actual document (ddoc and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void ddocValidationRequestNotMatchingDocumentTypeAndActualFileBdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Valid_IDCard_MobID_signatures.ddoc", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
     *
     * Title: Input random base64 string as ddoc
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: not relevant
     */
    @Test
    public void validationRequestRandomInputAsDdocDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "some_pdf.ddoc", "POlv3"))
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
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
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

    /**
     * TestCaseID: ValidationRequest-Validator-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface
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

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}





