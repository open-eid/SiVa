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

import ee.openeid.siva.common.Constants;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Disabled
class EuPlugValidationPassIT extends SiVaRestTests {

    @Value("${plugtest.location}")
    private String location;

    @Override
    protected String getTestFilesDirectory() {
        return location;
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Lithuania adoc-v2.0 signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    void lithuaniaAsiceAdoc20ValidSignature() {
        post(validationRequestForEu("Signature-A-LT_MIT-1.asice"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation multiple of Lithuania adoc-v2.0 signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-2.asice
     */
    @Test
    void lithuaniaAsiceAdoc20TwoValidSignatures() {
        post(validationRequestForEu("Signature-A-LT_MIT-2.asice"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[1].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[1].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-3
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Lithuania adoc-v2.0 signature with warning
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-5.asice"
     */
    @Test
    void lithuaniaAsiceAdoc20ValidSignatureWithWarning() {
        post(validationRequestForEu("Signature-A-LT_MIT-5.asice"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Latvian edoc-v2.0 signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LV_EUSO-1.asice
     */
    @Test
    void latviaAsiceEdoc20ValidSignature() {
        post(validationRequestForEu("Signature-A-LV_EUSO-1.asice"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-T")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-5
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LV_EUSO-2.asice
     */
    @Test //TODO: this file is actually identical to the Signature-A-LV_EUSO-1.asice
    void A_LV_EUSO_2Valid() {
        post(validationRequestForEu("Signature-A-LV_EUSO-2.asice"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-T")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-6
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Polish Asic-s with CAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-PL_KIR-1.asics
     */
    @Test
    void polandAsicsCadesValidSignatureWithWarning() {
        post(validationRequestForEu("Signature-A-PL_KIR-1.asics"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-7
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Polish Asic-s with XAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-PL_KIR-2.asics
     */
    @Test
    void polandAsicsXadesValidSignatureWithWarning() {
        post(validationRequestForEu("Signature-A-PL_KIR-2.asics"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-8
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Slovakia Asic-e with XAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-SK_DIT-3.asice
     */
    @Test //The file is not valid, revocation outside of 24h timeframe
    void slovakiaAsiceXadesValidSignature() {
        post(validationRequestForEu("Signature-A-SK_DIT-3.asice"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-05-02T09:16:58Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-05-02T09:35:58Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-10
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-C-AT_SIT-1.p7m
     */
    @Test
    void austrianCadesValidSignature() {
        post(validationRequestForEu("Signature-C-AT_SIT-1.p7m"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-03-31T14:41:57Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-11
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of German CAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-C-DE_SCI-1.p7m
     */
    @Test
    void germanyCadesValidSignatureWithWarning() {
        post(validationRequestForEu("Signature-C-DE_SCI-1.p7m"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-03-31T14:41:57Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-12
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-C-ES_MIN-1.p7m
     */
    @Test
    void spainCadesBValidSignature() {
        post(validationRequestForEu("Signature-C-ES_MIN-1.p7m"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-B"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-11T07:30:26Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-13
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-C-ES_MIN-2.p7m
     */
    @Test
    void spainCadesTValidSignature() {
        post(validationRequestForEu("Signature-C-ES_MIN-2.p7m"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-T")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-11T07:30:27Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-11T07:30:29Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-14
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Italian Cades signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-C-IT_BIT-5.p7m
     */
    @Test
    void italyCadesTwoValidSignatures() {
        post(validationRequestForEu("Signature-C-IT_BIT-5.p7m"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-22T14:07:35Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is(""))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[1].signatureFormat", Matchers.is("CAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[1].signatureLevel", Matchers.is("NA"))
                .body("validationReport.validationConclusion.signatures[1].claimedSigningTime", Matchers.is("2016-04-22T14:08:35Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-15
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Poland CAdES B signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-C-PL_ADS-4.p7m
     */
    @Test
    void polandCadesValidSignatureWithWarning() {
        post(validationRequestForEu("Signature-C-PL_ADS-4.p7m"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-08T12:09:38Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is(""))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-16
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Poland CAdES T signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-C-PL_ADS-7.p7m
     */
    @Test
    void polandCadesValidSignature() {
        post(validationRequestForEu("Signature-C-PL_ADS-7.p7m"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("CAdES-BASELINE-T")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-08T08:41:09Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-08T08:41:19Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-17
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Belgium PAdES B signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-BE_CONN-1.pdf
     */
    @Test
    void belgiumPadesValidSignature() {
        post(validationRequestForEu("Signature-P-BE_CONN-1.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-14T13:28:54Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-18
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Belgian PAdES LTA signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-BE_CONN-7.pdf
     */
    @Test
    void belgiumPadesValidSignatureWithWarnings() {
        post(validationRequestForEu("Signature-P-BE_CONN-7.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE-LTA")) //No acceptable revocation data
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The trust service of the timestamp has not expected type identifier!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-14T14:03:00Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-14T14:03:24Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-19
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of German PAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-DE_SCI-2.pdf
     */
    @Test
    void germanyPadesValidSignatureWithWarning() {
        post(validationRequestForEu("Signature-P-DE_SCI-2.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-03-31T14:49:57Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-20
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Italian PAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-IT_MID-1.pdf
     */
    @Test
    void italyPadesValidSignature() {
        post(validationRequestForEu("Signature-P-IT_MID-1.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-05T08:25:27Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-21
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Lithuanian PAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-LT_MIT-1.pdf
     */
    @Test
    void lithuaniaPadesValidSignature() {
        post(validationRequestForEu("Signature-P-LT_MIT-1.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-T")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-08T10:16:06Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-08T10:16:20Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-22
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Lithuanian PAdES signature 2
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-LT_MIT-2.pdf
     */
    @Test
    void lithuaniaPadesValidSignature2() {
        post(validationRequestForEu("Signature-P-LT_MIT-2.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES-BASELINE-T")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-08T10:14:19Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-08T10:14:45Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-23
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Latvian PAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-LV_EUSO-1.pdf
     */
    @Test
    void latviaPadesValidSignature() {
        post(validationRequestForEu("Signature-P-LV_EUSO-1.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-11T13:33:37Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-11T13:33:49Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-24
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-LV_EUSO-2.pdf
     */
    @Test //TODO: this file is identical to Signature-P-LV_EUSO-1.pdf
    void P_LV_EUSO_2Valid() {
        post(validationRequestForEu("Signature-P-LV_EUSO-2.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(0))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-11T13:33:37Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-11T13:33:49Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-25
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Polish PAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-PL_ADS-6.pdf
     */
    @Test //The file should not be valid
    void polandPadesValidSignatureWithWarnings() {
        post(validationRequestForEu("Signature-P-PL_ADS-6.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-08T12:56:31Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-08T12:56:42Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: EuPlug-ValidationPass-26
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Polish PAdES QES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-P-PL_ADS-8.pdf
     */
    @Test // This file should not be valid
    void polandPadesValidQesSignature() {
        post(validationRequestForEu("Signature-P-PL_ADS-8.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].Errors[0].content", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-08T08:47:28Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-04-08T08:47:38Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-27
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    void X_AT_SIT_1Valid() {
        post(validationRequestForEu("Signature-X-AT_SIT-1.xml"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NA"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-28
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    void X_AT_SIT_21Valid() {
        post(validationRequestForEu("Signature-X-AT_SIT-21.xml"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NA"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-29
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Belgian XAdES signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-X-BE_CONN-1.xml
     */
    @Test
    void belgiumXadesValidSignature() {
        post(validationRequestForEu("Signature-X-BE_CONN-1.xml"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-18T11:02:37Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-30
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-X-BE_CONN-21.xml
     */
    @Test
    void X_BE_CONN_21Valid() {
        post(validationRequestForEu("Signature-X-BE_CONN-21.xml"))
                .then()
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B")) //Minimal LT required
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("ADESIG_QC"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is not created by a QSCD!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-04-18T11:03:29Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is(""))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    private String validationRequestForEu(String file){
        return validationRequestForEuWithPolicy(file, VALID_SIGNATURE_POLICY_3);
    }

    private String validationRequestForEuWithPolicy(String file, String policy){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("document", Base64.encodeBase64String(Files.readAllBytes(Paths.get(getTestFilesDirectory() + file))));
            jsonObject.put("filename", file);
            jsonObject.put("signaturePolicy", policy);
        }catch (IOException e){
            throw new RuntimeException("Error on reading file", e);
        }
        return jsonObject.toString();
    }
}
