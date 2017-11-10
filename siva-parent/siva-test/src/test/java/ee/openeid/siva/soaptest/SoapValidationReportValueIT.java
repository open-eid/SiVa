/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.webapp.soap.SignatureValidationData;
import ee.openeid.siva.webapp.soap.ValidationReport;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

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
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocument("Valid_ID_sig.bdoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount(),getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT_TM", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertTrue("There should be no subIndication", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "QESIG", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC-E", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocument("23635_bdoc_ts_OCSP_random_nonce.bdoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount(),getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertTrue("There should be no subIndication", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "QESIG", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC-E", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     *
     */
    @Test
    @Ignore("Invalid signatureLevel")
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdes() {
        setTestFilesDirectory("bdoc/test/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc")).andReturn().body().asString());
        ValidationReport v = getValidationReportFromDom(report);
        SignatureValidationData signatureValidationData = v.getValidationConclusion().getSignatures().getSignature().get(0);
        assertEquals("validSignaturesCount should equal with signaturesCount", v.getValidationConclusion().getValidSignaturesCount(), v.getValidationConclusion().getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT_TM", signatureValidationData.getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", signatureValidationData.getIndication().value());
        assertTrue("There should be no subIndication", signatureValidationData.getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "QES", signatureValidationData.getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", signatureValidationData.getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", signatureValidationData.getErrors().getError().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC-E", v.getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23200_weakdigest-wrong-nonce.asice
     *
     */
    @Test
    @Ignore("DD4J to DSS ")
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/test/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("23200_weakdigest-wrong-nonce.asice")).andReturn().body().asString());
        ValidationReport v = getValidationReportFromDom(report);
        SignatureValidationData signatureValidationData = v.getValidationConclusion().getSignatures().getSignature().get(0);
        assertTrue("validSignaturesCount should be zero", v.getValidationConclusion().getValidSignaturesCount()== 0);
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT_TM", signatureValidationData.getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-FAILED", signatureValidationData.getIndication().value());
        assertEquals("SubIndication should match expected", "", signatureValidationData.getSubIndication());
        assertEquals("SignatureLevel should match expected", "QESIG", signatureValidationData.getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", signatureValidationData.getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Warnings should be empty", signatureValidationData.getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC-E", v.getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocument("EE_SER-AEX-B-LTA-V-24.bdoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount(), getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LTA", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("SignatureLevel should match expected", "QESIG",getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC-E", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: SK-XML1.0.ddoc
     *
     */
    @Test @Ignore //TODO: https://github.com/open-eid/SiVa/issues/11
    public void SoapDdocCorrectValuesArePresentV1_0() {
        setTestFilesDirectory("ddoc/live/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("SK-XML1.0.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount(),getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "SK_XML_1.0", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertTrue("SignatureLevel should match expected", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel().isEmpty());
        assertEquals("SignatureScopes should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.0", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocument("igasugust1.1.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount(),getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "DIGIDOC_XML_1.1", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.1", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocument("igasugust1.2.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount(),getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "DIGIDOC_XML_1.2", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.2", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocument("igasugust1.3.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount(),getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "DIGIDOC_XML_1.3", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.3", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Pdf-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocument("pades_lt_two_valid_sig.pdf")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount(),getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "PAdES_BASELINE_LT", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("SignatureLevel should match expected", "QESIG", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "PdfByteRangeSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Errors should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().isEmpty());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", null, getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Pdf-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, PAdES_baseline_B, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     *
     */
    @Test
    @Ignore("Unknown reason")
    public void SoapPdfCorrectValuesArePresentInvalidBaselineBSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        Document report = extractReportDom(post(validationRequestForDocument("hellopades-pades-b-sha256-auth.pdf")).andReturn().body().asString());
        assertTrue("validSignaturesCount should be zero", getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount()==0);
        assertEquals("SignatureFormat should match expected", "PAdES-BASELINE-B", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-FAILED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("SubIndication should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSubIndication());
        assertEquals("SignatureLevel should match expected", "QESIG", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "PAdES", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Xroad-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xroad-simple container
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: xroad-simple.asice
     *
     */
    @Test
    public void SoapXroadCorrectValuesArePresentValidSimpleSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        Document report = extractReportDom(post(validationRequestForDocumentExtended(encodedString, "xroad-simple.asice", "XROAD","")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount(), getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertTrue("There should be no subIndication", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC-E", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Xroad-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xroad-simple invalid container
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: invalid-digest.asice
     *
     */
    @Test
    public void SoapXroadCorrectValuesArePresentInvalidSimpleSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("invalid-digest.asice"));
        Document report = extractReportDom(post(validationRequestForDocumentExtended(encodedString, "invalid-digest.asice", "XROAD","")).andReturn().body().asString());
        assertEquals("validSignaturesCount is zero", new Integer(0), getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-FAILED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertTrue("There should be no subIndication", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSubIndication().isEmpty());
        assertEquals("Error message should match expected", "MissingHeaderField: Required field 'protocolVersion' is missing", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().get(0).getContent());
        assertEquals("SignatureLevel should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureScopes().getSignatureScope().get(0).getScope());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected","ASiC-E", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
