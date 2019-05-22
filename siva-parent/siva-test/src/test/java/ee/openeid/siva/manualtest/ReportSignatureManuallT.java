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

package ee.openeid.siva.manualtest;

import ee.openeid.siva.SivaWebApplication;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import ee.openeid.siva.soaptest.SiVaSoapTests;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SivaWebApplication.class, webEnvironment=RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:application-test.yml")

public class ReportSignatureManuallT  extends SiVaSoapTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signature_cryptographic_algorithm_test_files/";
    private static final String VALIDATION_ENDPOINT = "/validate";
    protected static final String VALID_SIGNATURE_POLICY_4 = "POLv4";
    private static final String PROJECT_SUBMODULE_NAME =  "siva-test";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    private Response response;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @Autowired
    private SignatureServiceConfigurationProperties signatureServiceConfigurationProperties;

    @Value("${local.server.port}")
    protected int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    /**
     * TestCaseID: Detailed-Report-Signatures-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LTA signed with RSA key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportRsaSignatureXadesBaselineLTA() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LTA");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
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
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureLevel"), equalTo("QESIG"));
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LT signed with RSA key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportRsaSignatureXadesBaselineLT() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LT");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
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
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat"), equalTo("XAdES_BASELINE_LT"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureLevel"), equalTo("QESIG"));
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_T signed with RSA key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportRsaSignatureXadesBaselineT() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_T");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .body(jsonObject.toString())
                .when()
                .post(VALIDATION_ENDPOINT)
                .then()
                .log()
                .all()
                .extract()
                .response();
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signaturesCount"), equalTo("1"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat[0]"), equalTo("XAdES_BASELINE_T"));

    }

    /**
     * TestCaseID: Detailed-Report-Signatures-5
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_B signed with RSA key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportRsaSignatureXadesBaselineB() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_B");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .body(jsonObject.toString())
                .when()
                .post(VALIDATION_ENDPOINT)
                .then()
                .log()
                .all()
                .extract()
                .response();
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signaturesCount"), equalTo("1"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat[0]"), equalTo("XAdES_BASELINE_B"));
    }


    /**
     * TestCaseID: Detailed-Report-Signatures-6
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LTA and signed with ECC key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportEccSignatureXadesBaselineLTA() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LTA");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/ecc.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
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
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat"), equalTo("XAdES_BASELINE_LTA"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureLevel"), equalTo("QESIG"));
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-7
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_LT and signed with ECC key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportEccSignatureXadesBaselineLT() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LT");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
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
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat"), equalTo("XAdES_BASELINE_LT"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureLevel"), equalTo("QESIG"));
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-8
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_T and signed with ECC key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportEccSignatureXadesBaselineT() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_T");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .body(jsonObject.toString())
                .when()
                .post(VALIDATION_ENDPOINT)
                .then()
                .log()
                .all()
                .extract()
                .response();
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signaturesCount"), equalTo("1"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat[0]"), equalTo("XAdES_BASELINE_T"));
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-9
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when SignatureLevel XAdES_BASELINE_B and signed with ECC key.
     *
     * Expected Result: validationReportSignature exists in report
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportEccSignatureXadesBaselineB() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_B");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .body(jsonObject.toString())
                .when()
                .post(VALIDATION_ENDPOINT)
                .then()
                .log()
                .all()
                .extract()
                .response();
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signaturesCount"), equalTo("1"));
        assertThat(reportSignatureValidation.jsonPath().getString("validationReport.validationConclusion.signatures.signatureFormat[0]"), equalTo("XAdES_BASELINE_B"));
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-10
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when configuration parameter OcspUrl empty
     *
     * Expected Result: No signature
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */

    @Test
    public void validateDetailedReportSignatureOcspUrlValueEmpty() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LT");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl(" ");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-11
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when configuration parameter TspUrl empty
     *
     * Expected Result: No signature
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Test
    public void validateDetailedReportSignatureTspUrlValueEmpty() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LT");
        signatureServiceConfigurationProperties.setTspUrl(" ");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-12
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when configuration parameter Pkcs11 wrong value
     *
     * Expected Result: Error message
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateDetailedReportSignaturePkcs11WrongCert() {
        signatureServiceConfigurationProperties.setSignatureLevel("XAdES_BASELINE_LT");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs11().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs11().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        String validationReportSignature = response.jsonPath().getString("validationReportSignature");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document",validationReportSignature);
        jsonObject.put("filename","filename.pdf");
        Response reportSignatureValidation = given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
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
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-13
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when configuration parameter SignatureLevel empty
     *
     * Expected Result: No Signature
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */

    @Test
    public void validateDetailedReportSignatureLevelEmptyValue() {
        signatureServiceConfigurationProperties.setSignatureLevel(" ");
        signatureServiceConfigurationProperties.setTspUrl("http://demo.sk.ee/tsa");
        signatureServiceConfigurationProperties.setOcspUrl("http://demo.sk.ee/ocsp");
        signatureServiceConfigurationProperties.getPkcs12().setPath("src/test/resources/test.p12");
        signatureServiceConfigurationProperties.getPkcs12().setPassword("password");
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
    }

    /**
     * TestCaseID: Detailed-Report-Signatures-14
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report signature when using pks11 to sign
     *
     * Expected Result: Signature exists
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    /*Testitud ja töötab RSA sertifikaatidega id-kaardiga. Ei tööta ECC kaardiga.
    Installida OpenSC
    slotIndex: 1 /Pin1 ja slotIndex: 2/Pin2*/
    @Ignore
    @Test
    public void validateDetailedReportSignatureLevelPkcs11() {
        signatureServiceConfigurationProperties.getPkcs11().setPath("C:/Windows/System32/opensc-pkcs11.dll");
        signatureServiceConfigurationProperties.getPkcs11().setPassword("PIN2");
        signatureServiceConfigurationProperties.getPkcs11().setSlotIndex(2);
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
    }

    private Response validateRequestForDetailedReport(String request, String validationUrl){
        return given()
                .contentType(ContentType.JSON)
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .body(request)
                .when()
                .post(validationUrl)
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    private String detailedReportRequest(String fileName, String signaturePolicy) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(fileName)));
        jsonObject.put("filename", fileName);
        jsonObject.put("signaturePolicy", signaturePolicy);
        jsonObject.put("reportType", "Detailed");
        return  jsonObject.toString();
    }

    protected byte[] readFileFromTestResources(String filename) {
        String testFilesBase = getProjectBaseDirectory() + "src/test/resources/";
        return readFileFromPath(testFilesBase + getTestFilesDirectory() + filename);
    }

    protected static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        int pathLength = path.lastIndexOf(PROJECT_SUBMODULE_NAME);
        pathLength = pathLength == -1 ? path.length() : pathLength;
        path = path.substring(0 , pathLength);
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
    }

    protected static String createXMLValidationRequestWithReportType(String base64Document, String filename, String reportType) {

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "            <ReportType>" + reportType + "</ReportType>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    protected String validationRequestForDocumentReportType(String filename, String reportType) {
        return createXMLValidationRequestWithReportType(
                Base64.encodeBase64String(readFileFromTestResources(filename)),
                filename, reportType);
    }




    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
