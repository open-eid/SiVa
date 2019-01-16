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

package ee.openeid.siva.soaptest;

import ee.openeid.siva.common.DateTimeMatcher;
import ee.openeid.siva.integrationtest.TestData;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.webapp.request.Datafile;
import ee.openeid.siva.webapp.request.JSONHashcodeValidationRequest;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
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
public class SoapHashcodeValidationRequestIT extends SiVaSoapTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";
    private static final String SIGNATURE_FILENAME_SUFFIX = ".xml";
    private static final String VALIDATION_CONCLUSION_PREFIX = "Envelope.Body.HashcodeValidationResponse.ValidationReport.ValidationConclusion.";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;
    private ZonedDateTime testStartDate;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        testStartDate = ZonedDateTime.now(ZoneId.of("GMT"));
    }

    @Test
    public void okHashcodeValidationWithSimpleReport() {
        JSONHashcodeValidationRequest request = validRequestBody();
        ValidatableResponse response = postHashcodeValidation(request).then();
        assertSimpleReportWithSignature(response, request);
    }

    @Test
    public void okHashcodeValidationWithDetailedReport() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType(ReportType.DETAILED.getValue());
        ValidatableResponse response = postHashcodeValidation(request).then();
        assertDetailedReportWithSignature(response, request);
    }

    @Test
    public void signatureFileMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile(null);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response, "Unmarshalling Error: cvc-complex-type.2.4.a: Invalid content was found starting with element 'Filename'. One of '{SignatureFile}' is expected. ");
    }

    @Test
    public void signatureFileEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile("");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response, "Unmarshalling Error: cvc-pattern-valid: Value '' is not facet-valid with respect to pattern '\\S+' for type 'NotEmptyString'. ");
    }

    @Test
    public void signatureFileNotBase64Encoded() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile("NOT.BASE64.ENCODED.VALUE");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response, SIGNATURE_FILE_NOT_BASE64_ENCODED);
    }

    @Test
    public void signatureFileContentWithoutSignature() {
        String randomXmlFileWithoutSignature = "PD94bWwgdmVyc2lvbj0nMS4wJyAgZW5jb2Rpbmc9J1VURi04JyA/Pg0KPHRlc3Q+DQoJPGRhdGE+DQoJCTxzb21ldGhpbmc+c29tZSBkYXRhPC9zb21ldGhpbmc+DQoJPC9kYXRhPg0KPC90ZXN0Pg0K";
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile(randomXmlFileWithoutSignature);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertSimpleReportWithoutSignature(response, request);
    }

    @Test
    public void signatureFileContentNotXML() {
        String notXmlFormattedContent = Base64.encodeBase64String("NOT_XML_FORMATTED_FILE_CONTENT".getBytes(StandardCharsets.UTF_8));
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignatureFile(notXmlFormattedContent);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response, SIGNATURE_FILE_MALFORMED);
    }

    @Test
    public void filenameMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename(null);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Invalid filename format");
    }

    @Test
    public void filenameEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename("");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Invalid filename format");
    }

    @Test
    public void filenameEmptyWhitespace() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename(" ");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Invalid filename format");
    }

    @Test
    public void filenameTooLong() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename(StringUtils.repeat('a', 260 + 1 - SIGNATURE_FILENAME_SUFFIX.length()) + SIGNATURE_FILENAME_SUFFIX);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-maxLength-valid: Value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.xml' with length = '261' is not facet-valid with respect to maxLength '260' for type 'Filename'. ");
    }

    @Test
    public void filenameInvalidExtension() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setFilename("signature.pdf");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response, INVALID_FILENAME_EXTENSION);
    }

    @Test
    public void reportTypeMissing_defaultsToSimple() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType(null);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertSimpleReportWithSignature(response, request);
    }

    @Test
    public void reportTypeCaseInsensitive() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType("SiMpLe");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertSimpleReportWithSignature(response, request);
    }

    @Test
    public void reportTypeInvalid() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setReportType("INVALID_REPORT_TYPE");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-enumeration-valid: Value 'INVALID_REPORT_TYPE' is not facet-valid with respect to enumeration '[SIMPLE, DETAILED]'. It must be a value from the enumeration. ");
    }

    @Test
    public void signaturePolicyMissing_defaultsToPOLv4() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy(null);

        ValidatableResponse response = postHashcodeValidation(request).then()
                .body(VALIDATION_CONCLUSION_PREFIX + "Policy.PolicyName", equalTo(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2));

        assertSimpleReportWithSignature(response, request);
    }

    @Test
    public void signaturePolicyInvalid() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy("POLv2");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Invalid signature policy: " + request.getSignaturePolicy() + "; Available abstractPolicies: [POLv3, POLv4]");
    }

    @Test
    public void signaturePolicyInvalidFormat() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy("POLv2.*");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-pattern-valid: Value 'POLv2.*' is not facet-valid with respect to pattern '[A-Za-z0-9_ -]*' for type 'SignaturePolicy'. ");
    }

    @Test
    public void signaturePolicyTooLong() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setSignaturePolicy(StringUtils.repeat('a', 101));

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-maxLength-valid: Value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' with length = '101' is not facet-valid with respect to maxLength '100' for type 'SignaturePolicy'. ");
    }

    @Test
    public void dataFilesMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setDatafiles(null);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-complex-type.2.4.b: The content of element 'soap:HashcodeValidationRequest' is not complete. One of '{DataFiles}' is expected. ");
    }

    @Test
    public void dataFilesEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.setDatafiles(new ArrayList<>());

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-complex-type.2.4.b: The content of element 'DataFiles' is not complete. One of '{DataFile}' is expected. ");
    }

    @Test
    public void dataFileFilenameMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename(null);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-complex-type.2.4.a: Invalid content was found starting with element 'HashAlgo'. One of '{Filename}' is expected. ");
    }

    @Test
    public void dataFileFilenameEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename("");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Invalid datafile filename format");
    }

    @Test
    public void dataFileFilenameEmptyWhitespace() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename(" ");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Invalid datafile filename format");
    }

    @Test
    public void dataFileFilenameTooLong() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename(StringUtils.repeat('a', 261));

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-maxLength-valid: Value 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' with length = '261' is not facet-valid with respect to maxLength '260' for type 'Filename'. ");
    }

    @Test
    public void dataFileHashAlgorithmInvalid() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHashAlgo("INVALID_HASH_ALGORITHM");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-enumeration-valid: Value 'INVALID_HASH_ALGORITHM' is not facet-valid with respect to enumeration '[SHA1, SHA224, SHA256, SHA384, SHA512, RIPEMD160, MD2, MD5]'. It must be a value from the enumeration. ");
    }

    @Test
    public void dataFileHashAlgorithmCaseInsensitive() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHashAlgo("sha256");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertSimpleReportWithSignature(response, request);
    }

    @Test
    public void dataFileHashAlgorithmDoesNotMatchWithSignatureDataFileHashAlgorithm() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHashAlgo("SHA512");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertValidationConclusion(response, request);
        assertSignatureDataNotFound(response);
    }

    @Test
    public void dataFileHashMissing() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHash(null);

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-complex-type.2.4.b: The content of element 'DataFile' is not complete. One of '{Hash}' is expected. ");
    }

    @Test
    public void dataFileHashEmpty() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHash("");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response,
                "Unmarshalling Error: cvc-pattern-valid: Value '' is not facet-valid with respect to pattern '\\S+' for type 'NotEmptyString'. ");
    }

    @Test
    public void dataFileHashNotBase64Encoded() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHash("NOT.BASE64.ENCODED.VALUE");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertClientFault(response, INVALID_BASE_64);
    }

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

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertSimpleReportWithSignature(response, request);
    }

    @Test
    public void dataFileHashDoesNotMatchWithSignatureFile_totalFailedHashFailure() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setHash(Base64.encodeBase64String("INVALID_SIGNATURE_DIGEST".getBytes(StandardCharsets.UTF_8)));

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertValidationConclusion(response, request);
        assertSignatureHashFailure(response);
    }

    @Test
    public void dataFileHashCorrectButFilenameDoesNotMatchWithSignatureFile() {
        JSONHashcodeValidationRequest request = validRequestBody();
        request.getDatafiles().get(0).setFilename("INVALID_FILE_NAME.pdf");

        ValidatableResponse response = postHashcodeValidation(request).then();
        assertValidationConclusion(response, request);
        assertSignatureDataNotFound(response);
    }

    @Test
    public void validationRequestBodyEmpty() {
        String emptyRequestBody =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
        postHashcodeValidation(emptyRequestBody)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("Envelope.Body.Fault.faultcode", is(CLIENT_FAULT))
                .body("Envelope.Body.Fault.faultstring", is("Invalid filename format"));
    }

    private void assertDetailedReportWithSignature(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        assertSimpleReportWithSignature(response, request);
        response
                .body("Envelope.Body.HashcodeValidationResponse.ValidationReport.ValidationProcess.Signatures.ValidationProcessBasicSignatures.Conclusion.Indication", is(TestData.VALID_INDICATION_VALUE_PASSED))
                .body("Envelope.Body.HashcodeValidationResponse.ValidationReport.ValidationProcess.Signatures.ValidationProcessTimestamps.Conclusion.Indication", is(TestData.VALID_INDICATION_VALUE_PASSED))
                .body("Envelope.Body.HashcodeValidationResponse.ValidationReport.ValidationProcess.Signatures.ValidationProcessLongTermData.Conclusion.Indication", is(TestData.VALID_INDICATION_VALUE_PASSED))
                .body("Envelope.Body.HashcodeValidationResponse.ValidationReport.ValidationProcess.Signatures.ValidationProcessArchivalData.Conclusion.Indication", is(TestData.VALID_INDICATION_VALUE_PASSED));
    }

    private void assertSimpleReportWithSignature(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        assertValidationConclusion(response, request);
        assertSignatureTotalPassed(response);
    }

    private void assertSimpleReportWithoutSignature(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        assertValidationConclusion(response, request);
        response
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures", isEmptyOrNullString())
                .body(VALIDATION_CONCLUSION_PREFIX + "ValidSignaturesCount", is("0"))
                .body(VALIDATION_CONCLUSION_PREFIX + "SignaturesCount", is("0"));
    }

    private void assertValidationConclusion(ValidatableResponse response, JSONHashcodeValidationRequest request) {
        response.statusCode(HttpStatus.OK.value())
                .body(VALIDATION_CONCLUSION_PREFIX + "ValidationTime", DateTimeMatcher.isEqualOrAfter(testStartDate))
                .body(VALIDATION_CONCLUSION_PREFIX + "ValidatedDocument.Filename", equalTo(request.getFilename()))
                .body(VALIDATION_CONCLUSION_PREFIX + "ValidationLevel", is(TestData.VALID_VALIDATION_LEVEL_ARCHIVAL_DATA));

        ValidationPolicy signaturePolicy;
        if (request.getSignaturePolicy() == null) {
            signaturePolicy = determineValidationPolicy(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2);
        } else {
            signaturePolicy = determineValidationPolicy(request.getSignaturePolicy());
        }

        response
                .body(VALIDATION_CONCLUSION_PREFIX + "Policy.PolicyDescription", equalTo(signaturePolicy.getDescription()))
                .body(VALIDATION_CONCLUSION_PREFIX + "Policy.PolicyName", equalTo(signaturePolicy.getName()))
                .body(VALIDATION_CONCLUSION_PREFIX + "Policy.PolicyUrl", equalTo(signaturePolicy.getUrl()));
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
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Id", is(TestData.MOCK_XADES_SIGNATURE_ID))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureFormat", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_FORMAT_XADES_LT))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureLevel", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_LEVEL_ADESIG_QC))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignedBy", is(TestData.MOCK_XADES_SIGNATURE_SIGNER))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Indication", is(TestData.VALID_INDICATION_TOTAL_PASSED))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", is(TestData.MOCK_XADES_DATAFILE_FILENAME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", is(TestData.VALID_SIGNATURE_SCOPE_VALUE_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", is(TestData.VALID_SIGNATURE_SCOPE_CONTENT_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].ClaimedSigningTime", is(TestData.MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Info.BestSignatureTime", is(TestData.MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Errors", Matchers.isEmptyOrNullString())
                .body(VALIDATION_CONCLUSION_PREFIX + "ValidSignaturesCount", is("1"))
                .body(VALIDATION_CONCLUSION_PREFIX + "SignaturesCount", is("1"));
    }

    private void assertSignatureDataNotFound(ValidatableResponse response) {
        response
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Id", is(TestData.MOCK_XADES_SIGNATURE_ID))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureFormat", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_FORMAT_XADES_LT))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureLevel", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_LEVEL_INDETERMINATE_ADESIG_QC))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignedBy", is(TestData.MOCK_XADES_SIGNATURE_SIGNER))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Indication", is(TestData.VALID_INDICATION_VALUE_INDETERMINATE))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SubIndication", is(TestData.SUB_INDICATION_SIGNED_DATA_NOT_FOUND))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", is(TestData.MOCK_XADES_DATAFILE_FILENAME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", is(TestData.VALID_SIGNATURE_SCOPE_VALUE_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", is(TestData.VALID_SIGNATURE_SCOPE_CONTENT_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].ClaimedSigningTime", is(TestData.MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Info.BestSignatureTime", is(TestData.MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Errors.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Errors.Error[0].Content" , is("The reference data object(s) is not found!"))
                .body(VALIDATION_CONCLUSION_PREFIX + "ValidSignaturesCount", is("0"))
                .body(VALIDATION_CONCLUSION_PREFIX + "SignaturesCount", is("1"));
    }

    private void assertSignatureHashFailure(ValidatableResponse response) {
        response
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Id", is(TestData.MOCK_XADES_SIGNATURE_ID))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureFormat", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_FORMAT_XADES_LT))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureLevel", is(TestData.VALID_VALIDATION_CONCLUSION_SIGNATURE_LEVEL_NOT_ADES_QC))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignedBy", is(TestData.MOCK_XADES_SIGNATURE_SIGNER))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Indication", is(TestData.VALID_INDICATION_TOTAL_FAILED))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SubIndication", is(TestData.SUB_INDICATION_HASH_FAILURE))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", is(TestData.MOCK_XADES_DATAFILE_FILENAME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", is(TestData.VALID_SIGNATURE_SCOPE_VALUE_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", is(TestData.VALID_SIGNATURE_SCOPE_CONTENT_1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].ClaimedSigningTime", is(TestData.MOCK_XADES_SIGNATURE_CLAIMED_SIGNING_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Info.BestSignatureTime", is(TestData.MOCK_XADES_SIGNATURE_BEST_SIGNATURE_TIME))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Errors.children().size()", is(1))
                .body(VALIDATION_CONCLUSION_PREFIX + "Signatures.Signature[0].Errors.Error[0].Content" , is("The reference data object(s) is not intact!"))
                .body(VALIDATION_CONCLUSION_PREFIX + "ValidSignaturesCount", is("0"))
                .body(VALIDATION_CONCLUSION_PREFIX + "SignaturesCount", is("1"));
    }

    private void assertClientFault(ValidatableResponse response, String errorMessage) {
        response
                .statusCode(HttpStatus.OK.value())
                .body("Envelope.Body.Fault.faultcode", equalTo(CLIENT_FAULT))
                .body("Envelope.Body.Fault.faultstring", equalTo(errorMessage));
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

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
