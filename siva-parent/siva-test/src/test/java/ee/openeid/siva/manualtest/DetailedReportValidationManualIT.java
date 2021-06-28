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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ee.openeid.siva.integrationtest.TestData.*;
import static org.hamcrest.Matchers.*;

@Category(IntegrationTest.class)

public class DetailedReportValidationManualIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signature_cryptographic_algorithm_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
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
    @Ignore //TODO: New test LOTL is needed with correct data
    public void detailedReportAssertValidationProcessTlanalysis() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().rootPath(VALIDATION_PROCESS_PREFIX)
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
                .then().rootPath(VALIDATION_PROCESS_PREFIX)
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_5))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_5))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].id", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.errors", nullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp.validationProcessTimestamps.constraint", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp.validationProcessTimestamps.constraint.name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_48))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_49))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].id", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.conclusion.errors", nullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].id", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.type", equalTo("SIGNATURE_TIMESTAMP"))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_6))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_6))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[1]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[1].name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_49))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_50))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[1].id", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[2]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[2].name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[2].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_57))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[2].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_56))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[2].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[3]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[3].name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[3].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_58))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[3].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_57))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[3].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[4]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[4].name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[4].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_50))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[4].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_51))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[4].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[5]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[5].name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[5].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_19))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[5].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_19))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[5].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[6]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[6].name", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[6].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_51))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[6].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_52))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[6].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_4))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.errors", nullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessArchivalData.constraint[0]", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].validationProcessArchivalData.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_7))
                .body("signatureOrTimestampOrCertificate[0].validationProcessArchivalData.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_7))
                .body("signatureOrTimestampOrCertificate[0].validationProcessArchivalData.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("signatureOrTimestampOrCertificate[0].validationProcessArchivalData.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("signatureOrTimestampOrCertificate[0].validationProcessArchivalData.conclusion.errors", nullValue());
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
    public void detailedReportForPdfAssertBasicBuildingBlocksTypeTimestamp() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().rootPath(VALIDATION_PROCESS_PREFIX)
                .body("basicBuildingBlocks[1].isc.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_9))
                .body("basicBuildingBlocks[1].isc.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_9))
                .body("basicBuildingBlocks[1].isc.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[1].isc.conclusion.", notNullValue())
                .body("basicBuildingBlocks[1].isc.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[1].isc.certificateChain.chainItem[0].source", equalTo("TRUSTED_LIST"))
                .body("basicBuildingBlocks[1].isc.certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[1].cv.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[1].cv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_16))
                .body("basicBuildingBlocks[1].cv.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_16))
                .body("basicBuildingBlocks[1].cv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[1].cv.constraint[1].name", notNullValue())
                .body("basicBuildingBlocks[1].cv.constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_17))
                .body("basicBuildingBlocks[1].cv.constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_17))
                .body("basicBuildingBlocks[1].cv.constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[1].cv.constraint[2].name", notNullValue())
                .body("basicBuildingBlocks[1].cv.constraint[2].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_55))
                .body("basicBuildingBlocks[1].cv.constraint[2].name.nameId", startsWith(VALID_VALIDATION_PROCESS_NAMEID_18))
                .body("basicBuildingBlocks[1].cv.constraint[2].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[1].cv.conclusion.", notNullValue())
                .body("basicBuildingBlocks[1].cv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[1].sav.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[1].sav.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_53))
                .body("basicBuildingBlocks[1].sav.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_54))
                .body("basicBuildingBlocks[1].sav.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[1].sav.constraint[0].additionalInfo", notNullValue())
                .body("basicBuildingBlocks[1].sav.conclusion.", notNullValue())
                .body("basicBuildingBlocks[1].sav.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[1].xcv.constraint.name", notNullValue())
                .body("basicBuildingBlocks[1].xcv.constraint.name[0].value", equalTo(VALID_VALIDATION_PROCESS_VALUE_21))
                .body("basicBuildingBlocks[1].xcv.constraint.name[0].nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_21))
                .body("basicBuildingBlocks[1].xcv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[1].xcv.constraint.name", notNullValue())
                .body("basicBuildingBlocks[1].xcv.constraint.name[1].value", equalTo(VALID_VALIDATION_PROCESS_VALUE_24))
                .body("basicBuildingBlocks[1].xcv.constraint.name[1].nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_24))
                .body("basicBuildingBlocks[1].xcv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[1].xcv.conclusion", notNullValue())
                .body("basicBuildingBlocks[1].xcv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[1].xcv.subXCV[0].conclusion", notNullValue())
                .body("basicBuildingBlocks[1].xcv.subXCV[0].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[1].xcv.subXCV[0].id", notNullValue())
                .body("basicBuildingBlocks[1].xcv.subXCV[0].trustAnchor", equalTo(true))
                .body("basicBuildingBlocks[1].certificateChain.chainItem[0].source", equalTo("TRUSTED_LIST"))
                .body("basicBuildingBlocks[1].certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[1].conclusion", notNullValue())
                .body("basicBuildingBlocks[1].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[1].id", notNullValue())
                .body("basicBuildingBlocks[1].type", equalTo("TIMESTAMP"));
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
    public void detailedReportForPdfAssertBasicBuildingBlocksTypeRevocation() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().rootPath(VALIDATION_PROCESS_PREFIX)
                .body("basicBuildingBlocks[0].isc.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_9))
                .body("basicBuildingBlocks[0].isc.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_9))
                .body("basicBuildingBlocks[0].isc.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].isc.conclusion.", notNullValue())
                .body("basicBuildingBlocks[0].isc.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].isc.certificateChain.chainItem[0].source", equalTo("TRUSTED_LIST"))
                .body("basicBuildingBlocks[0].isc.certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[0].cv.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[0].cv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_56))
                .body("basicBuildingBlocks[0].cv.constraint[0].name.nameId", startsWith(VALID_VALIDATION_PROCESS_NAMEID_18))
                .body("basicBuildingBlocks[0].cv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].cv.conclusion.", notNullValue())
                .body("basicBuildingBlocks[0].cv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].sav.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[0].sav.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_54))
                .body("basicBuildingBlocks[0].sav.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_55))
                .body("basicBuildingBlocks[0].sav.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].sav.constraint[0].additionalInfo", notNullValue())
                .body("basicBuildingBlocks[0].sav.conclusion.", notNullValue())
                .body("basicBuildingBlocks[0].sav.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[0].xcv.constraint[0]", notNullValue())
                .body("basicBuildingBlocks[0].xcv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_21))
                .body("basicBuildingBlocks[0].xcv.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_21))
                .body("basicBuildingBlocks[0].xcv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[0].xcv.constraint[1].name", notNullValue())
                .body("basicBuildingBlocks[0].xcv.constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_24))
                .body("basicBuildingBlocks[0].xcv.constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_24))
                .body("basicBuildingBlocks[0].xcv.constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
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
                .body("basicBuildingBlocks[0].type", equalTo("REVOCATION"));
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
    @Test
    public void detailedReportForPdfAssertBasicBuildingBlocksTypeSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");

        post(validationRequestFor("pades-baseline-lta-live-aj.pdf", null, REPORT_TYPE_DETAILED))
                .then().rootPath(VALIDATION_PROCESS_PREFIX)
                .body("basicBuildingBlocks[3].isc.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_9))
                .body("basicBuildingBlocks[3].isc.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_9))
                .body("basicBuildingBlocks[3].isc.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].isc.conclusion.", notNullValue())
                .body("basicBuildingBlocks[3].isc.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].isc.certificateChain.chainItem[0].source", equalTo("SIGNATURE"))
                .body("basicBuildingBlocks[3].isc.certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[3].cv.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[3].cv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_16))
                .body("basicBuildingBlocks[3].cv.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_16))
                .body("basicBuildingBlocks[3].cv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].cv.conclusion.", notNullValue())
                .body("basicBuildingBlocks[3].cv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].sav.constraint[0].name", notNullValue())
                .body("basicBuildingBlocks[3].sav.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_60))
                .body("basicBuildingBlocks[3].sav.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_60))
                .body("basicBuildingBlocks[3].sav.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].sav.constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_19))
                .body("basicBuildingBlocks[3].sav.constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_19))
                .body("basicBuildingBlocks[3].sav.constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].sav.constraint[2].name", notNullValue())
                .body("basicBuildingBlocks[3].sav.constraint[2].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_59))
                .body("basicBuildingBlocks[3].sav.constraint[2].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_59))
                .body("basicBuildingBlocks[3].sav.constraint[2].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].sav.constraint[3].name", notNullValue())
                .body("basicBuildingBlocks[3].sav.constraint[3].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_20))
                .body("basicBuildingBlocks[3].sav.constraint[3].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_20))
                .body("basicBuildingBlocks[3].sav.constraint[3].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].sav.constraint[3].additionalInfo", notNullValue())
                .body("basicBuildingBlocks[3].sav.conclusion.", notNullValue())
                .body("basicBuildingBlocks[3].sav.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].xcv.constraint[0]", notNullValue())
                .body("basicBuildingBlocks[3].xcv.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_21))
                .body("basicBuildingBlocks[3].xcv.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_21))
                .body("basicBuildingBlocks[3].xcv.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].xcv.constraint[1].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_24))
                .body("basicBuildingBlocks[3].xcv.constraint[1].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_24))
                .body("basicBuildingBlocks[3].xcv.constraint[1].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_2))
                .body("basicBuildingBlocks[3].xcv.constraint[2].name", notNullValue())
                .body("basicBuildingBlocks[3].xcv.conclusion", notNullValue())
                .body("basicBuildingBlocks[3].xcv.conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].xcv.subXCV[1].conclusion", notNullValue())
                .body("basicBuildingBlocks[3].xcv.subXCV[1].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].xcv.subXCV[1].id", notNullValue())
                .body("basicBuildingBlocks[3].xcv.subXCV[1].trustAnchor", equalTo(true))
                .body("basicBuildingBlocks[3].certificateChain.chainItem[0].source", equalTo("SIGNATURE"))
                .body("basicBuildingBlocks[3].certificateChain.chainItem[0].id", notNullValue())
                .body("basicBuildingBlocks[3].conclusion", notNullValue())
                .body("basicBuildingBlocks[3].conclusion.indication", equalTo(VALID_INDICATION_VALUE_PASSED))
                .body("basicBuildingBlocks[3].id", notNullValue())
                .body("basicBuildingBlocks[3].type", equalTo("SIGNATURE"));
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
                .then().rootPath(VALIDATION_PROCESS_PREFIX)
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_5))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_5))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_1))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].error.value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_1))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].error.nameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_1))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.constraint[0].id", equalTo("S-D2C322D4081AFF8711CC89FCB2D58DBECEA5F13CDF37A0138AF67FDC2930989C"))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.indication", equalTo(VALID_INDICATION_VALUE_FAILED))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.subIndication", equalTo(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.errors[0].value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_10))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.errors[0].nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_32))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.errors[1].value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_9))
                .body("signatureOrTimestampOrCertificate[0].validationProcessBasicSignature.conclusion.errors[1].nameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_8))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_49))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_48))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_1))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].error.value", equalTo(TS_PROCESS_NOT_CONCLUSIVE))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].error.nameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_9))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.constraint[0].id", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.conclusion.indication", equalTo(VALID_INDICATION_VALUE_FAILED))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.conclusion.subIndication", equalTo(SUB_INDICATION_HASH_FAILURE))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.conclusion.errors[0].value", equalTo(REFERENCE_DATA_NOT_INTACT))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.conclusion.errors[0].nameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_10))
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].id", notNullValue())
                .body("signatureOrTimestampOrCertificate[0].timestamp[0].validationProcessTimestamp.type", equalTo("SIGNATURE_TIMESTAMP"))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].name.value", equalTo(VALID_VALIDATION_PROCESS_VALUE_6))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].name.nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_6))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].status", equalTo(VALID_VALIDATION_PROCESS_STATUS_1))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].error.value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_3))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.constraint[0].error.nameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_3))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.indication", equalTo(VALID_INDICATION_VALUE_FAILED))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.subIndication", equalTo(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.errors[0].nameId", equalTo(VALID_VALIDATION_PROCESS_NAMEID_32))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.errors[0].value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_10))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.errors[1].nameId", equalTo(VALID_VALIDATION_PROCESS_ERROR_NAMEID_8))
                .body("signatureOrTimestampOrCertificate[0].validationProcessLongTermData.conclusion.errors[1].value", equalTo(VALID_VALIDATION_PROCESS_ERROR_VALUE_9));
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
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
