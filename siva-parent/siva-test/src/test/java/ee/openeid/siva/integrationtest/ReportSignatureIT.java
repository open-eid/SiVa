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

package ee.openeid.siva.integrationtest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;

import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(IntegrationTest.class)
public class ReportSignatureIT extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";
    private static final String VALIDATION_ENDPOINT = "/validate";
    @Autowired
    private SignatureServiceConfigurationProperties signatureServiceConfigurationProperties;

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }

    /**
     * TestCaseID: Detailed-Report-Signature-1
     *
     * TestType: Auto
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Simple report signature should not be in response
     *
     * Expected Result: Simple report response should not contain signature
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void whenRequestingSimpleReport_thenValidationReportSignatureShouldNotBeInResponse() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", null, "Simple"))
                .then().log().all()
                .body("validationReportSignature", isEmptyOrNullString());
    }

    /**
     * TestCaseID: Detailed-Report-Signature-2
     *
     * TestType: Auto
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Simple report signature should be in response
     *
     * Expected Result: Simple report response should contain signature
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void whenRequestingDetailedReport_thenValidationReportSignatureShouldBeInResponse() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", null, "Detailed"))
                .then().log().all()
                .body("validationReportSignature", not(isEmptyOrNullString()));
    }

    /**
     * TestCaseID: Detailed-Report-Signature-3
     *
     * TestType: Auto
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature
     *
     * Expected Result: Signed report is successfully validated
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Test
    public void validateDetailedReportSignature() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LT");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-pades-lt-sha256-sign.pdf";
        String request = validationRequestFor(filename, VALID_SIGNATURE_POLICY_4, "Detailed");
        Response response = validateRequestForDetailedReport(request, VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", validationReportSignature);
        jsonObject.put("filename", "filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .body(jsonObject.toString())
                .when()
                .post(VALIDATION_ENDPOINT)
                .then()
                .log()
                .all()
                .extract()
                .response();
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signaturesCount"), equalTo("1"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.validSignaturesCount"), equalTo("1"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat[0]"), equalTo("XAdES_BASELINE_LT"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat[0]"), equalTo("XAdES_BASELINE_LT"));
    }

    /**
     * TestCaseID: Detailed-Report-Signature-4
     *
     * TestType: Auto
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: In simple report reportSignatureEnabled parameter value true
     *
     * Expected Result: File hash in hex in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Test
    public void whenRequestingSimpleReport_andreportSignatureEnabledTrue_fileHashInHex_InReport() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", null, "Simple"))
                .then().log().all()
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", not(isEmptyOrNullString()));
    }

    /**
     * TestCaseID: Detailed-Report-Signature-5
     *
     * TestType: Auto
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: In simple report reportSignatureEnabled parameter value false
     *
     * Expected Result: File hash in hex not in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore //This test should be ran manually after configuring the report signature feature
    @Test
    public void whenRequestingSimpleReport_andreportSignatureEnabledFalse_fileHashInHex_NotInReport() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", null, "Simple"))
                .then().log().all()
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", isEmptyOrNullString());
    }

    private Response validateRequestForDetailedReport(String request, String validationUrl) {
        return given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8")))
                .body(request)
                .when()
                .post(validationUrl)
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

} 
