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
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: ValidationConclusion element
     *
     * Expected Result: Detailed report includes validationConclusion element
     *
     * File: ValidLiveSignature.asice
     */
    @Test
    public void detailedReportAssertValidValidationConclusionAsicE() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        ZonedDateTime testStartDate = ZonedDateTime.now(ZoneId.of("GMT"));

        post(validationRequestFor("ValidLiveSignature.asice", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("policy.policyDescription", equalTo(POLICY_4_DESCRIPTION))
                .body("policy.policyName", equalTo(SIGNATURE_POLICY_2))
                .body("policy.policyUrl", equalTo(POLICY_4_URL))
                .body("signatureForm", equalTo(SIGNATURE_FORM_ASICE))
                .body("validationTime", DateTimeMatcher.isEqualOrAfter(testStartDate))
                .body("signaturesCount", equalTo(1))
                .body("validSignaturesCount", equalTo(1))
                .body("signatures", notNullValue())
                .body("signatures.id[0]", equalTo("S0"))
                .body("signatures.signatureFormat[0]", equalTo(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures.signatureLevel[0]", equalTo(SIGNATURE_LEVEL_QESIG))
                .body("signatures.signedBy[0]", equalTo("NURM,AARE,38211015222"))
                .body("signatures.indication[0]", equalTo(TOTAL_PASSED))
                .body("signatures.signatureScopes[0].name[0]", equalTo("Tresting.txt"))
                .body("signatures.signatureScopes[0].scope[0]", equalTo(SIGNATURE_SCOPE_FULL))
                .body("signatures.signatureScopes[0].content[0]", equalTo(VALID_SIGNATURE_SCOPE_CONTENT_FULL))
                .body("signatures.claimedSigningTime[0]", equalTo("2016-10-11T09:35:48Z"))
                .body("signatures.info.bestSignatureTime[0]", equalTo("2016-10-11T09:36:10Z"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: TL analysis element
     *
     * Expected Result: Detailed report includes tlanalysis element and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    @Ignore("SIVA-119")
    public void detailedReportAssertValidationProcessTlanalysis() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_PROCESS_PREFIX)
                .body("tlanalysis", notNullValue())
                .body("tlanalysis.constraint", notNullValue())
                .body("tlanalysis[0]", notNullValue())
                .body("tlanalysis[0].constraint[0]", notNullValue())
                .body("tlanalysis[0].constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_1))
                .body("tlanalysis[0].constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_1))
                .body("tlanalysis[0].constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[0]", notNullValue())
                .body("tlanalysis[0].constraint[1]", notNullValue())
                .body("tlanalysis[0].constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_2))
                .body("tlanalysis[0].constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_2))
                .body("tlanalysis[0].constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[0].constraint[2]", notNullValue())
                .body("tlanalysis[0].constraint[2].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_3))
                .body("tlanalysis[0].constraint[2].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_3))
                .body("tlanalysis[0].constraint[2].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[0].constraint[3]", notNullValue())
                .body("tlanalysis[0].constraint[3].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_4))
                .body("tlanalysis[0].constraint[3].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_4))
                .body("tlanalysis[0].constraint[3].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[0].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("tlanalysis[0].countryCode", equalTo("EU"))
                .body("tlanalysis[1]", notNullValue())
                .body("tlanalysis[1].constraint[0]", notNullValue())
                .body("tlanalysis[1].constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_1))
                .body("tlanalysis[1].constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_1))
                .body("tlanalysis[1].constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[1]", notNullValue())
                .body("tlanalysis[1].constraint[1]", notNullValue())
                .body("tlanalysis[1].constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_2))
                .body("tlanalysis[1].constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_2))
                .body("tlanalysis[1].constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[1]", notNullValue())
                .body("tlanalysis[1].constraint[2]", notNullValue())
                .body("tlanalysis[1].constraint[2].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_3))
                .body("tlanalysis[1].constraint[2].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_3))
                .body("tlanalysis[1].constraint[2].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[1]", notNullValue())
                .body("tlanalysis[1].constraint[3]", notNullValue())
                .body("tlanalysis[1].constraint[3].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_4))
                .body("tlanalysis[1].constraint[3].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_4))
                .body("tlanalysis[1].constraint[3].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("tlanalysis[1].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("tlanalysis[1].countryCode", equalTo("EE"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Signatures element
     *
     * Expected Result: Detailed report includes signatures element and its sub-elements and its values
     *
     * File: hellopades-lt-sha256-ec256.pdf
     */
    @Test //TODO: This test misses validationSignatureQualification block
    public void detailedReportForPdfValidateSignaturesElement() {
        setTestFilesDirectory("pdf/signature_cryptographic_algorithm_test_files/");

        post(validationRequestFor("hellopades-lt-sha256-ec256.pdf", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_PROCESS_PREFIX)
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_5))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_5))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Id", notNullValue())
                .body("Signatures[0].ValidationProcessBasicSignatures.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("Signatures[0].ValidationProcessBasicSignatures.Conclusion.errors", nullValue())
                .body("Signatures[0].ValidationProcessTimestamps.Constraint", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps.Constraint.Name", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_48))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_49))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Id", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps[0].Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("Signatures[0].ValidationProcessTimestamps[0].Conclusion.Errors", nullValue())
                .body("Signatures[0].ValidationProcessTimestamps[0].Id", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps[0].Type", equalTo("SIGNATURE_TIMESTAMP"))
                .body("Signatures[0].ValidationProcessTimestamps[1].Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_48))
                .body("Signatures[0].ValidationProcessTimestamps[1].Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_49))
                .body("Signatures[0].ValidationProcessTimestamps[1].Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessTimestamps[1].Constraint[0].Id", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps[1].Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("Signatures[0].ValidationProcessTimestamps[1].Conclusion.Errors", nullValue())
                .body("Signatures[0].ValidationProcessTimestamps[1].Id", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps[1].Type", equalTo("SIGNATURE_TIMESTAMP"))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0]", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Name", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_6))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_6))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[1]", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[1].Name", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[1].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_49))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[1].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_50))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[1].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[1].Id", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[2]", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[2].Name", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[2].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_50))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[2].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_51))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[2].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[3]", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[3].Name", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[3].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_19))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[3].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_19))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[3].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[4]", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[4].Name", notNullValue())
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[4].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_51))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[4].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_52))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[4].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_4))
                .body("Signatures[0].ValidationProcessLongTermData.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("Signatures[0].ValidationProcessLongTermData.Conclusion.Errors", nullValue())
                .body("Signatures[0].ValidationProcessArchivalData.Constraint[0]", notNullValue())
                .body("Signatures[0].ValidationProcessArchivalData.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_7))
                .body("Signatures[0].ValidationProcessArchivalData.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_7))
                .body("Signatures[0].ValidationProcessArchivalData.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("Signatures[0].ValidationProcessArchivalData.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("Signatures[0].ValidationProcessArchivalData.Conclusion.Errors", nullValue());
    }

    /**
     * TestCaseID: Detailed-Report-Validation-4
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: basicBuildingBlocks element
     *
     * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    @Ignore("SIVA-187 - refactoring needed")
    public void detailedReportForPdfAssertBasicBuildingBlocksTypeTimestamp() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_PROCESS_PREFIX)
                .body("basicBuildingBlocks[0].isc.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_9))
                .body("basicBuildingBlocks[0].isc.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_9))
                .body("basicBuildingBlocks[0].isc.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].isc.conclusion.", notNullValue())
                .body("basicBuildingBlocks[0].isc.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].isc.certificateChain.chainItem[0].source", equalTo("TRUSTED_LIST"))
                .body("basicBuildingBlocks[0].isc.certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[0].cv.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[0].cv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_16))
                .body("basicBuildingBlocks[0].cv.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_16))
                .body("basicBuildingBlocks[0].cv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].cv.constraint[1].name", notNullValue())
                .body("basicBuildingBlocks[0].cv.constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_17))
                .body("basicBuildingBlocks[0].cv.constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_17))
                .body("basicBuildingBlocks[0].cv.constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].cv.constraint[2].name", notNullValue())
                .body("basicBuildingBlocks[0].cv.constraint[2].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_18))
                .body("basicBuildingBlocks[0].cv.constraint[2].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_18))
                .body("basicBuildingBlocks[0].cv.constraint[2].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].cv.conclusion.", notNullValue())
                .body("basicBuildingBlocks[0].cv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].sav.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[0].sav.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_20))
                .body("basicBuildingBlocks[0].sav.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_20))
                .body("basicBuildingBlocks[0].sav.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].sav.constraint[0].additionalInfo", notNullValue())
                .body("basicBuildingBlocks[0].sav.conclusion.", notNullValue())
                .body("basicBuildingBlocks[0].sav.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].xcv.constraint.name", notNullValue())
                .body("basicBuildingBlocks[0].xcv.constraint.name[0].value", equalTo(VALID_VALIDATION_PROCESS_VALUE_21))
                .body("basicBuildingBlocks[0].xcv.constraint.name[0].nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_21))
                .body("basicBuildingBlocks[0].xcv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].xcv.constraint.name", notNullValue())
                .body("basicBuildingBlocks[0].xcv.constraint.name[1].value", equalTo(VALID_VALIDATION_PROCESS_VALUE_24))
                .body("basicBuildingBlocks[0].xcv.constraint.name[1].nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_24))
                .body("basicBuildingBlocks[0].xcv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].xcv.conclusion", notNullValue())
                .body("basicBuildingBlocks[0].xcv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].xcv.subXCV[0].conclusion", notNullValue())
                .body("basicBuildingBlocks[0].xcv.subXCV[0].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].xcv.subXCV[0].id", notNullValue())
                .body("basicBuildingBlocks[0].xcv.subXCV[0].trustAnchor", equalTo(true))
                .body("basicBuildingBlocks[0].certificateChain.chainItem[0].source", equalTo("TRUSTED_LIST"))
                .body("basicBuildingBlocks[0].certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[0].conclusion", notNullValue())
                .body("basicBuildingBlocks[0].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].id", notNullValue())
                .body("basicBuildingBlocks[0].type", equalTo("TIMESTAMP"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-5
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: BasicBuildingBlocks element
     *
     * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Test
    @Ignore("SIVA-187 - refactoring needed")
    public void detailedReportForPdfAssertBasicBuildingBlocksTypeRevocation() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_PROCESS_PREFIX)
                .body("BasicBuildingBlocks[1].ISC.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_9))
                .body("BasicBuildingBlocks[1].ISC.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_9))
                .body("BasicBuildingBlocks[1].ISC.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("BasicBuildingBlocks[1].ISC.Conclusion.", notNullValue())
                .body("BasicBuildingBlocks[1].ISC.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("BasicBuildingBlocks[1].ISC.CertificateChain.ChainItem[0].Source", equalTo("TRUSTED_LIST"))
                .body("BasicBuildingBlocks[1].ISC.CertificateChain.ChainItem[0].Id", notNullValue())
                .body("BasicBuildingBlocks[1].CV.Constraint[0].Name", notNullValue())
                .body("BasicBuildingBlocks[1].CV.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_18))
                .body("BasicBuildingBlocks[1].CV.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_18))
                .body("BasicBuildingBlocks[1].CV.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("BasicBuildingBlocks[1].CV.Conclusion.", notNullValue())
                .body("BasicBuildingBlocks[1].CV.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("BasicBuildingBlocks[1].SAV.Constraint[0].Name", notNullValue())
                .body("BasicBuildingBlocks[1].SAV.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_20))
                .body("BasicBuildingBlocks[1].SAV.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_20))
                .body("BasicBuildingBlocks[1].SAV.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("BasicBuildingBlocks[1].SAV.Constraint[0].AdditionalInfo", notNullValue())
                .body("BasicBuildingBlocks[1].SAV.Conclusion.", notNullValue())
                .body("BasicBuildingBlocks[1].SAV.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("BasicBuildingBlocks[1].XCV.Constraint[0]", notNullValue())
                .body("BasicBuildingBlocks[1].XCV.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_21))
                .body("BasicBuildingBlocks[1].XCV.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_21))
                .body("BasicBuildingBlocks[1].XCV.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("BasicBuildingBlocks[1].XCV.Constraint[1].Name", notNullValue())
                .body("BasicBuildingBlocks[1].XCV.Constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_24))
                .body("BasicBuildingBlocks[1].XCV.Constraint[1].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_24))
                .body("BasicBuildingBlocks[1].XCV.Constraint[1].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("BasicBuildingBlocks[1].XCV.Conclusion", notNullValue())
                .body("BasicBuildingBlocks[1].XCV.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("BasicBuildingBlocks[1].XCV.SubXCV[0].Conclusion", notNullValue())
                .body("BasicBuildingBlocks[1].XCV.SubXCV[0].Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("BasicBuildingBlocks[1].XCV.SubXCV[0].Id", notNullValue())
                .body("BasicBuildingBlocks[1].XCV.SubXCV[0].TrustAnchor", equalTo(true))
                .body("BasicBuildingBlocks[1].CertificateChain.ChainItem[0].Source", equalTo("TRUSTED_LIST"))
                .body("BasicBuildingBlocks[1].CertificateChain.ChainItem[0].Id", notNullValue())
                .body("BasicBuildingBlocks[1].Conclusion", notNullValue())
                .body("BasicBuildingBlocks[1].Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("BasicBuildingBlocks[1].Id", notNullValue())
                .body("BasicBuildingBlocks[1].Type", equalTo("REVOCATION"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-6
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: BasicBuildingBlocks element
     *
     * Expected Result: Detailed report includes basicBuildingBlocks element and its sub-elements and its values
     *
     * File: pades-baseline-lta-live-aj.pdf
     */
    @Ignore //TODO: The sequence of blocks changes
    @Test
    public void detailedReportForPdfAssertBasicBuildingBlocksTypeSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_PROCESS_PREFIX)
                .body("basicBuildingBlocks[3].isc.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_9))
                .body("basicBuildingBlocks[3].isc.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_9))
                .body("basicBuildingBlocks[3].isc.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].isc.conclusion.", notNullValue())
                .body("basicBuildingBlocks[3].isc.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].isc.certificateChain.chainItem[0].source", equalTo("TRUSTED_LIST"))
                .body("basicBuildingBlocks[3].isc.certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[3].cv.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[3].cv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_18))
                .body("basicBuildingBlocks[3].cv.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_18))
                .body("basicBuildingBlocks[3].cv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].cv.conclusion.", notNullValue())
                .body("basicBuildingBlocks[3].cv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].sav.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[3].sav.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_20))
                .body("basicBuildingBlocks[3].sav.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_20))
                .body("basicBuildingBlocks[3].sav.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].sav.constraint[0].additionalInfo", notNullValue())
                .body("basicBuildingBlocks[3].sav.conclusion.", notNullValue())
                .body("basicBuildingBlocks[3].sav.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].xcv.constraint[0]", notNullValue())
                .body("basicBuildingBlocks[3].xcv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_21))
                .body("basicBuildingBlocks[3].xcv.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_21))
                .body("basicBuildingBlocks[3].xcv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].xcv.constraint[3].name", notNullValue())
                .body("basicBuildingBlocks[3].xcv.constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_24))
                .body("basicBuildingBlocks[3].xcv.constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_24))
                .body("basicBuildingBlocks[3].xcv.constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].xcv.conclusion", notNullValue())
                .body("basicBuildingBlocks[3].xcv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].xcv.subXCV[0].conclusion", notNullValue())
                .body("basicBuildingBlocks[3].xcv.subXCV[0].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].xcv.subXCV[0].id", notNullValue())
                .body("basicBuildingBlocks[3].xcv.subXCV[0].trustAnchor", equalTo(true))
                .body("basicBuildingBlocks[3].certificateChain.chainItem[0].source", equalTo("TRUSTED_LIST"))
                .body("basicBuildingBlocks[3].certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[3].conclusion", notNullValue())
                .body("basicBuildingBlocks[3].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].id", notNullValue())
                .body("basicBuildingBlocks[3].type", equalTo("REVOCATION"));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-7
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Wrong signature value
     *
     * Expected Result: Detailed report includes wrong signature value
     *
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     */
    @Test
    public void detailedReportWrongSignatureValueAsice() {
        setTestFilesDirectory("bdoc/live/timestamp/");

        post(validationRequestFor("TS-02_23634_TS_wrong_SignatureValue.asice", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_PROCESS_PREFIX)
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_5))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_5))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_1))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Error.value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_1))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Error.NameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_1))
                .body("Signatures[0].ValidationProcessBasicSignatures.Constraint[0].Id", equalTo("S-226BFC9D7B7C8DA856B070F96B9798F2F675AC6585FD82E23197D2B42E17F24D"))
                .body("Signatures[0].ValidationProcessBasicSignatures.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_FAILED))
                .body("Signatures[0].ValidationProcessBasicSignatures.Conclusion.SubIndication", equalTo(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("Signatures[0].ValidationProcessBasicSignatures.Conclusion.Errors[0].value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_9))
                .body("Signatures[0].ValidationProcessBasicSignatures.Conclusion.Errors[0].NameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_8))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_49))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_48))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_1))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Error.value", equalTo(TS_PROCESS_NOT_CONCLUSIVE))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Error.NameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_9))
                .body("Signatures[0].ValidationProcessTimestamps[0].Constraint[0].Id", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps[0].Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_FAILED))
                .body("Signatures[0].ValidationProcessTimestamps[0].Conclusion.SubIndication", equalTo(SUB_INDICATION_HASH_FAILURE))
                .body("Signatures[0].ValidationProcessTimestamps[0].Conclusion.Errors[0].value", equalTo(TS_PROCESS_NOT_CONCLUSIVE))
                .body("Signatures[0].ValidationProcessTimestamps[0].Conclusion.Errors[0].NameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_9))
                .body("Signatures[0].ValidationProcessTimestamps[0].Id", notNullValue())
                .body("Signatures[0].ValidationProcessTimestamps[0].Type", equalTo("SIGNATURE_TIMESTAMP"))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_6))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Name.NameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_6))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Status", equalTo(VALID_VALIDATION_PROCESS_STATUS_1))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Error.value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_3))
                .body("Signatures[0].ValidationProcessLongTermData.Constraint[0].Error.NameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_3))
                .body("Signatures[0].ValidationProcessLongTermData.Conclusion.Indication", equalTo(VALID_INDICATION_VALUE_FAILED))
                .body("Signatures[0].ValidationProcessLongTermData.Conclusion.SubIndication", equalTo(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("Signatures[0].ValidationProcessLongTermData.Conclusion.Errors[0].NameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_8))
                .body("Signatures[0].ValidationProcessLongTermData.Conclusion.Errors[0].value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_9));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-8
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Wrong data file in manifest
     *
     * Expected Result:
     *
     * File: WrongDataFileInManifestAsics.asics
     */
    @Test
    public void detailedReportForAsicsWrongDataFileInManifestAsics() {
        setTestFilesDirectory("asics/");
        post(validationRequestFor("WrongDataFileInManifestAsics.asics", null, REPORT_TYPE_DETAILED))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("policy.policyDescription", equalTo(POLICY_4_DESCRIPTION))
                .body("policy.policyName", equalTo(SIGNATURE_POLICY_2))
                .body("policy.policyUrl", equalTo(POLICY_4_URL))
                .body("signatureForm", equalTo(SIGNATURE_FORM_ASICS))
                .body("signaturesCount", equalTo(1))
                .body("validSignaturesCount", equalTo(1));
    }

    /**
     * TestCaseID: Detailed-Report-Validation-9
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report file hash if ReportSignatureEnabled value true
     *
     * Expected Result: fileHash calculated
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Test
    @Ignore("SIVA-196")
    public void validateFileHashInDetailedReportReportSignatureEnabledTrue() {
        post(validationRequestFor("hellopades-lt-sha256-rsa2048.pdf", null, REPORT_TYPE_DETAILED))
                .then()
                .body("validationReport.validationConclusion.validatedDocument.filename", equalTo("hellopades-lt-sha256-rsa2048.pdf"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", notNullValue())
                .body("validationReport.validationConclusion.validatedDocument.hashAlgo", equalTo("SHA256"))
                .body("validationReportSignature", notNullValue());
    }

    /**
     * TestCaseID: Detailed-Report-Validation-10
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Validate detailed report file hash if ReportSignatureEnabled value false
     *
     * Expected Result: fileHash no calculated
     *
     * File: hellopades-lt-sha256-rsa2048.pdf
     */
    @Ignore //TODO: Needs possibility to configure report signing in tests
    @Test
    public void validateFileHashInDetailedReportReportSignatureEnabledFalse() {
        post(validationRequestFor("hellopades-lt-sha256-rsa2048.pdf", null, REPORT_TYPE_DETAILED))
                .then()
                .body("validationReport.validationConclusion.validatedDocument.filename", equalTo("hellopades-lt-sha256-rsa2048.pdf"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", nullValue())
                .body("validationReport.validationConclusion.validatedDocument.hashAlgo", nullValue())
                .body("validationReportSignature", nullValue());
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
