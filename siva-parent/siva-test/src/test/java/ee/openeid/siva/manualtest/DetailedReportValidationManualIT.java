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
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ee.openeid.siva.integrationtest.TestData.*;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Category(IntegrationTest.class)

public class DetailedReportValidationManualIT extends SiVaRestTests {
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
     * TestCaseID: Detailed-Report-Validation-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: ValidationConclusion element
     *
     * Expected Result: Detailed report includes validationConclusion element
     *
     * File: ValidLiveSignature.asice
     */
    @Ignore
    @Test
    public void detailedReportAssertValidValidationConclusionAsicE() {
        String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timestamp/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationConclusion = validationReport + "validationConclusion";
        String filename = "ValidLiveSignature.asice";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        ZonedDateTime testStartDate = ZonedDateTime.now(ZoneId.of("GMT"));
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        assertThat(response.jsonPath().getString(validationConclusion + ".policy.policyDescription"), equalTo(POLICY_4_DESCRIPTION));
        assertThat(response.jsonPath().getString(validationConclusion + ".policy.policyName"), equalTo(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2));
        assertThat(response.jsonPath().getString(validationConclusion + ".policy.policyUrl"), equalTo(POLICY_4_URL));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatureForm"), equalTo(VALID_SIGNATURE_FORM_1));
        assertThat(response.jsonPath().getString(validationConclusion + ".validationTime"), DateTimeMatcher.isEqualOrAfter(testStartDate));
        assertThat(response.jsonPath().getString(validationConclusion + ".signaturesCount"), equalTo("1"));
        assertThat(response.jsonPath().getString(validationConclusion + ".validSignaturesCount"), equalTo("1"));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures"), notNullValue());
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.id[0]"), equalTo("S0"));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.signatureFormat[0]"), equalTo(VALID_VALIDATION_CONCLUSION_SIGNATURE_FORMAT_XADES_LT));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.signatureLevel[0]"), equalTo(VALID_VALIDATION_CONCLUSION_SIGNATURE_LEVEL_1));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.signedBy[0]"), equalTo("NURM,AARE,38211015222"));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.indication[0]"), equalTo(VALID_INDICATION_TOTAL_PASSED));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.signatureScopes[0].name[0]"), equalTo("Tresting.txt"));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.signatureScopes[0].scope[0]"), equalTo(VALID_SIGNATURE_SCOPE_VALUE_1));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.signatureScopes[0].content[0]"), equalTo(VALID_SIGNATURE_SCOPE_CONTENT_1));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.claimedSigningTime[0]"), equalTo("2016-10-11T09:35:48Z"));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatures.info.bestSignatureTime[0]"), equalTo("2016-10-11T09:36:10Z"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: QmatrixBlock element
     *
     * Expected Result: Detailed report includes QmatrixBlock element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Ignore
    @Test
    public  void detailedReportAssertValidationProtsessQmatrixBlock(){
        String DEFAULT_TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationProcess = validationReport + "validationProcess";
        String filename = "pades-baseline-lta-live-aj.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);

        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[0]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_34));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[0]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_34));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[1]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_36));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[1]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_36));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[1]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[2]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_48));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[2]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_47));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[2]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].id[2]"), equalTo("EU"));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[3]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_48));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[3]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_47));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[3]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].id[3]"), equalTo("EE"));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[4]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_37));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[4]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_37));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[4]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[5]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_38));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[5]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_38));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[5]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[6]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_39));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[6]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_39));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[6]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[7]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_40));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[7]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_40));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[7]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].nameId[8]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_41));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint.name[0].value[8]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_41));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.constraint[0].status[8]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.conclusion.indication[0]"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.signatureQualification[0]"), equalTo(VALID_VALIDATION_PROCESS_SIGNATUREQUALIFICATION_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.signatureAnalysis.id"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_1));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_1));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].status[1]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_3));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_3));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].status[2]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[3].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_4));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].name[3].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_4));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.countryCode[0]"), equalTo("EU"));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_1));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_1));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].status[1]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_3));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_3));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].status[2]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[3].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_4));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].name[3].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_4));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.constraint[1].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.conclusion[1].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".qmatrixBlock.tlanalysis.countryCode[1]"), equalTo("EE"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Signatures element
     *
     * Expected Result: Detailed report includes signatures element and its sub-elements and its values
     *
     * File: hellopades-lt-sha256-ec256.pdf
     */
    @Ignore
    @Test
    public  void detailedReportForPdfValidateSignaturesElement() {
        String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signature_cryptographic_algorithm_test_files/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationProcess = validationReport + "validationProcess";
        String filename = "hellopades-lt-sha256-ec256.pdf";
        String request = detailedReportRequest(filename, VALID_SIGNATURE_POLICY_4);
        response = validateRequestForDetailedReport(request, VALIDATION_ENDPOINT);

        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_5));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_5));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures[0].constraint[0].id"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures[0].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures[0].conclusion.errors"), nullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps.constraint"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps.constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_48));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_49));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].id[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion[0].errors"), nullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].id[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].type[0]"), equalTo("SIGNATURE_TIMESTAMP"));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps.constraint"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps.constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[1].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_48));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[1].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_49));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[1].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[1].id[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion[1].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion[1].errors"), nullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].id[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].type[1]"), equalTo("SIGNATURE_TIMESTAMP"));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_6));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_6));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_49));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_50));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].status[1]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].id[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_50));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_51));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].status[2]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[3].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_19));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[3].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_19));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].status[3]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[4].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_51));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[4].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_52));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].status[4]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.conclusion[0].errors"), nullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessArchivalData.constraint"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessArchivalData.constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessArchivalData.constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_7));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessArchivalData.constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_7));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessArchivalData.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessArchivalData.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessArchivalData.conclusion[0].errors"), nullValue());
    }

    /**
     * TestCaseID: Detailed-Report-Validation-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: basicBuildingBlocks element
     *
     * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Ignore
    @Test
    public  void detailedReportForPdfAssertBasicBuildingBlocksTypeTimestamp() {
        String DEFAULT_TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationProcess = validationReport + "validationProcess";
        String filename = "pades-baseline-lta-live-aj.pdf";
        String request = detailedReportRequest(filename, VALID_SIGNATURE_POLICY_4);
        String currentDateLocal = currentDateTime("GMT+3", "yyyy-MM-dd HH:mm");
        response = validateRequestForDetailedReport(request, VALIDATION_ENDPOINT);

        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[0].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[0].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_9));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[0].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_9));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[0].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[0].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_16));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_16));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_17));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_17));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_18));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint.name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_18));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[0].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[0].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[0].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[0].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[0].constraint[0].additionalInfo"), equalTo("Validation time : " + currentDateLocal));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[0].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[0].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_21));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_21));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_24));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_24));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].conclusion"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].subXCV.conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].subXCV.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].subXCV[0].id[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[0].subXCV[0].trustAnchor"), equalTo("true"));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.conclusion[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.conclusion[1].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.id[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.type[0]"), equalTo("TIMESTAMP"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-5
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: BasicBuildingBlocks element
     *
     * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Ignore
    @Test
    public  void detailedReportForPdfAssertBasicBuildingBlocksTypeRevocation() {
        String DEFAULT_TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationProcess = validationReport + "validationProcess";
        String filename = "pades-baseline-lta-live-aj.pdf";
        String request = detailedReportRequest(filename, VALID_SIGNATURE_POLICY_4);
        String currentDateLocal = currentDateTime("GMT+3", "yyyy-MM-dd HH:mm");
        response = validateRequestForDetailedReport(request, VALIDATION_ENDPOINT);

        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[1].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_9));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[1].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_9));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[1].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[1].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_16));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_16));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_17));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_17));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_18));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint.name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_18));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[1].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[1].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[1].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[1].constraint[0].additionalInfo"), equalTo("Validation time : " + currentDateLocal));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[1].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[1].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_21));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_21));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_22));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_22));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.additionalInfo[1]"), equalTo("Type : " + OCSP_QC));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_23));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_23));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.additionalInfo[2]"), equalTo("Status : " + GRANTED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[3].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_24));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint.name[3].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_24));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].conclusion"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].subXCV.conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].subXCV.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].subXCV[0].id[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[1].subXCV[0].trustAnchor"), equalTo("true"));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.conclusion[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.conclusion[1].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.id[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.type[1]"), equalTo("REVOCATION"));
    }


    /**
     * TestCaseID: Detailed-Report-Validation-6
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: BasicBuildingBlocks element
     *
     * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Ignore
    @Test
    public  void detailedReportForPdfAssertBasicBuildingBlocksTypeSignature() {
        String DEFAULT_TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationProcess = validationReport + "validationProcess";
        String filename = "pades-baseline-lta-live-aj.pdf";
        String request = detailedReportRequest(filename, VALID_SIGNATURE_POLICY_4);
        String currentDateLocal = currentDateTime("GMT+3", "yyyy-MM-dd HH:mm");
        response = validateRequestForDetailedReport(request, VALIDATION_ENDPOINT);

        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.fc.constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.fc.constraint.name[0].value[0]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_8));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.fc.constraint.name[0].nameId[0]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_8));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.fc.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.fc.conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.fc.conclusion.indication[0]"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_9));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_9));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_10));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_10));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_11));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_11));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[3].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_12));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[3].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_12));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[4].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_13));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[4].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_13));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[5].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_14));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint.name[5].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_14));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.isc[3].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.vci.constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.vci.constraint.name[0].value[0]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_15));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.vci.constraint.name[0].nameId[0]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_15));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.vci.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.vci.conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.vci.conclusion.indication[0]"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_16));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_16));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_17));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_17));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_18));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint.name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_18));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.cv[3].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint.additionalInfo[1]"), equalTo("Validation time : " + currentDateLocal));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_19));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_19));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].conclusion."), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.sav[3].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint.name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_21));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint.name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_21));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint.name"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint.name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_24));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint.name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_24));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].conclusion"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].conclusion.indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_52));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_53));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_26));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_26));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].status[1]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[2]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_28));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_28));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].status[2]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[3]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[3].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_29));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[3].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_29));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].status[3]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[4]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[4].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_30));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[4].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_30));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].status[4]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[5]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[5].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_31));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[5].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_31));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].status[5]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[6]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[6].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_32));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].name[6].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_43));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.constraint[0].status[6]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.conclusion[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_33));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_33));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[1]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_44));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_45));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].status[1]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_3));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].warning[1].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_45));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].warning[1].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_46));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[2]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[2].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_46));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[2].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_47));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].status[2]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].additionalInfo[2]"), equalTo("Next update : not defined"));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[3]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[3].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].name[3].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_20));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].status[3]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_2));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.constraint[0].additionalInfo[3]"), equalTo("Validation time : " + currentDateLocal));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.conclusion"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.rfc.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.id[0]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.xcv[3].subXCV.trustAnchor[0]"), equalTo("false"));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.conclusion[3]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.conclusion[3].indication"), equalTo(VALID_INDICATION_VALUE_PASSED));
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.id[3]"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".basicBuildingBlocks.type[3]"), equalTo("SIGNATURE"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-7
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Wrong signature value
     *
     * Expected Result: Detailed report includes wrong signature value
     *
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     */
    @Ignore
    @Test
    public  void detailedReportWrongSignatureValueAsice() {
        String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timestamp/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationProcess = validationReport + "validationProcess";
        String filename = "TS-02_23634_TS_wrong_SignatureValue.asice";
        String request = detailedReportRequest(filename, VALID_SIGNATURE_POLICY_4);
        response = validateRequestForDetailedReport(request, VALIDATION_ENDPOINT);

        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint.name[0].nameId[0]"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_5));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint.name[0].value[0]"), equalTo(VALID_VALIDATION_PROCESS_VALUE_5));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_1));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint.error[0].value[0]"), equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_1));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint.error[0].nameId[0]"), equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_1));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.constraint[0].id[0]"), equalTo("S0"));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_FAILED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.conclusion[0].subIndication"), equalTo(VALID_VALIDATION_PROCESS_SUB_INDICATION_3));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.conclusion.errors[0].value[0]"), equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_9));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessBasicSignatures.conclusion.errors[0].nameId[0]"), equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_8));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_49));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_48));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].status[0]"), equalTo(VALID_VALIDATION_PROCESS_STATUS_1));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].error[0].value"), equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_10));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].error[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_9));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].constraint[0].id"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_FAILED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion[0].subIndication"), equalTo(VALID_VALIDATION_PROCESS_SUB_INDICATION_4));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion.errors[0].value[0]"), equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_10));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].conclusion.errors[0].nameId[0]"), equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_9));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].id"), notNullValue());
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessTimestamps[0].type[0]"), equalTo("SIGNATURE_TIMESTAMP"));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[0].value"), equalTo(VALID_VALIDATION_PROCESS_VALUE_6));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].name[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_NAMEID_6));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData[0].constraint[0].status"), equalTo(VALID_VALIDATION_PROCESS_STATUS_1));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].error[0].value"), equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_3));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.constraint[0].error[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_3));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.conclusion[0].indication"), equalTo(VALID_INDICATION_VALUE_FAILED));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.conclusion[0].subIndication"), equalTo(VALID_VALIDATION_PROCESS_SUB_INDICATION_3));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.conclusion[0].errors[0].nameId"), equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_8));
        assertThat(response.jsonPath().getString(validationProcess + ".signatures.validationProcessLongTermData.conclusion[0].errors[0].value"), equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_9));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-8
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Wrong data file in manifest
     *
     * Expected Result:
     *
     * File: WrongDataFileInManifestAsics.asics
     */
    @Ignore
    @Test
    public  void detailedReportForAsicsWrongDataFileInManifestAsics() {
        String DEFAULT_TEST_FILES_DIRECTORY = "asics/";
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        String validationReport = "validationReport.";
        String validationConclusion = validationReport + "validationConclusion";
        String filename = "WrongDataFileInManifestAsics.asics";
        String request = detailedReportRequest(filename, VALID_SIGNATURE_POLICY_4);
        ZonedDateTime testStartDate = ZonedDateTime.now(ZoneId.of("GMT"));
        response = validateRequestForDetailedReport(request, VALIDATION_ENDPOINT);
        assertThat(response.jsonPath().getString(validationConclusion + ".policy.policyDescription"), equalTo(POLICY_4_DESCRIPTION));
        assertThat(response.jsonPath().getString(validationConclusion + ".policy.policyName"), equalTo(VALID_VALIDATION_CONCLUSION_SIGNATURE_POLICY_2));
        assertThat(response.jsonPath().getString(validationConclusion + ".policy.policyUrl"), equalTo(POLICY_4_URL));
        assertThat(response.jsonPath().getString(validationConclusion + ".signatureForm"), equalTo(VALID_SIGNATURE_FORM_2));
        assertThat(response.jsonPath().getString(validationConclusion + ".validationTime"), DateTimeMatcher.isEqualOrAfter(testStartDate));
        assertThat(response.jsonPath().getString(validationConclusion + ".signaturesCount"), equalTo("1"));
        assertThat(response.jsonPath().getString(validationConclusion + ".validSignaturesCount"), equalTo("1"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-9
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report file hash if ReportSignatureEnabled value true
     *
     * Expected Result: fileHashInHex calculated
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateFileHashInDetailedReportReportSignatureEnabledTrue() {
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        assertThat(response.jsonPath().getString("validationReport.validationConclusion.validatedDocument.filename"), equalTo("hellopades-lt-sha256-rsa2048.pdf"));
        assertThat(response.jsonPath().getString("validationReport.validationConclusion.validatedDocument.fileHashInHex"), notNullValue() );
        assertThat(response.jsonPath().getString("validationReport.validationConclusion.validatedDocument.hashAlgo"), equalTo("SHA-256"));
        assertThat(response.jsonPath().getString("validationReportSignature"), notNullValue());
    }

    /**
     * TestCaseID: Detailed-Report-Validation-10
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report file hash if ReportSignatureEnabled value false
     *
     * Expected Result: fileHashInHex no calculated
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore
    @Test
    public void validateFileHashInDetailedReportReportSignatureEnabledFalse() {
        String filename = "hellopades-lt-sha256-rsa2048.pdf";
        String request = detailedReportRequest(filename,VALID_SIGNATURE_POLICY_4);
        response =  validateRequestForDetailedReport(request,VALIDATION_ENDPOINT);
        assertThat(response.jsonPath().getString("validationReport.validationConclusion.validatedDocument.filename"), equalTo(filename));
        assertThat(response.jsonPath().getString("validationReport.validationConclusion.validatedDocument.fileHashInHex"), nullValue() );
        assertThat(response.jsonPath().getString("validationReport.validationConclusion.validatedDocument.hashAlgo"), nullValue());
    }

    private Response validateRequestForDetailedReport(String request, String validationUrl){
        return RestAssured.given()
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

    private String detailedReportRequest(String fileName, String signaturePolicy) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(fileName)));
        jsonObject.put("filename", fileName);
        jsonObject.put("signaturePolicy", signaturePolicy);
        jsonObject.put("reportType", "Detailed");
        return  jsonObject.toString();
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
