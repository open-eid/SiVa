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
package ee.openeid.siva.manualtest;

import ee.openeid.siva.common.DateTimeMatcher;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ee.openeid.siva.integrationtest.TestData.*;
import static org.hamcrest.Matchers.*;

@Category(IntegrationTest.class)

public class DiagnosticReportValidationManualIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signature_cryptographic_algorithm_test_files/";
    private static final String VALIDATION_ENDPOINT = "/validate";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    private Response response;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @Autowired
    private SignatureServiceConfigurationProperties signatureServiceConfigurationProperties;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: ValidationConclusion element
     *
     * Expected Result: Diagnostic report includes validationConclusion element
     *
     * File: ValidLiveSignature.asice
     */
    @Test
    public void diagnosticReportAssertValidValidationConclusionAsicE() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        ZonedDateTime testStartDate = ZonedDateTime.now(ZoneId.of("GMT"));

        post(validationRequestFor("ValidLiveSignature.asice", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("policy.policyDescription", equalTo(POLICY_4_DESCRIPTION))
                .body("policy.policyName", equalTo(SIGNATURE_POLICY_2))
                .body("policy.policyUrl", equalTo(POLICY_4_URL))
                .body("signatureForm", equalTo(SIGNATURE_FORM_ASICE))
                .body("validationTime", DateTimeMatcher.isEqualOrAfter(testStartDate))
                .body("signaturesCount", equalTo(1))
                .body("validSignaturesCount", equalTo(1))
                .body("signatures", notNullValue())
                .body("signatures[0].id", equalTo("S0"))
                .body("signatures[0].signatureFormat", equalTo(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].signatureLevel", equalTo(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].signedBy", equalTo("NURM,AARE,38211015222"))
                .body("signatures[0].indication", equalTo(TOTAL_PASSED))
                .body("signatures[0].signatureScopes[0].name", equalTo("Tresting.txt"))
                .body("signatures[0].signatureScopes[0].scope", equalTo(SIGNATURE_SCOPE_FULL))
                .body("signatures[0].signatureScopes[0].content", equalTo(VALID_SIGNATURE_SCOPE_CONTENT_FULL))
                .body("signatures[0].claimedSigningTime", equalTo("2016-10-11T09:35:48Z"))
                .body("signatures[0].info.bestSignatureTime", equalTo("2016-10-11T09:36:10Z"));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Trusted List element
     *
     * Expected Result: Diagnostic report includes Trust List element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    public  void diagnosticReportAssertTrustedLists(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("trustedLists", notNullValue())
                .body("trustedLists[0].countryCode", equalTo("EE"))
                .body("trustedLists[0].url", equalTo("https://sr.riik.ee/tsl/estonian-tsl.xml"))
                .body("trustedLists[0].sequenceNumber", greaterThanOrEqualTo(46))
                .body("trustedLists[0].version", equalTo(5))
                .body("trustedLists[0].lastLoading", notNullValue())
                .body("trustedLists[0].issueDate", notNullValue())
                .body("trustedLists[0].nextUpdate", notNullValue())
                .body("trustedLists[0].wellSigned", equalTo(true));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: List Of Trusted List element
     *
     * Expected Result: Diagnostic report includes lotl element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    public  void diagnosticReportAssertLotl(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("listOfTrustedLists", notNullValue())
                .body("listOfTrustedLists.countryCode", equalTo("EU"))
                .body("listOfTrustedLists.url", equalTo("https://ec.europa.eu/tools/lotl/eu-lotl.xml"))
                .body("listOfTrustedLists.sequenceNumber", greaterThanOrEqualTo(237))
                .body("listOfTrustedLists.version", equalTo(5))
                .body("listOfTrustedLists.lastLoading", notNullValue())
                .body("listOfTrustedLists.issueDate", notNullValue())
                .body("listOfTrustedLists.nextUpdate", notNullValue())
                .body("listOfTrustedLists.wellSigned", equalTo(true));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Signatures element
     *
     * Expected Result: Diagnostic report includes tlanalysis element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test //TODO: This test may became flaky, we need a mechanism to cover elements changing their order
    public  void diagnosticReportAssertSignature(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("signatures[0]", notNullValue())
                .body("signatures[0].signatureFilename", equalTo("pades-baseline-lta-live-aj.pdf"))
                .body("signatures[0].dateTime", notNullValue())
                .body("signatures[0].signatureFormat", equalTo("PAdES-BASELINE-LTA"))
                .body("signatures[0].contentType", equalTo("application/pdf"))
                .body("signatures[0].structuralValidation.valid", equalTo(true))
                .body("signatures[0].digestMatchers[0].digestMethod", equalTo(HASH_ALGO_SHA256))
                .body("signatures[0].digestMatchers[0].digestValue", equalTo("7UlS2NYiVo7OhneOHdb6gsTuA1HLM433vrBKSYnI46c="))
                .body("signatures[0].digestMatchers[0].dataFound", equalTo(true))
                .body("signatures[0].digestMatchers[0].dataIntact", equalTo(true))
                .body("signatures[0].digestMatchers[0].type", equalTo("MESSAGE_DIGEST"))
                .body("signatures[0].basicSignature.encryptionAlgoUsedToSignThisToken", equalTo("RSA"))
                .body("signatures[0].basicSignature.keyLengthUsedToSignThisToken", equalTo("2048"))
                .body("signatures[0].basicSignature.digestAlgoUsedToSignThisToken", equalTo(HASH_ALGO_SHA256))
                .body("signatures[0].basicSignature.signatureIntact", equalTo(true))
                .body("signatures[0].basicSignature.signatureValid", equalTo(true))
                .body("signatures[0].signingCertificate.attributePresent", equalTo(true))
                .body("signatures[0].signingCertificate.digestValuePresent", equalTo(true))
                .body("signatures[0].signingCertificate.digestValueMatch", equalTo(true))
                .body("signatures[0].signingCertificate.issuerSerialMatch", equalTo(true))
                .body("signatures[0].signingCertificate.id", notNullValue())
                .body("signatures[0].certificateChain[0].source", equalTo("SIGNATURE"))
                .body("signatures[0].certificateChain[0].id", equalTo("F014C7DF249D8734DF273D937EE5EBF0F8166BE0775C47A80608F1A14EB23F4C"))
                .body("signatures[0].certificateChain[1].source", equalTo("TRUSTED_LIST"))
                .body("signatures[0].certificateChain[1].id", equalTo("74D992D3910BCF7E34B8B5CD28F91EAEB4F41F3DA6394D78B8C43672D43F4F0F"))
                .body("signatures[0].timestamps[0].productionTime", notNullValue())
                .body("signatures[0].timestamps[0].id", equalTo("6D7FCB82BA440E0BFE8E0349C94EB54CAA6714BC68492C66A9279252CA07B33C"))
                .body("signatures[0].timestamps[0].type", equalTo("SIGNATURE_TIMESTAMP"))
                .body("signatures[0].timestamps[0].digestMatcher.digestMethod", equalTo(HASH_ALGO_SHA256))
                .body("signatures[0].timestamps[0].digestMatcher.digestValue", equalTo("7DhYQqR1MqVR3yDAY3HgnjvVjYmQpJPI9Afhqe8usmA="))
                .body("signatures[0].timestamps[0].digestMatcher.dataFound", equalTo(true))
                .body("signatures[0].timestamps[0].digestMatcher.dataIntact", equalTo(true))
                .body("signatures[0].timestamps[0].digestMatcher.type", equalTo("MESSAGE_IMPRINT"))
                .body("signatures[0].timestamps[0].basicSignature.encryptionAlgoUsedToSignThisToken", equalTo("RSA"))
                .body("signatures[0].timestamps[0].basicSignature.keyLengthUsedToSignThisToken", equalTo("2048"))
                .body("signatures[0].timestamps[0].basicSignature.digestAlgoUsedToSignThisToken", equalTo(HASH_ALGO_SHA256))
                .body("signatures[0].timestamps[0].basicSignature.signatureIntact", equalTo(true))
                .body("signatures[0].timestamps[0].basicSignature.signatureValid", equalTo(true))
                .body("signatures[0].timestamps[0].signingCertificate.id", equalTo("1E49F497D89D430AAD534B622D82BD9B9D0D4AFDB7B7D36986C5DF0981D9067D"))
                .body("signatures[0].timestamps[0].certificateChain[0].source", equalTo("TRUSTED_LIST"))
                .body("signatures[0].timestamps[0].certificateChain[0].id", equalTo("1E49F497D89D430AAD534B622D82BD9B9D0D4AFDB7B7D36986C5DF0981D9067D"))
                .body("signatures[0].timestamps[0].timestampedObjects[0].category", equalTo("SIGNATURE"))
                .body("signatures[0].timestamps[0].timestampedObjects[0].id", equalTo("id-1328da20a3b04fdd7f5d1b5b88e4e66009e001ac0e724e93c33e6b5b7401b470"))
                .body("signatures[0].timestamps[0].timestampedObjects[1].digestAlgoAndValue.digestMethod", equalTo(HASH_ALGO_SHA256))
                .body("signatures[0].timestamps[0].timestampedObjects[1].digestAlgoAndValue.digestValue", equalTo("8BTH3ySdhzTfJz2TfuXr8PgWa+B3XEeoBgjxoU6yP0w="))
                .body("signatures[0].timestamps[0].timestampedObjects[1].category", equalTo("CERTIFICATE"))
                .body("signatures[0].signatureScopes[0].value", equalTo("The document byte range: [0, 9136, 28082, 26387]"))
                .body("signatures[0].signatureScopes[0].name", equalTo("PDF previous version #1"))
                .body("signatures[0].signatureScopes[0].scope", equalTo("PARTIAL"));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-5
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Used Certificates
     *
     * Expected Result: Diagnostic report includes used certificates element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Ignore
    @Test //TODO: This test is flaky, we need a mechanism to cover elements changing their order
    public  void diagnosticReportAssertUsedCertificates(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DIAGNOSTIC ))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("usedCertificates", notNullValue())
                .body("usedCertificates.serialNumber", notNullValue())
                .body("usedCertificates[0].subjectDistinguishedName[0].value", equalTo("cn=esteid-sk 2015,2.5.4.97=#0c0e4e545245452d3130373437303133,o=as sertifitseerimiskeskus,c=ee"))
                .body("usedCertificates[0].subjectDistinguishedName[0].format", equalTo("CANONICAL"))
                .body("usedCertificates[0].subjectDistinguishedName[1].value", equalTo("CN=ESTEID-SK 2015,2.5.4.97=#0c0e4e545245452d3130373437303133,O=AS Sertifitseerimiskeskus,C=EE"))
                .body("usedCertificates[0].subjectDistinguishedName[1].format", equalTo("RFC2253"))
                .body("usedCertificates[0].issuerDistinguishedName[0].value", equalTo("1.2.840.113549.1.9.1=#1609706b6940736b2e6565,cn=ee certification centre root ca,o=as sertifitseerimiskeskus,c=ee"))
                .body("usedCertificates[0].issuerDistinguishedName[0].format", equalTo("CANONICAL"))
                .body("usedCertificates[0].issuerDistinguishedName[1].value", equalTo("1.2.840.113549.1.9.1=#1609706b6940736b2e6565,CN=EE Certification Centre Root CA,O=AS Sertifitseerimiskeskus,C=EE"))
                .body("usedCertificates[0].issuerDistinguishedName[1].format", equalTo("RFC2253"));
    }

    /**
     * TestCaseID: Diagnostic-Report-Validation-6
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Wrong signature value
     *
     * Expected Result: Diagnostic report includes wrong signature value
     *
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     */
    @Test
    public  void detailedReportWrongSignatureValueAsice() {
        setTestFilesDirectory("bdoc/live/timestamp/");

        post(validationRequestFor("TS-02_23634_TS_wrong_SignatureValue.asice", null, REPORT_TYPE_DIAGNOSTIC))
                .then().root(DIAGNOSTIC_DATA_PREFIX)
                .body("signatures[0].basicSignature.encryptionAlgoUsedToSignThisToken", equalTo("RSA"))
                .body("signatures[0].basicSignature.keyLengthUsedToSignThisToken", equalTo("2048"))
                .body("signatures[0].basicSignature.digestAlgoUsedToSignThisToken", equalTo(HASH_ALGO_SHA256))
                .body("signatures[0].basicSignature.signatureIntact", equalTo(false))
                .body("signatures[0].basicSignature.signatureValid", equalTo(false));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
