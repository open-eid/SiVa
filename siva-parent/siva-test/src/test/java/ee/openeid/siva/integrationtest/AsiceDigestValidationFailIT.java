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
import ee.openeid.siva.proxy.document.ReportType;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

@Category(IntegrationTest.class)
public class AsiceDigestValidationFailIT extends AbstractDigestValidationTest {

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
        post(validationRequestFor("InvalidLiveSignature.asice", null, ReportType.SIMPLE.name()))
                .then()
                //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The reference data object(s) is not intact!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("InvalidMultipleSignatures.asice", null, ReportType.SIMPLE.name()))
                .then()
                // .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[1].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[1].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
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
        post(validationRequestFor("InvalidAndValidSignatures.asice", null, ReportType.SIMPLE.name()))
                .then()
                //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
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
        post(validationRequestFor("TS-02_23634_TS_wrong_SignatureValue.asice", null, ReportType.SIMPLE.name()))
                .then()
                //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CRYPTO_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The signature is not intact!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("EE_SER-AEX-B-LT-I-26.asice", null, ReportType.SIMPLE.name()))
                .then()
               // .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The signer's certificate has not expected key-usage!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not qualified at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
    @Ignore //TODO: https://ec.europa.eu/cefdigital/tracker/browse/DSS-1221
    @Test
    public void asiceNotTrustedTsaCert() {
        post(validationRequestFor("TS-05_23634_TS_unknown_TSA.asice", null, ReportType.SIMPLE.name()))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("Signature has an invalid timestamp"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("EE_SER-AEX-B-LT-R-25.asice", null, ReportType.SIMPLE.name()))
                .then()
               // .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The past signature validation is not conclusive!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("EE_SER-AEX-B-LT-V-20.asice", null, ReportType.SIMPLE.name()))
                .then()
               // .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The revocation information is not considered as 'fresh'."))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("signWithIdCard_d4j_1.0.4_BES.asice", null, ReportType.SIMPLE.name()))
                .then()
                //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                //.body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
        post(validationRequestFor("TM-04_kehtivuskinnituset.4.asice", null, ReportType.SIMPLE.name()))
                .then()
            // .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            // .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The expected format is not found!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("SS-4_teadmataCA.4.asice", null, ReportType.SIMPLE.name()))
                .then()
               // .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_CERTIFICATE_CHAIN_FOUND"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The certificate path is not trusted!"))
                .body("validationReport.validationConclusion.signatures[0].errors[1].content", Matchers.is("The certificate chain for signature is not trusted, there is no trusted anchor."))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("TM-15_revoked.4.asice", null, ReportType.SIMPLE.name()))
                .then()
                //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("The past signature validation is not conclusive!"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
        post(validationRequestFor("TM-16_unknown.4.asice", null, ReportType.SIMPLE.name()))
                .then()
               // .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItems("No revocation data for the certificate"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
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
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("Signature has been created with expired certificate"))
                .body("validationReport.validationConclusion.validationLevel", Matchers.is("ARCHIVAL_DATA"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
