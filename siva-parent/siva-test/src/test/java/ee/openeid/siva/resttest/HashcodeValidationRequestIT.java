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
import ee.openeid.siva.webapp.request.SignatureFile;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
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
import java.util.Collections;

import static ee.openeid.siva.integrationtest.TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_1;
import static ee.openeid.siva.integrationtest.TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2;
import static org.hamcrest.Matchers.*;

@Category(IntegrationTest.class)
public class HashcodeValidationRequestIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";
    private static final String VALIDATION_CONCLUSION_PREFIX = "validationReport.validationConclusion.";

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
     * TestCaseID: Hascode-Validation-Request-Report-Type-1
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
     * TestCaseID: Hascode-Validation-Request-Report-Type-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input correct values for detailed report
     *
     * Expected Result: Simple report is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void simpleReportIsReturnedWithDetailedReportType() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType(ReportType.DETAILED.getValue());
        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Report-Type-3
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
    public void reportTypeMissingDefaultsToSimple() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Report-Type-4
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
     * TestCaseID: Hascode-Validation-Request-Report-Type-5
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
     * TestCaseID: Hascode-Validation-Request-Policy-1
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
    public void signaturePolicyPOLv3() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_1);

        ValidatableResponse response = postHashcodeValidation(toRequest(request))
                .then()
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyName", equalTo(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_1));

        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Policy-2
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
    public void signaturePolicyPOLv4() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2);

        ValidatableResponse response = postHashcodeValidation(toRequest(request))
                .then()
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyName", equalTo(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2));

        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Policy-3
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
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyName", equalTo(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2));

        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Policy-4
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
     * TestCaseID: Hascode-Validation-Request-Policy-5
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
        request.setSignaturePolicy("POLv3.*");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_POLICY, INVALID_SIGNATURE_POLICY));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Policy-6
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
     * TestCaseID: Hascode-Validation-Request-Policy-7
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
     * TestCaseID: Hascode-Validation-Request-Policy-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Signature policy too long
     *
     * Expected Result: Last value is used
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void doubleSignaturePolicy() {
        String file = Base64.encodeBase64String(readFileFromTestResources(TestData.MOCK_XADES_SIGNATURE_FILE));

        String request = "{\n" +
                "    \"reportType\": \"Simple\",\n" +
                "    \"signaturePolicy\": \"POLv3\",\n" +
                "    \"signaturePolicy\": \"POLv.5\",\n" +
                "    \"signatureFiles\": [\n" +
                "        {\n" +
                "            \"signature\": \"" + file + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_POLICY, INVALID_SIGNATURE_POLICY));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Signature-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input missing signature files
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signatureFileMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();

        request.setSignatureFiles(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_FILES, MAY_NOT_BE_EMPTY),
                new RequestError(SIGNATURE_FILES, MAY_NOT_BE_NULL)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Signature-2
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
        request.setSignatureFiles(Collections.emptyList());

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE_FILES, MAY_NOT_BE_EMPTY)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Signature-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Input incorrect signature
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void signatureFileNotBase64Encoded() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getSignatureFiles().get(0).setSignature("NOT.BASE64.ENCODED.VALUE");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response, new RequestError(SIGNATURE_INDEX_0, SIGNATURE_FILE_NOT_BASE64_ENCODED));
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Signature-4
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
        request.getSignatureFiles().get(0).setSignature(randomXmlFileWithoutSignature);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithoutSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Signature-5
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
    public void signatureContentNotXML() {
        String notXmlFormattedContent = Base64.encodeBase64String("NOT_XML_FORMATTED_FILE_CONTENT".getBytes(StandardCharsets.UTF_8));
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getSignatureFiles().get(0).setSignature(notXmlFormattedContent);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(SIGNATURE, SIGNATURE_MALFORMED)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Data files not in request
     *
     * Expected Result: Simple report is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFilesMissing() {
        JSONHashcodeValidationRequest request = validRequestBodyMinimal();

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-2
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
        request.getSignatureFiles().get(0).setDatafiles(new ArrayList<>());

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES, INVALID_DATAFILES_LIST)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-3
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setFilename(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_FILENAME, INVALID_FILENAME),
                new RequestError(DATAFILES_FILENAME, MAY_NOT_BE_EMPTY)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-4
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setFilename("");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_FILENAME, INVALID_FILENAME_SIZE),
                new RequestError(DATAFILES_FILENAME, MAY_NOT_BE_EMPTY)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-5
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setFilename(StringUtils.repeat('a', 261));

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_FILENAME, INVALID_FILENAME_SIZE)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-6
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setHashAlgo("INVALID_HASH_ALGORITHM");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH_ALGO, INVALID_HASH_ALGO)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-7
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setHashAlgo("sha256");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertSimpleReportWithSignature(response, request);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-8
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setHash(null);

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH, MAY_NOT_BE_EMPTY),
                new RequestError(DATAFILES_HASH, INVALID_BASE_64)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-9
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setHash("");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH, MAY_NOT_BE_EMPTY),
                new RequestError(DATAFILES_HASH, INVALID_BASE_64),
                new RequestError(DATAFILES_HASH, INVALID_HASH_SIZE)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-10
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
        request.getSignatureFiles().get(0).getDatafiles().get(0).setHash("NOT.BASE64.ENCODED.VALUE");

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH, INVALID_BASE_64)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Hash too long
     *
     * Expected Result: Error is returned
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void dataFileHasTooLong() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getSignatureFiles().get(0).getDatafiles().get(0).setHash(StringUtils.repeat('P', 1001));

        ValidatableResponse response = postHashcodeValidation(toRequest(request)).then();
        assertErrorResponse(response,
                new RequestError(DATAFILES_HASH, INVALID_HASH_SIZE)
        );
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Policy-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-request-interface-for-hashcode
     *
     * Title: Double fields in datafile object
     *
     * Expected Result: Last value is used
     *
     * File: hashAsiceXades.xml
     **/
    @Test
    public void doubleFieldsInDatafiles() {
        String file = Base64.encodeBase64String(readFileFromTestResources(TestData.MOCK_XADES_SIGNATURE_FILE));

        String request = "{\n" +
                "    \"reportType\": \"Simple\",\n" +
                "    \"signaturePolicy\": \"POLv4\",\n" +
                "    \"signatureFiles\": [\n" +
                "        {\n" +
                "            \"signature\":\"" + file + "\",\n" +
                "            \"datafiles\": [\n" +
                "                {\n" +
                "                    \"filename\": \"test2.pdf\",\n" +
                "                    \"hashAlgo\": \"SHA512\",\n" +
                "                    \"hash\": \"IucjUcbRo9RkdsfdfsscwiIiplP9pSrSPr7LKln1EiI=\",\n" +
                "                    \"filename\": \"test.pdf\",\n" +
                "                    \"hashAlgo\": \"SHA256\",\n" +
                "                    \"hash\": \"IucjUcbRo9Rke0bZLiHcwiIiplP9pSrSPr7LKln1EiI=\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertSignatureTotalPassed(response);
    }

    /**
     * TestCaseID: Hascode-Validation-Request-Datafiles-12
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
    public void multipleDataFilesOneNotInSignature() {
        JSONHashcodeValidationRequest request = validRequestBody();

        Datafile invalidDataFile = new Datafile();
        invalidDataFile.setHash(Base64.encodeBase64String("INVALID_SIGNATURE_DIGEST".getBytes(StandardCharsets.UTF_8)));
        invalidDataFile.setHashAlgo(TestData.MOCK_XADES_DATAFILE_HASH_ALGO);
        invalidDataFile.setFilename("INVALID_FILE");

        Datafile validDataFile = new Datafile();
        validDataFile.setHash(TestData.MOCK_XADES_DATAFILE_HASH);
        validDataFile.setHashAlgo(TestData.MOCK_XADES_DATAFILE_HASH_ALGO);
        validDataFile.setFilename(TestData.MOCK_XADES_DATAFILE_FILENAME);

        request.getSignatureFiles().get(0).setDatafiles(Arrays.asList(
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
                .body(VALIDATION_CONCLUSION_PREFIX + "validationLevel", is(TestData.VALID_VALIDATION_LEVEL_ARCHIVAL_DATA));

        ValidationPolicy signaturePolicy;
        if (request.getSignaturePolicy() == null) {
            signaturePolicy = determineValidationPolicy(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2);
        } else {
            signaturePolicy = determineValidationPolicy(request.getSignaturePolicy());
        }

        response
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyDescription", equalTo(signaturePolicy.getDescription()))
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyName", equalTo(signaturePolicy.getName()))
                .body(VALIDATION_CONCLUSION_PREFIX + "policy.policyUrl", equalTo(signaturePolicy.getUrl()));
    }

    private ValidationPolicy determineValidationPolicy(String signaturePolicy) {
        if (VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_1.equals(signaturePolicy)) {
            return PredefinedValidationPolicySource.ADES_POLICY;
        } else if (VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2.equals(signaturePolicy)) {
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
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].hashAlgo", is(TestData.MOCK_XADES_DATAFILE_HASH_ALGO))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].signatureScopes[0].hash", is(TestData.MOCK_XADES_DATAFILE_HASH))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].claimedSigningTime", is(TestData.MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].info.bestSignatureTime", is(TestData.MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "signatures[0].errors", Matchers.isEmptyOrNullString())
                .body(VALIDATION_CONCLUSION_PREFIX + "validSignaturesCount", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "signaturesCount", is(1));
    }

    private JSONHashcodeValidationRequest validRequestBody() {
        JSONHashcodeValidationRequest request = new JSONHashcodeValidationRequest();
        Datafile datafile = new Datafile();
        datafile.setHash(TestData.MOCK_XADES_DATAFILE_HASH);
        datafile.setHashAlgo(TestData.MOCK_XADES_DATAFILE_HASH_ALGO);
        datafile.setFilename(TestData.MOCK_XADES_DATAFILE_FILENAME);

        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature(Base64.encodeBase64String(readFileFromTestResources(TestData.MOCK_XADES_SIGNATURE_FILE)));
        signatureFile.setDatafiles(Collections.singletonList(datafile));

        request.setReportType(ReportType.SIMPLE.getValue());
        request.setSignaturePolicy(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2);

        request.setSignatureFiles(Collections.singletonList(signatureFile));
        return request;
    }

    private JSONHashcodeValidationRequest validRequestBodyMinimal() {
        JSONHashcodeValidationRequest request = new JSONHashcodeValidationRequest();
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature(Base64.encodeBase64String(readFileFromTestResources(TestData.MOCK_XADES_SIGNATURE_FILE)));
        request.setSignatureFiles(Collections.singletonList(signatureFile));
        return request;
    }

    private String toRequest(JSONHashcodeValidationRequest request) {
        JSONObject jsonObject = new JSONObject();
        if(request.getSignatureFiles() != null) {
            jsonObject.put(SIGNATURE_FILES, request.getSignatureFiles());
        }
        if (request.getSignaturePolicy() != null) {
            jsonObject.put(SIGNATURE_POLICY, request.getSignaturePolicy());
        }
        if (request.getReportType() != null) {
            jsonObject.put(REPORT_TYPE, request.getReportType());
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
