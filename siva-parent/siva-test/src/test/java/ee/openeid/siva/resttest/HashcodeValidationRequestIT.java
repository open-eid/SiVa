/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.common.DateTimeMatcher;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.TestData;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.JSONHashcodeValidationRequest;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;

@Category(IntegrationTest.class)
public class HashcodeValidationRequestIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";
    private static final String VALIDATION_CONCLUSION_PREFIX = "validationReport.validationConclusion.";
    private static final String SIGNATURE_FILENAME_SUFFIX = ".xml";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;
    private ZonedDateTime testStartDate;

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static String getFailMessageForKey(String key) {
        return key + " error or corresponding message was not in the response";
    }

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        testStartDate = ZonedDateTime.now(ZoneId.of("GMT"));
    }

    /**
     * TestCaseID: Get-Hascode-Validation-Request-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input correct values for simple report
     *
     * Expected Result: Simple report is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void okHashcodeValidationWithSimpleReport() {
        JSONHashcodeValidationRequest request = validRequestBody();
        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input correct values for detailed report
     *
     * Expected Result: Detailed report is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void okHashcodeValidationWithDetailedReport() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType(ReportType.DETAILED.getValue());
        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertDetailedReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input missing signature file
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signatureFileMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_FILE, MAY_NOT_BE_EMPTY),
                new RequestError(SIGNATURE_FILE, SIGNATURE_FILE_NOT_BASE64_ENCODED)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input missing signature file
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signatureFileEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile("");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_FILE, MAY_NOT_BE_EMPTY),
                new RequestError(SIGNATURE_FILE, SIGNATURE_FILE_NOT_BASE64_ENCODED)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input incorrect signature file
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signatureFileNotBase64Encoded() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile("NOT.BASE64.ENCODED.VALUE");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_FILE, SIGNATURE_FILE_NOT_BASE64_ENCODED)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input file without signature
     *
     * Expected Result: Validation report is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signatureFileContentWithoutSignature() {
        String randomXmlFileWithoutSignature = "PD94bWwgdmVyc2lvbj0nMS4wJyAgZW5jb2Rpbmc9J1VURi04JyA/Pg0KPHRlc3Q+DQoJPGRhdGE+DQoJCTxzb21ldGhpbmc+c29tZSBkYXRhPC9zb21ldGhpbmc+DQoJPC9kYXRhPg0KPC90ZXN0Pg0K";
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile(randomXmlFileWithoutSignature);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithoutSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Not correct file type
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signatureFileContentNotXML() {
        String notXmlFormattedContent = Base64.encodeBase64String("NOT_XML_FORMATTED_FILE_CONTENT".getBytes(StandardCharsets.UTF_8));
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile(notXmlFormattedContent);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_FILE, SIGNATURE_FILE_MALFORMED)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input missing filename
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void filenameMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(FILENAME, MAY_NOT_BE_EMPTY),
                new RequestError(FILENAME, INVALID_FILENAME),
                new RequestError(FILENAME, INVALID_FILENAME_EXTENSION)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input empty filename
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void filenameEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename("");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(FILENAME, INVALID_FILENAME_SIZE),
                new RequestError(FILENAME, INVALID_FILENAME_EXTENSION),
                new RequestError(FILENAME, MAY_NOT_BE_EMPTY)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input whitespace filename
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void filenameEmptyWhitespace() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename(" ");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(FILENAME, INVALID_FILENAME_EXTENSION),
                new RequestError(FILENAME, MAY_NOT_BE_EMPTY)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input too long filename
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void filenameTooLong() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename(StringUtils.repeat('a', 260 + 1 - SIGNATURE_FILENAME_SUFFIX.length()) + SIGNATURE_FILENAME_SUFFIX);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(FILENAME, INVALID_FILENAME_SIZE));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input invalid format filename
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void filenameInvalidFormat() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename("*:?!");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(FILENAME, INVALID_FILENAME_EXTENSION)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input wrong file type in filename
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void filenameInvalidExtension() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename("signature.pdf");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(FILENAME, INVALID_FILENAME_EXTENSION)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Report type missing
     *
     * Expected Result: Default is used
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void reportTypeMissing_defaultsToSimple() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Report type case sensitivity
     *
     * Expected Result: Report type is case insensitive
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void reportTypeCaseInsensitive() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType("SiMpLe");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Report type is invalid
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void reportTypeInvalid() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType("INVALID_REPORT_TYPE");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(REPORT_TYPE, INVALID_REPORT_TYPE));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Signature policy missing
     *
     * Expected Result: Default is used
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signaturePolicyMissing_defaultsToPOLv4() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request))
                .then()
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyName", equalTo(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2));

        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-18
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Signature policy is invalid
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signaturePolicyInvalid() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy("POLv2");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_POLICY, "Invalid signature policy: " + request.getSignaturePolicy() + "; Available abstractPolicies: [POLv3, POLv4]"));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-19
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Incorrect signature policy format
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signaturePolicyInvalidFormat() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy("POLv2.*");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_POLICY, INVALID_SIGNATURE_POLICY));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-20
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Signature policy is empty
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signaturePolicyEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy("");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_POLICY, INVALID_POLICY_SIZE));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-21
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Signature policy too long
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signaturePolicyTooLong() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy(StringUtils.repeat('a', 101));

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_POLICY, INVALID_POLICY_SIZE)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-22
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file missing
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFilesMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setDatafiles(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES, MAY_NOT_BE_EMPTY),
                new RequestError(DATAFILES, MAY_NOT_BE_NULL)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-23
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Empty data files list
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFilesEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setDatafiles(new ArrayList<>());

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES, MAY_NOT_BE_EMPTY),
                new RequestError(DATAFILES, MAY_NOT_BE_NULL)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-24
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file filename missing
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileFilenameMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_FILENAME, INVALID_FILENAME),
                new RequestError(DATAFILES_FILENAME, MAY_NOT_BE_EMPTY)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-25
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file filename empty
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileFilenameEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename("");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_FILENAME, INVALID_FILENAME_SIZE),
                new RequestError(DATAFILES_FILENAME, MAY_NOT_BE_EMPTY)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-26
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file filename too long
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileFilenameTooLong() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename(StringUtils.repeat('a', 261));

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_FILENAME, INVALID_FILENAME_SIZE)
        );
    }


    /**
     * TestCaseID: Hascode-Validation-Request-28
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file invalid hash algorithm
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileHashAlgorithmInvalid() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHashAlgo("INVALID_HASH_ALGORITHM");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH_ALGO, INVALID_HASH_ALGO)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-29
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file hash algorithm case sensitivity
     *
     * Expected Result: Validation report is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileHashAlgorithmCaseInsensitive() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHashAlgo("sha256");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-30
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file hash missing
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileHashMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHash(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH, MAY_NOT_BE_EMPTY),
                new RequestError(DATAFILES_HASH, INVALID_BASE_64)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-31
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file hash empty
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileHashEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHash("");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH, MAY_NOT_BE_EMPTY),
                new RequestError(DATAFILES_HASH, INVALID_BASE_64)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-32
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data file hash wrong format
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileHashNotBase64Encoded() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHash("NOT.BASE64.ENCODED.VALUE");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH, INVALID_BASE_64)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-33
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Excess data files are ignored
     *
     * Expected Result: Validation report is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void multipleDataFiles_firstDataFileIncorrect_secondDataFileCorrect() {
        JSONHashcodeValidationRequest request = validRequestBody();

        Datafile invalidDataFile = new Datafile();
        invalidDataFile.setHash(Base64.encodeBase64String("INVALID_SIGNATURE_DIGEST".getBytes(StandardCharsets.UTF_8)));
        invalidDataFile.setHashAlgo(TestData.MOCK_XADES_DATAFILE_HASH_ALGO);
        invalidDataFile.setFilename("INVALID_FILE");

        Datafile validDataFile = new Datafile();
        validDataFile.setHash(TestData.MOCK_XADES_DATAFILE_HASH);
        validDataFile.setHashAlgo(TestData.MOCK_XADES_DATAFILE_HASH_ALGO);
        validDataFile.setFilename(TestData.MOCK_XADES_DATAFILE_FILENAME);

        request.setDatafiles(Arrays.asList(
                invalidDataFile,
                validDataFile)
        );

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    private void assertErrorResponse(ValidatableResponse response, RequestError... requestErrors) {
        response
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors", hasSize(requestErrors.length));

        for (RequestError requestError : requestErrors) {
            response.body("requestErrors.findAll { requestError -> " +
                    "requestError.key == '" + requestError.getKey() + "' && " +
                    "requestError.message=='" + requestError.getMessage() + "' }", hasSize(1));
        }
    }

    private void assertDetailedReportWithSignature(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        assertSimpleReportWithSignature(response, request);
        response
                .body("validationReport.validationProcess.signatures", hasSize(1))
                .body("validationReport.validationProcess.signatures[0].validationProcessBasicSignatures.conclusion.indication", is(TestData.VALID_INDICATION_VALUE_PASSED))
                .body("validationReport.validationProcess.signatures[0].validationProcessTimestamps.conclusion.indication", hasItem(TestData.VALID_INDICATION_VALUE_PASSED))
                .body("validationReport.validationProcess.signatures[0].validationProcessLongTermData.conclusion.indication", is(TestData.VALID_INDICATION_VALUE_PASSED))
                .body("validationReport.validationProcess.signatures[0].validationProcessArchivalData.conclusion.indication", is(TestData.VALID_INDICATION_VALUE_PASSED))

                .body("validationReport.validationProcess.tlanalysis", hasSize(2))
                .body("validationReport.validationProcess.tlanalysis[0].countryCode", is("EU"))
                .body("validationReport.validationProcess.tlanalysis[0].conclusion.indication", is(TestData.VALID_INDICATION_VALUE_PASSED))
                .body("validationReport.validationProcess.tlanalysis[1].countryCode", is("EE"))
                .body("validationReport.validationProcess.tlanalysis[1].conclusion.indication", is(TestData.VALID_INDICATION_VALUE_PASSED));
    }

    private void assertSimpleReportWithSignature(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        assertValidationConclusion(response, request);
        assertSignatureTotalPassed(response);
    }

    private void assertSimpleReportWithoutSignature(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        assertValidationConclusion(response, request);
        response
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures", isEmptyOrNullString())
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", is(0))
                .body(VALIDATION_CONCLUSION_PREFIX + "signaturesCount", is(0));
    }

    private void assertValidationConclusion(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        response.statusCode(HttpStatus.OK.value())
                .body(VALIDATION_CONCLUSION_PREFIX + "validationTime", DateTimeMatcher.isEqualOrAfter(testStartDate))
                .body(VALIDATION_CONCLUSION_PREFIX + "validatedDocument.filename", equalTo(request.getFilename()))
                .body(VALIDATION_CONCLUSION_PREFIX + "validationLevel", is(TestData.VALID_VALIDATION_LEVEL_ARCHIVAL_DATA));

        ValidationPolicy signaturePolicy;
        if (request.getSignaturePolicy() == null) {
            signaturePolicy = determineValidationPolicy(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2);
        } else {
            signaturePolicy = determineValidationPolicy(request.getSignaturePolicy());
        }

        response
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyDescription", equalTo(signaturePolicy.getDescription()))
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyName", equalTo(signaturePolicy.getName()))
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyUrl", equalTo(signaturePolicy.getUrl()));
    }

    private ValidationPolicy determineValidationPolicy(String signaturePolicy) {
        if (TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_1.equals(signaturePolicy)) {
            return PredefinedValidationPolicySource.ADES_POLICY;
        } else if (TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2.equals(signaturePolicy)) {
            return PredefinedValidationPolicySource.QES_POLICY;
        } else {
            throw new IllegalArgumentException("Unknown validation policy '" + signaturePolicy + "'");
        }
    }

    private void assertSignatureTotalPassed(ValidatableResponse response) {
        response
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].id", is(TestData.MOCK_XADES_SIGNATURE_ID))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureFormat", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_FORMAT_XADES_LT))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureLevel", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_LEVEL_ADESIG_QC))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signedBy", is(TestData.MOCK_XADES_SIGNATURE_SIGNER))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].indication", is(TestData.VALID_INDICATION_TOTAL_PASSED))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].name", is(TestData.MOCK_XADES_DATAFILE_FILENAME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].scope", is(TestData.VALID_SIGNATURE_SCOPE_VALUE_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].content", is(TestData.VALID_SIGNATURE_SCOPE_CONTENT_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].claimedSigningTime", is(TestData.MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].info.bestSignatureTime", is(TestData.MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].errors", Matchers.isEmptyOrNullString())
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signaturesCount", is(1));
    }

    private void assertSignatureDataNotFound(ValidatableResponse response) {
        response
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].id", is(TestData.MOCK_XADES_SIGNATURE_ID))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureFormat", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_FORMAT_XADES_LT))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureLevel", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_LEVEL_INDETERMINATE_ADESIG_QC))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signedBy", is(TestData.MOCK_XADES_SIGNATURE_SIGNER))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].indication", is(TestData.VALID_INDICATION_VALUE_INDETERMINATE))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].subIndication", is(TestData.SUB_INDICATION_SIGNED_DATA_NOT_FOUND))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].name", is(TestData.MOCK_XADES_DATAFILE_FILENAME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].scope", is(TestData.VALID_SIGNATURE_SCOPE_VALUE_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].content", is(TestData.VALID_SIGNATURE_SCOPE_CONTENT_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].claimedSigningTime", is(TestData.MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].info.bestSignatureTime", is(TestData.MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].errors", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].errors[0].content" , is("The reference data object(s) is not found!"))
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", is(0))
                .body(VALIDATION_CONCLUSION_PREFIX + "signaturesCount", is(1));
    }

    private void assertSignatureHashFailure(ValidatableResponse response) {
        response
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].id", is(TestData.MOCK_XADES_SIGNATURE_ID))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureFormat", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_FORMAT_XADES_LT))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureLevel", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_LEVEL_NOT_ADES_QC))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signedBy", is(TestData.MOCK_XADES_SIGNATURE_SIGNER))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].indication", is(TestData.VALID_INDICATION_TOTAL_FAILED))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].subIndication", is(TestData.SUB_INDICATION_HASH_FAILURE))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].name", is(TestData.MOCK_XADES_DATAFILE_FILENAME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].scope", is(TestData.VALID_SIGNATURE_SCOPE_VALUE_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].content", is(TestData.VALID_SIGNATURE_SCOPE_CONTENT_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].claimedSigningTime", is(TestData.MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].info.bestSignatureTime", is(TestData.MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].errors", hasSize(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].errors[0].content" , is("The reference data object(s) is not intact!"))
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", is(0))
                .body(VALIDATION_CONCLUSION_PREFIX + "signaturesCount", is(1));
    }

    private JSONHashcodeValidationRequest validRequestBody() {
        JSONHashcodeValidationRequest request = new JSONHashcodeValidationRequest();
        request.setSignatureFile(Base64.encodeBase64String(readFileFromTestResources(TestData.MOCK_XADES_SIGNATURE_FILE)));
        request.setFilename(TestData.MOCK_XADES_SIGNATURE_FILE_NAME);
        request.setReportType(ReportType.SIMPLE.getValue());
        request.setSignaturePolicy(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_1);

        Datafile datafile = new Datafile();
        datafile.setHash(TestData.MOCK_XADES_DATAFILE_HASH);
        datafile.setHashAlgo(TestData.MOCK_XADES_DATAFILE_HASH_ALGO);
        datafile.setFilename(TestData.MOCK_XADES_DATAFILE_FILENAME);
        request.setDatafiles(Arrays.asList(datafile));
        return request;
    }

    private String toRequest(JSONHashcodeValidationRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (request.getSignatureFile() != null) {
            jsonObject.put(SIGNATURE_FILE, request.getSignatureFile());
        }
        if (request.getFilename() != null) {
            jsonObject.put(FILENAME, request.getFilename());
        }
        if (request.getSignaturePolicy() != null) {
            jsonObject.put(SIGNATURE_POLICY, request.getSignaturePolicy());
        }
        if (request.getReportType() != null) {
            jsonObject.put(REPORT_TYPE, request.getReportType());
        }
        if (CollectionUtils.isNotEmpty(request.getDatafiles())) {
            jsonObject.put(DATAFILES, request.getDatafiles());
        }
        return jsonObject.toString();
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @Data
    @AllArgsConstructor
    private static class RequestError {
        private String key;
        private String message;
    }
}
