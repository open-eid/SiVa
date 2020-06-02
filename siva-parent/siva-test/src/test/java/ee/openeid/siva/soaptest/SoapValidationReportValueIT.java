/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.webapp.soap.response.SignatureValidationData;
import ee.openeid.siva.webapp.soap.response.ValidationReport;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

import static ee.openeid.siva.integrationtest.TestData.SOAP_VALIDATION_CONCLUSION_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class SoapValidationReportValueIT extends SiVaSoapTests {

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
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: Valid_ID_sig.bdoc
     *
     */
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtTmSignature() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDocument("Valid_ID_sig.bdoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc
     *
     */
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDocument("23635_bdoc_ts_OCSP_random_nonce.bdoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     *
     */
    @Test
    @Ignore //TODO: New testfile needed
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdes() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestForDocument("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("ADES"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: testAdesQC.bdoc
     *
     */
    @Ignore //TODO: Testfile needed
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDocument("testAdesQC.bdoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("ADESIG_QC"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LTA, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: EE_SER-AEX-B-LTA-V-24.bdoc
     *
     */
    @Test
    public void SoapBdocCorrectValuesArePresentInvalidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDocument("EE_SER-AEX-B-LTA-V-24.bdoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: SK-XML1.0.ddoc
     *
     */
    @Ignore //TODO: DDOC 1.0 fails in Travis CI. Needs investigation
    @Test
    public void SoapDdocCorrectValuesArePresentV1_0() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("SK-XML1.0.ddoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("2"))
                .body("ValidSignaturesCount", Matchers.is("2"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("SK_XML_1.0"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings.Warning[0].Content", Matchers.is("Old and unsupported format: SK-XML version: 1.0"))
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.0"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.1.ddoc
     *
     */
    @Test
    public void SoapDdocCorrectValuesArePresentV1_1() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("igasugust1.1.ddoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("3"))
                .body("ValidSignaturesCount", Matchers.is("3"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("DIGIDOC_XML_1.1"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings.Warning.size()", Matchers.is(1))
                .body("Signatures.Signature[0].Warnings.Warning.Content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.1"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.2.ddoc
     *
     */
    @Test
    public void SoapDdocCorrectValuesArePresentV1_2() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("igasugust1.2.ddoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("3"))
                .body("ValidSignaturesCount", Matchers.is("3"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("DIGIDOC_XML_1.2"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings.Warning.size()", Matchers.is(1))
                .body("Signatures.Signature[0].Warnings.Warning.Content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.2"))
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.2"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.3.ddoc
     *
     */
    @Test
    public void SoapDdocCorrectValuesArePresentV1_3() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("igasugust1.3.ddoc")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("3"))
                .body("ValidSignaturesCount", Matchers.is("3"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.3"));
    }

    /**
     *
     * TestCaseID: Pdf-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, PAdES_baseline_LT, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: pades_lt_two_valid_sig.pdf
     *
     */
    @Test
    public void SoapPdfCorrectValuesArePresentBaselineLtSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestForDocument("pades_lt_two_valid_sig.pdf")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("2"))
                .body("ValidSignaturesCount", Matchers.is("2"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignatureLevel[0]", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("PARTIAL"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings.Warning.Content", Matchers.is("The trusted certificate doesn't match the trust service"));
    }

    /**
     *
     * TestCaseID: Pdf-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, PAdES_baseline_B, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     *
     */
    @Test
    public void SoapPdfCorrectValuesArePresentInvalidBaselineBSignatureV2() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestForDocument("hellopades-pades-b-sha256-auth.pdf")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("0"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-FAILED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.is("FORMAT_FAILURE"))
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FULL"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}