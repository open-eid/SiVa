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

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

import static ee.openeid.siva.integrationtest.TestData.*;

@Category(IntegrationTest.class)
public class AsiceValidationFailIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timestamp/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Asice-ValidationFail-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Bdoc with single invalid signature
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: InvalidLiveSignature.asice
     */
    @Test
    public void asiceInvalidSingleSignature() {
        post(validationRequestFor("InvalidLiveSignature.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("38211015222"))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_HASH_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-10-11T09:36:10Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE, TS_PROCESS_NOT_CONCLUSIVE, REFERENCE_DATA_NOT_INTACT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validatedDocument.fileHash", Matchers.is("+/WJhUuOlP0RDfENTaSzESr+Dbg5D3GYcS3CsqjPm0U="));
    }

    /**
     * TestCaseID: Asice-ValidationFail-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice with multiple invalid signatures
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: InvalidMultipleSignatures.bdoc
     */
    @Test
    public void asiceInvalidMultipleSignatures() {
        post(validationRequestFor("InvalidMultipleSignatures.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-06-21T21:33:10Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE, TS_PROCESS_NOT_CONCLUSIVE, REFERENCE_DATA_NOT_INTACT))
                .body("signatures[1].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[1].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[1].info.bestSignatureTime", Matchers.is("2016-06-21T21:38:50Z"))
                .body("signatures[1].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE, TS_PROCESS_NOT_CONCLUSIVE, REFERENCE_DATA_NOT_INTACT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(2))
                .body("validatedDocument.fileHash", Matchers.is("s0H0Nsgk5x6FYA+q65kYo8ENIm4ZTMxxSiMjOfNcLqQ="));
    }

    /**
     * TestCaseID: Asice-ValidationFail-3
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice with multiple signatures both valid and invalid
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: InvalidAndValidSignatures.asice
     */
    @Test
    public void asiceInvalidAndValidMultipleSignatures() {
        post(validationRequestFor("InvalidAndValidSignatures.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[1].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].info.bestSignatureTime", Matchers.is("2016-06-21T21:38:50Z"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-06-21T21:33:10Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE, TS_PROCESS_NOT_CONCLUSIVE, REFERENCE_DATA_NOT_INTACT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2))
                .body("validatedDocument.fileHash", Matchers.is("RUVTd2Ch4XitCNk2ntJTCB3aAX+YSke/xbId8IwCJok="));
    }

    /**
     * TestCaseID: Asice-ValidationFail-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice with no signatures
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: AsiceContainerNoSignature.asice
     */
    @Test
    public void asiceNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");

        post(validationRequestFor("AsiceContainerNoSignature.asice"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));

    }

    /**
     * TestCaseID: Asice-ValidationFail-5
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Wrong signature timestamp
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TS-02_23634_TS_wrong_SignatureValue.asice
     */
    @Test
    public void asiceInvalidTimeStampDontMatchSigValue() {
        post(validationRequestFor("TS-02_23634_TS_wrong_SignatureValue.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2015-11-13T11:15:36Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE, TS_PROCESS_NOT_CONCLUSIVE, REFERENCE_DATA_NOT_INTACT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-6
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice No non-repudiation key usage value in the certificate, verification of AdES signature level
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: EE_SER-AEX-B-LT-I-43.asice
     */
    @Test
    public void asiceInvalidNonRepudiationKey() {
        post(validationRequestFor("EE_SER-AEX-B-LT-I-43.asice", VALID_SIGNATURE_POLICY_3,"Simple"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_NOT_ADES))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CONSTRAINTS_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-7
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice signers certificate does not have non-repudiation value in the certificates key usage field and it does not contain the QC and SSCD compliance information.
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: EE_SER-AEX-B-LT-I-26.asice
     */
    @Test
    public void asiceInvalidNonRepudiationKeyNoComplianceInfo() {
        post(validationRequestFor("EE_SER-AEX-B-LT-I-26.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CONSTRAINTS_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("signatures[0].warnings[0].content", Matchers.is(CERTIFICATE_DO_NOT_MATCH_TRUST_SERVICE))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-8
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: OCSP certificate is not trusted
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-01_bdoc21-unknown-resp.bdoc
     */
    @Test
    public void asiceNotTrustedOcspCert() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDSS("TM-01_bdoc21-unknown-resp.bdoc", null, null))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_NO_CERTIFICATE_CHAIN_FOUND))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2013-11-11T06:45:46Z")) //this may not be valid time to show
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-9
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice TSA certificate is not trusted
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TS-05_23634_TS_unknown_TSA.asice
     */
    @Test
    public void asiceNotTrustedTsaCert() {
        post(validationRequestFor("TS-05_23634_TS_unknown_TSA.asice", null, null))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(TS_PROCESS_NOT_CONCLUSIVE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-10
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice OCSP response status is revoked
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: EE_SER-AEX-B-LT-R-25.asice
     */
    @Test
    public void asiceTsOcspStatusRevoked() {
        post(validationRequestFor("EE_SER-AEX-B-LT-R-25.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_NO_POE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2014-11-07T11:43:06Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(PAST_SIG_VALIDATION_NOT_CONCLUSIVE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-11
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice difference between OCSP and time-stamp issuing times is more than 24 hours
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: EE_SER-AEX-B-LT-V-20.asice
     */
    @Test
    public void asiceOcspAndTsDifferenceOver24H() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-20.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2014-11-07T13:18:01Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(REVOCATION_NOT_FRESH))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-12
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice unsigned data files in the container
     * <p>
     * Expected Result: The document should pass the validation with warning
     * <p>
     * File: EE_SER-AEX-B-LT-V-34.asice
     */
    @Test
    public void asiceUnsignedDataFiles() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-34.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings.content", Matchers.hasItems(ALL_FILES_NOT_SIGNED))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: Asice-ValidationFail-13
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice SignatureValue does not correspond to the SignedInfo block
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: REF-19_bdoc21-no-sig-asn1-pref.bdoc
     */
    @Test
    public void asiceSignatureValueDoNotCorrespondToSignedInfo() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDSS("REF-19_bdoc21-no-sig-asn1-pref.bdoc", null, null))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-14
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice Baseline-BES file
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: signWithIdCard_d4j_1.0.4_BES.asice
     */
    @Test
    public void asiceBaselineBesSignatureLevel() {
        post(validationRequestFor("signWithIdCard_d4j_1.0.4_BES.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_B))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationFail-15
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice Baseline-EPES file
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-04_kehtivuskinnituset.4.asice
     */
    @Test
    public void asiceBaselineEpesSignatureLevel() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-04_kehtivuskinnituset.4.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_B))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-16
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice signers certificate is not trusted
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: SS-4_teadmataCA.4.asice
     */
    @Test
    public void asiceSignersCertNotTrusted() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("SS-4_teadmataCA.4.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors[0].content", Matchers.is(CERT_PATH_NOT_TRUSTED))
                .body("signatures[0].errors[1].content", Matchers.is(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-17
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice OCSP response status is revoked
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-15_revoked.4.asice
     */
    @Test
    public void asiceTmOcspStatusRevoked() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-15_revoked.4.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_NO_POE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2013-10-11T11:27:19Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(PAST_SIG_VALIDATION_NOT_CONCLUSIVE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-18
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice OCSP response status is unknown
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-16_unknown.4.asice
     */
    @Test
    public void asiceTmOcspStatusUnknown() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("TM-16_unknown.4.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-19
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice signed data file has been removed from the container
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: KS-21_fileeemaldatud.4.asice
     */
    @Test
    public void asiceSignedFileRemoved() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("KS-21_fileeemaldatud.4.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIGNED_DATA_NOT_FOUND))
                .body("signatures[0].errors[0].content", Matchers.is(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-20
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice no files in container
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: KS-02_tyhi.bdoc
     */
    @Test
    public void asiceNoFilesInContainer() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDSS("KS-02_tyhi.bdoc", null, null))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors", Matchers.hasSize(2));
    }

    /**
     * TestCaseID: Asice-ValidationFail-21
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice signed data file(s) don't match the hash values in reference elements
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: REF-14_filesisumuudetud.4.bdoc
     */
    @Test
    public void asiceDataFilesDontMatchHash() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDSS("REF-14_filesisumuudetud.4.bdoc", null, null))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_HASH_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItem(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-22
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice Baseline-T signature
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TS-06_23634_TS_missing_OCSP.asice
     */
    @Test
    public void asiceBaselineTSignature() {
        post(validationRequestFor("TS-06_23634_TS_missing_OCSP.asice", null, null))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_T))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItem(LTV_PROCESS_NOT_ACCEPTABLE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-23
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     * <p>
     * Title: Asice certificate's validity time is not in the period of OCSP producedAt time
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File:
     */
    @Ignore //TODO: test file is needed where certificate expiration end is before the OCSP produced at time
    @Test
    public void asiceCertificateValidityOutOfOcspRange() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDSS("", null, null))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItem(SIG_CREATED_WITH_EXP_CERT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
