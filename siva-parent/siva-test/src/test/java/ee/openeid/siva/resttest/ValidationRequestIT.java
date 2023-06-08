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

import ee.openeid.siva.integrationtest.SiVaRestTests;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static ee.openeid.siva.integrationtest.TestData.REPORT_TYPE_DETAILED;
import static ee.openeid.siva.integrationtest.TestData.REPORT_TYPE_DIAGNOSTIC;
import static ee.openeid.siva.integrationtest.TestData.REPORT_TYPE_SIMPLE;
import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("IntegrationTest")
class ValidationRequestIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/test/timestamp/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    static int getRequestErrorsCount(String json, String field, String message) {
        List<Map> errors = from(json).get("requestErrors.findAll { requestError -> requestError.key == '" + field + "' && requestError.message=='" + message + "' }");
        return errors.size();
    }

    public static String getFailMessageForKey(String key) {
        return key + " error or corresponding message was not in the response";
    }

    @BeforeEach
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Happy path valid mandatory inputs
     *
     * Expected Result: Validation report is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestAllRequiredInputs() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("singleValidSignatureTM.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validatedDocument.filename", equalTo("singleValidSignatureTM.bdoc"))
                .body("validSignaturesCount", equalTo(1));
    }



    /**
     * TestCaseID: ValidationRequest-Parameters-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Totally empty request body is sent
     *
     * Expected Result: Error is given
     *
     * File: not relevant
     */
    @Test
    void validationRequestEmptyBody() {
        String response = post(new JSONObject().toString()).thenReturn().body().asString();

        assertEquals(1, getRequestErrorsCount(response, FILENAME, MUST_NOT_BE_EMPTY), getFailMessageForKey(FILENAME));
        assertEquals(1, getRequestErrorsCount(response, DOCUMENT, MUST_NOT_BE_BLANK), getFailMessageForKey(DOCUMENT));
        assertEquals(1, getRequestErrorsCount(response, FILENAME, INVALID_FILENAME), getFailMessageForKey(FILENAME));
        assertEquals(1, getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64), getFailMessageForKey(DOCUMENT));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Input empty values
     *
     * Expected Result: Errors are returned stating the missing values
     *
     * File: not relevant
     */
    @Test
    void validationRequestEmptyInputs() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, "");
        jsonObject.put(FILENAME, "");
        jsonObject.put(SIGNATURE_POLICY, "");
        jsonObject.put(REPORT_TYPE, "");

        String json = post(jsonObject.toString()).thenReturn().body().asString();

        assertEquals(1, getRequestErrorsCount(json, FILENAME, MUST_NOT_BE_EMPTY), getFailMessageForKey(FILENAME));
        assertEquals(1, getRequestErrorsCount(json, DOCUMENT, MUST_NOT_BE_BLANK), getFailMessageForKey(DOCUMENT));
        assertEquals(1, getRequestErrorsCount(json, DOCUMENT, INVALID_BASE_64), getFailMessageForKey(DOCUMENT));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Request with more parameters than expected is sent
     *
     * Expected Result: Error is given or extra parameters are ignored?
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestMoreKeysThanExpected() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "singleValidSignatureTM.bdoc");
        jsonObject.put(SIGNATURE_POLICY, VALID_SIGNATURE_POLICY_3);
        jsonObject.put("ExtraOne", "RandomValue");
        jsonObject.put("ExtraTwo", "AnotherValue");
        post(jsonObject.toString())
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validatedDocument.filename", equalTo("singleValidSignatureTM.bdoc"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Request has invalid keys (capital letters)
     *
     * Expected Result: Error is returned stating wrong values
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestInvalidDocumentKey() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        String response = post(validationRequestForExtended("DOCUMENT", encodedString,
                "FILENAME", "*.exe", SIGNATURE_POLICY, VALID_SIGNATURE_POLICY_3)).asString();

        assertEquals(1, getRequestErrorsCount(response, DOCUMENT, MUST_NOT_BE_BLANK), getFailMessageForKey(DOCUMENT));
        assertEquals(1, getRequestErrorsCount(response, DOCUMENT, INVALID_BASE_64), getFailMessageForKey(DOCUMENT));
        assertEquals(1, getRequestErrorsCount(response, FILENAME, MUST_NOT_BE_EMPTY), getFailMessageForKey(FILENAME));
        assertEquals(1, getRequestErrorsCount(response, FILENAME, INVALID_FILENAME), getFailMessageForKey(FILENAME));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Document parameter is missing
     *
     * Expected Result: Errors are returned stating missing Document values
     *
     * File: not relevant
     */
    @Test
    void validationRequestDocumentParameterMissing() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, "");
        jsonObject.put(FILENAME, "randomString.asice");

        String json = post(jsonObject.toString()).thenReturn().body().asString();

        assertEquals(1, getRequestErrorsCount(json, DOCUMENT, MUST_NOT_BE_BLANK), getFailMessageForKey(DOCUMENT));
        assertEquals(1, getRequestErrorsCount(json, DOCUMENT, INVALID_BASE_64), getFailMessageForKey(DOCUMENT));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Filename parameter is missing
     *
     * Expected Result: Errors are returned stating the missing values
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestFilenameParameterMissing() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "");

        String json = post(jsonObject.toString()).thenReturn().body().asString();

        assertEquals(1, getRequestErrorsCount(json, FILENAME, MUST_NOT_BE_EMPTY), getFailMessageForKey(FILENAME));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Happy path valid file encoding format
     *
     * Expected Result: Validation report is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestCorrectBase64() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "singleValidSignatureTM.bdoc", null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validatedDocument.filename", equalTo("singleValidSignatureTM.bdoc"))
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Happy path valid file encoding format
     *
     * Expected Result: Validation report is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestFaultyBase64() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        post(validationRequestWithValidKeys("a" + encodedString, "singleValidSignatureTM.bdoc", null))
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Request with not base64 string as document
     *
     * Expected Result: Error is returned stating encoding problem
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    void validationRequestNonBase64Input() {
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Filename field should be case insensitive
     *
     * Expected Result: Validation report is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void ValidationRequestCaseInsensitiveFilename() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "singleValidSignatureTM.bDoC", null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Filename field should ignore spaces
     *
     * Expected Result: Validation report is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void ValidationRequestSpaceInFilename() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "singleValidSignatureTM .bDoC", null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Filename is in allowed length
     *
     * Expected Result: Report is returned with the same filename
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestMaxFilename() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));

        String filename = StringUtils.repeat("a", 250) + ".bdoc";

        post(validationRequestWithValidKeys(encodedString, filename, "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validatedDocument.filename", equalTo(filename));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Filename is over allowed length
     *
     * Expected Result: Error is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestTooLongFilename() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));

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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: DocumentType parameter is missing
     *
     * Expected Result: Document type is selected automatically and validation report is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestDocumentTypeMissing() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "singleValidSignatureTM.bdoc");
        jsonObject.put(SIGNATURE_POLICY, "POLv3");

        post(jsonObject.toString())
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-20
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy parameter is missing, default is used
     *
     * Expected Result: Validation report is returned with POLv4
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestDefaultPolicy() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("singleValidSignatureTM.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("policy.policyName", equalTo("POLv4"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-21
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy is set as POLv3
     *
     * Expected Result: Validation report is returned with POLv3
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestPOLv3() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("singleValidSignatureTM.bdoc", "POLv3", null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("policy.policyName", equalTo("POLv3"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-22
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy is set as POLv4
     *
     * Expected Result: Validation report is returned with POLv4
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestPOLv4() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("singleValidSignatureTM.bdoc", "POLv4", null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("policy.policyName", equalTo("POLv4"));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-23
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy has not allowed value
     *
     * Expected Result: error is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestInvalidPolicy() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("singleValidSignatureTM.bdoc", INVALID_SIGNATURE_POLICY, null))
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter is missing
     *
     * Expected Result: Simple report is returned as default
     *
     * File: singleValidSignatureTS.asice
     */
    @Test
    void validationRequestDefaultReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTS.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "singleValidSignatureTS.asice");

        post(jsonObject.toString())
                .then()
                .body("validationReport.validationProcess", emptyOrNullString())
                .body("validationReport.validationConclusion.validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-25
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter Simple
     *
     * Expected Result: Simple report is returned
     *
     * File: singleValidSignatureTS.asice
     */
    @Test
    void validationRequestSimpleReport() {
        post(validationRequestFor("singleValidSignatureTS.asice", null, REPORT_TYPE_SIMPLE ))
                .then()
                .body("validationReport.validationProcess", emptyOrNullString())
                .body("validationReport.diagnosticData", emptyOrNullString())
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-26
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter Detailed
     *
     * Expected Result: Detailed report is returned
     *
     * File: singleValidSignatureTS.asice
     */
    @Test
    void validationRequestDetailedReport() {
        post(validationRequestFor("singleValidSignatureTS.asice", null, REPORT_TYPE_DETAILED ))
                .then()
                .body("validationReport.diagnosticData", emptyOrNullString())
                .body("validationReport.validationProcess.signatureOrTimestampOrCertificate[0].validationSignatureQualification.signatureQualification", equalTo("QESIG"))
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-27
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter is invalid
     *
     * Expected Result: Error is returned
     *
     * File: singleValidSignatureTS.asice
     */
    @Test
    void validationRequestReportTypeInvalid() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTS.asice"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DOCUMENT, encodedString);
        jsonObject.put(FILENAME, "singleValidSignatureTS.asice");
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Filename is under allowed length
     *
     * Expected Result: Error is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestTooShortFilename() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));

        String filename = "";

        post(validationRequestWithValidKeys(encodedString, filename, "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

                .body("requestErrors[0].key", Matchers.is(FILENAME))
                .body("requestErrors[0].message", Matchers.oneOf(INVALID_FILENAME_SIZE, MUST_NOT_BE_EMPTY))
                .body("requestErrors[1].key", Matchers.is(FILENAME))
                .body("requestErrors[1].message", Matchers.oneOf(INVALID_FILENAME_SIZE, MUST_NOT_BE_EMPTY));


    }

    /**
     * TestCaseID: ValidationRequest-Parameters-29
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy is under allowed length
     *
     * Expected Result: Error is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestTooShortSignaturePolicy() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: SignaturePolicy is too long
     *
     * Expected Result: Error is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void validationRequestTooLongSignaturePolicy() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        String signaturePolicy = StringUtils.repeat("a", 101);

        post(validationRequestWithValidKeys(encodedString, "filename.bdoc", signaturePolicy))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(SIGNATURE_POLICY))
                .body("requestErrors[0].message", Matchers.containsString(INVALID_POLICY_SIZE));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-31
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: ReportType parameter Diagnostic
     *
     * Expected Result: Diagnostic report is returned
     *
     * File: singleValidSignatureTS.asice
     */
    @Test
    void validationRequestDiagnosticReport() {
        post(validationRequestFor("singleValidSignatureTS.asice", null, REPORT_TYPE_DIAGNOSTIC ))
                .then()
                .body("validationReport.validationProcess", emptyOrNullString())
                .body("validationReport.diagnosticData.documentName", equalTo("singleValidSignatureTS.asice"))
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: ValidationRequest-Parameters-32
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: DocumentType parameter is not allowed
     *
     * Expected Result: Error is returned
     *
     * File: xroad-simple.asice
     */

    @Test
    void validationRequestWithDocumentType() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));

        post(validationRequestWithDocumentTypeValidKeys(encodedString, "xroad-simple.asice", "xroad", VALID_SIGNATURE_POLICY_3))
                .then().statusCode(400)
                .body("requestErrors[0].key", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.is(DOCUMENT_TYPE_NOT_ACCEPTED));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Mismatch in stated and actual document (bdoc and pdf)
     *
     * Expected Result: Error is returned
     *
     * File: PdfValidSingleSignature.pdf
     */
    @Test
    void bdocValidationRequestNotMatchingDocumentTypeAndActualFilePdf() {
        setTestFilesDirectory("document_format_test_files/");
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Input random base64 string as bdoc
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: not relevant
     */
    @Test
    void validationRequestRandomInputAsBdocDocument() {
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Acceptance of ASICE as BDOC document type
     *
     * Expected Result: Asice files are handled the same as bdoc
     *
     * File: singleValidSignatureTS.asice
     */
    @Test
    void validationRequestDocumentTypeBdocAndFileAsice() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTS.asice"));
        post(validationRequestWithValidKeys(encodedString, "singleValidSignatureTS.bdoc", "POLv3"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Input random base64 string as pdf
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    void validationRequestRandomInputAsPdfDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "some_pdf.pdf", "POLv3"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].message", Matchers.containsString("Document malformed or not matching documentType"));
    }

    /**
     * TestCaseID: ValidationRequest-Validator-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Mismatch in stated and actual document (ddoc and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: singleValidSignatureTM.bdoc
     */
    @Test
    void ddocValidationRequestNotMatchingDocumentTypeAndActualFileBdoc() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("singleValidSignatureTM.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "singleValidSignatureTM.ddoc", "POLv3"))
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Input random base64 string as ddoc
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: not relevant
     */
    @Test
    void validationRequestRandomInputAsDdocDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestWithValidKeys(encodedString, "some_pdf.ddoc", "POlv3"))
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





