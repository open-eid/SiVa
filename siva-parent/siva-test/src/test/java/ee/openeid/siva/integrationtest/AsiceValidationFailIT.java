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

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static ee.openeid.siva.integrationtest.TestData.*;

@Tag("IntegrationTest")
public class AsiceValidationFailIT extends SiVaRestTests {

    @BeforeEach
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common-validation-constraints-polv3-polv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].signedBy", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("38211015222"))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_HASH_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-10-11T09:36:10Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, TS_MESSAGE_NOT_INTACT, REFERENCE_DATA_NOT_INTACT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-06-21T21:33:10Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, TS_MESSAGE_NOT_INTACT, VALID_VALIDATION_PROCESS_ERROR_VALUE_9))
                .body("signatures[1].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[1].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[1].info.bestSignatureTime", Matchers.is("2016-06-21T21:38:50Z"))
                .body("signatures[1].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, TS_MESSAGE_NOT_INTACT, VALID_VALIDATION_PROCESS_ERROR_VALUE_9))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Asice-ValidationFail-3
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[1].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].info.bestSignatureTime", Matchers.is("2016-06-21T21:38:50Z"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-06-21T21:33:10Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(TS_MESSAGE_NOT_INTACT, VALID_VALIDATION_PROCESS_ERROR_VALUE_9))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Asice-ValidationFail-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-5
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2015-11-13T11:15:36Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(TS_MESSAGE_NOT_INTACT, VALID_VALIDATION_PROCESS_ERROR_VALUE_9))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-6
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_INDETERMINATE_UNKNOWN))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_CHAIN_CONSTRAINTS_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, NOT_EXPECTED_KEY_USAGE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-7
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_CHAIN_CONSTRAINTS_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, NOT_EXPECTED_KEY_USAGE))
                .body("signatures[0].errors.content", Matchers.hasItems(NOT_EXPECTED_KEY_USAGE))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-8
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_FORMAT_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(VALID_VALIDATION_PROCESS_ERROR_VALUE_5, REVOCATION_NOT_TRUSTED))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].certificates.size()", Matchers.is(1))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEPzCCAyegAwIBAgIQH0FobucEcidPGVN0HUUgATANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhk"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-9
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(TS_NOT_TRUSTED))
                .body("signatures[0].certificates.size()", Matchers.is(3))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("ŽAIKOVSKI,IGOR,37101010021"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEjzCCA3egAwIBAgIQZTNeodpzkAxPgpfyQEp1dTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("TEST of ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIEuzCCA6OgAwIBAgIQSxRID7FoIaNNdNhBeucLvDANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("Time Stamp Authority Server"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIG2jCCBMKgAwIBAgIBCDANBgkqhkiG9w0BAQUFADCBpDELMA"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-10
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_REVOKED_NO_POE))
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Asice unsigned data files in the container
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: EE_SER-AEX-B-LT-V-34.asice
     */
    @Test
    public void asiceUnsignedDataFiles() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-34.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].warnings.content", Matchers.hasItems(VALID_VALIDATION_PROCESS_VALUE_35))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-13
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIG_CRYPTO_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, VALID_VALIDATION_PROCESS_ERROR_VALUE_9))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-14
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_B))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, SIG_UNEXPECTED_FORMAT))
                .body("signatures[0].signedBy", Matchers.is("UUKKIVI,KRISTI,48505280278"))
                .body("signatures[0].certificates.size()", Matchers.is(1))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("UUKKIVI,KRISTI,48505280278"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEojCCA4qgAwIBAgIQPKphkF8jscxRrFRhBsxlhjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhk"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationFail-15
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_B))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(SIG_UNEXPECTED_FORMAT))
                .body("signatures[0].signedBy", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].certificates.size()", Matchers.is(1))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIE/TCCA+WgAwIBAgIQJw9uhQnKff9RdnVKwzk1OzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("TEST of ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIEuzCCA6OgAwIBAgIQSxRID7FoIaNNdNhBeucLvDANBgkqhk"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-16
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(SIG_NOT_TRUSTED, CERT_PATH_NOT_TRUSTED))
                .body("signatures[0].signedBy", Matchers.is("signer1"))
                .body("signatures[0].certificates.size()", Matchers.is(2))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("signer1"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIICHDCCAYWgAwIBAgIBAjANBgkqhkiG9w0BAQUFADAqMQswCQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("libdigidocpp Inter"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIICCTCCAXKgAwIBAgIBAzANBgkqhkiG9w0BAQUFADAnMQswCQ"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-17
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_REVOKED_NO_POE))
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].errors.content", Matchers.hasItems(REVOCATION_UNKNOWN))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-19
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_SIGNED_DATA_NOT_FOUND))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, REFERENCE_DATA_NOT_FOUND))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-20
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_HASH_FAILURE))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, REFERENCE_DATA_NOT_INTACT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-22
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_T))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItems(CERT_VALIDATION_NOT_CONCLUSIVE, REVOCATION_NOT_FOUND))
                .body("signatures[0].signedBy", Matchers.is("ŽAIKOVSKI,IGOR,37101010021"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("ŽAIKOVSKI,IGOR,37101010021"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEjzCCA3egAwIBAgIQZTNeodpzkAxPgpfyQEp1dTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("TEST of ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIEuzCCA6OgAwIBAgIQSxRID7FoIaNNdNhBeucLvDANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("tsa01.quovadisglobal.com"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIGOzCCBSOgAwIBAgIUe6m/OP/GwmsrkHR8Mz8LJoNedfgwDQ"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-23
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Asice certificate's validity time is not in the period of OCSP producedAt time
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File:
     */
    @Disabled //TODO: test file is needed where certificate expiration end is before the OCSP produced at time
    @Test
    public void asiceCertificateValidityOutOfOcspRange() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDSS("", null, null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItem(SIG_CREATED_WITH_EXP_CERT))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-24
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: BDoc with invalid signature, no signing certificate found
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: TM-invalid-sig-no-sign-cert.asice
     */
    @Test
    public void asiceInvalidSignatureNoSigningCertificateFound() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String fileName = "TM-invalid-sig-no-sign-cert.asice";
        post(validationRequestFor(fileName))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signedBy", Matchers.emptyOrNullString())
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_T))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].claimedSigningTime", Matchers.is("2013-10-11T11:47:40Z"))
                .body("signatures[0].errors.content", Matchers.hasItems(VALID_VALIDATION_PROCESS_ERROR_VALUE_9, SIG_NO_CANDIDATE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1))
                .body("validatedDocument.filename", Matchers.is(fileName));
    }

    /**
     * TestCaseID: Asice-ValidationFail-25
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: BDoc with invalid signature, signed with expired certificate
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: IB-5987_signed_with_expired_certificate.asice
     */
    @Test
    public void asiceSignedWithExpiredCertificate() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        String fileName = "IB-5987_signed_with_expired_certificate.asice";
        post(validationRequestFor(fileName))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signedBy", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_B))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].claimedSigningTime", Matchers.is("2016-08-01T13:07:13Z"))
                .body("signatures[0].errors.content", Matchers.hasItem(VALID_VALIDATION_PROCESS_ERROR_VALUE_10))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1))
                .body("validatedDocument.filename", Matchers.is(fileName));
    }

    /**
     * TestCaseID: Asice-ValidationFail-26
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Bdoc signed properties element missing
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: REF-03_bdoc21-TS-no-signedpropref.asice
     */
    @Test
    public void bdocTimemarkSignedPropertiesMissing() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
        post(validationRequestFor("REF-03_bdoc21-TS-no-signedpropref.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_NOT_ETSI))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].errors.content", Matchers.hasItem(SIG_QUALIFYING_PROPERTY_MISSING))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-27
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Asice LT signature signed with expired AIA OCSP certificate
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: esteid2018signerAiaOcspLT.asice
     */
    @Test
    public void asiceLtSignatureSignedWithExpiredAiaOCSP() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("esteid2018signerAiaOcspLT.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].errors.content", Matchers.hasItem(VALID_VALIDATION_PROCESS_ERROR_VALUE_5))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-28
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Asice LTA signature signed with expired AIA OCSP certificate
     * <p>
     * Expected Result: The document should fail the validation
     * <p>
     * File: esteid2018signerAiaOcspLTA.asice
     */
    @Test
    public void asiceLtaSignatureSignedWithExpiredAiaOCSP() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("esteid2018signerAiaOcspLTA.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LTA))
                .body("signatures[0].indication", Matchers.is(INDETERMINATE))
                .body("signatures[0].errors.content", Matchers.hasItem(VALID_VALIDATION_PROCESS_ERROR_VALUE_5))
                .body("validSignaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Asice-ValidationFail-29
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Asice simple xroad document
     *
     * Expected Result: Document should fail as xroad document validation is not supported
     *
     * File: xroad-simple.asice
     */
    @Test
    public void asiceSimpleXroadDocumentShouldFail() {
        setTestFilesDirectory("xroad/");

        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.asice", VALID_SIGNATURE_POLICY_3))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_FORMAT_FAILURE))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("70006317"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].errors.content", Matchers.hasItems(SIG_UNEXPECTED_FORMAT))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationFail-30
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Asice batchsignature xroad document
     *
     * Expected Result: Document should fail as xroad document validation is not supported
     *
     * File: xroad-batchsignature.asice
     */
    @Test
    public void asiceBatchXroadDocumentShouldFail() {
        setTestFilesDirectory("xroad/");

        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-batchsignature.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-batchsignature.asice", VALID_SIGNATURE_POLICY_3))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_FORMAT_FAILURE))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("70006317"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].errors.content", Matchers.hasItems(SIG_UNEXPECTED_FORMAT))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationFail-31
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Asice attachment xroad document
     *
     * Expected Result: Document should fail as xroad document validation is not supported
     *
     * File: xroad-attachment.asice
     */
    @Test
    public void validatingAttachXroadDocumentShouldFail() {
        setTestFilesDirectory("xroad/");

        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-attachment.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-attachment.asice", VALID_SIGNATURE_POLICY_3))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B"))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].subIndication", Matchers.is(SUB_INDICATION_FORMAT_FAILURE))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("70006317"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].errors.content", Matchers.hasItems(SIG_UNEXPECTED_FORMAT))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationFail-32
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice Baseline-LTA file
     *
     * Expected Result: The document should fail the validation as TS is not qualified
     *
     * File: EE_SER-AEX-B-LTA-V-24.asice
     */
    @Test
    public void asiceBaselineLtaProfileInvalidSignature() {
        post(validationRequestFor("EE_SER-AEX-B-LTA-V-24.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LTA))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2014-10-30T18:50:35Z"))
                .body("signatures[0].signedBy", Matchers.is("METSMA,RAUL,38207162766"))
                .body("signatures[0].errors.content", Matchers.hasItems(VALID_VALIDATION_PROCESS_ERROR_VALUE_11))
                .body("signatures[0].certificates.size()", Matchers.is(4))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("METSMA,RAUL,38207162766"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEmzCCA4OgAwIBAgIQFQe7NKtE06tRSY1vHfPijjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("BalTstamp QTSA TSU2"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEtzCCA5+gAwIBAgIKFg5NNQAAAAADhzANBgkqhkiG9w0BAQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].issuer.commonName",  Matchers.startsWith("SSC Qualified Class 3 CA"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].issuer.content",  Matchers.startsWith("MIIFvTCCA6WgAwIBAgIQWJFmnMAIyiVAcLMn/5wGnjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].commonName",  Matchers.is("BalTstamp QTSA TSU2"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEtzCCA5+gAwIBAgIKFg5NNQAAAAADhzANBgkqhkiG9w0BAQ"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].issuer.commonName",  Matchers.startsWith("SSC Qualified Class 3 CA"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].issuer.content",  Matchers.startsWith("MIIFvTCCA6WgAwIBAgIQWJFmnMAIyiVAcLMn/5wGnjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(0));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
