package ee.openeid.siva.soap;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
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

    /***
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: Valid_ID_sig.bdoc
     *
     ***/
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtTmSignature() {
        setTestFilesDirectory("bdoc/live/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("Valid_ID_sig.bdoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getValidSignaturesCount(),getQualifiedReportFromDom(report).getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT_TM", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("There should be no subIndication", getQualifiedReportFromDom(report).getSignatures().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "QES", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC_E", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23635_bdoc_ts_OCSP_random_nonce.bdoc
     *
     ***/
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        Document report = extractReportDom(post(validationRequestForDocument("23635_bdoc_ts_OCSP_random_nonce.bdoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getValidSignaturesCount(),getQualifiedReportFromDom(report).getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("There should be no subIndication", getQualifiedReportFromDom(report).getSignatures().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "QES", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC_E", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-3
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     *
     ***/
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdes() {
        setTestFilesDirectory("bdoc/test/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getValidSignaturesCount(),getQualifiedReportFromDom(report).getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT_TM", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("There should be no subIndication", getQualifiedReportFromDom(report).getSignatures().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "AdES", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertEquals("Warnings should match expected", "The certificate is not supported by SSCD!", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().get(0).getDescription());
        assertEquals("SignatureForm should match expected", "ASiC_E", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-4
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23200_weakdigest-wrong-nonce.asice
     *
     ***/
    @Test @Ignore //TODO: VAL-242 Subindication is empty although in case of failure it is expected to have value
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/test/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("23200_weakdigest-wrong-nonce.asice")).andReturn().body().asString());
        assertTrue("validSignaturesCount should be zero", getQualifiedReportFromDom(report).getValidSignaturesCount()== 0);
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT_TM", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-FAILED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertEquals("SubIndication should match expected", "ReplaceWithCorrectValue", getQualifiedReportFromDom(report).getSignatures().get(0).getSubIndication());
        assertEquals("SignatureLevel should match expected", "AdESqc", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC_E", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-5
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LTA, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: EE_SER-AEX-B-LTA-V-24.bdoc
     *
     ***/
    @Test
    public void SoapBdocCorrectValuesArePresentInvalidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        Document report = extractReportDom(post(validationRequestForDocument("EE_SER-AEX-B-LTA-V-24.bdoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getSignaturesCount(), getQualifiedReportFromDom(report).getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LTA", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertEquals("SignatureLevel should match expected", "QES",getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC_E", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: SK-XML1.0.ddoc
     *
     ***/
    @Test @Ignore //TODO: VAL-238 Travis fails the test, although in local machine it passes
    public void SoapDdocCorrectValuesArePresentV1_0() {
        setTestFilesDirectory("ddoc/live/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("SK-XML1.0.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getSignaturesCount(),getQualifiedReportFromDom(report).getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "SK_XML_1.0", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("SignatureLevel should match expected", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel().isEmpty());
        assertEquals("SignatureScopes should match expected", "", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.0", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.1.ddoc
     *
     ***/
    @Test
    public void SoapDdocCorrectValuesArePresentV1_1() {
        setTestFilesDirectory("ddoc/live/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("igasugust1.1.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getSignaturesCount(),getQualifiedReportFromDom(report).getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "DIGIDOC_XML_1.1", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("SignatureLevel should match expected", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel().isEmpty());
        assertEquals("SignatureScopes should match expected", "", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.1", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-3
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.2.ddoc
     *
     ***/
    @Test
    public void SoapDdocCorrectValuesArePresentV1_2() {
        setTestFilesDirectory("ddoc/live/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("igasugust1.2.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getSignaturesCount(),getQualifiedReportFromDom(report).getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "DIGIDOC_XML_1.2", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("SignatureLevel should match expected", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel().isEmpty());
        assertEquals("SignatureScopes should match expected", "", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.2", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-4
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.3.ddoc
     *
     ***/
    @Test
    public void SoapDdocCorrectValuesArePresentV1_3() {
        setTestFilesDirectory("ddoc/live/timemark/");
        Document report = extractReportDom(post(validationRequestForDocument("igasugust1.3.ddoc")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getSignaturesCount(),getQualifiedReportFromDom(report).getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "DIGIDOC_XML_1.3", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("SignatureLevel should match expected", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel().isEmpty());
        assertEquals("SignatureScopes should match expected", "", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "DIGIDOC_XML_1.3", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Pdf-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, PAdES_baseline_LT, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: pades_lt_two_valid_sig.pdf
     *
     ***/
    @Test
    public void SoapPdfCorrectValuesArePresentBaselineLtSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        Document report = extractReportDom(post(validationRequestForDocument("pades_lt_two_valid_sig.pdf")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getSignaturesCount(),getQualifiedReportFromDom(report).getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "PAdES_BASELINE_LT", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertEquals("SignatureLevel should match expected", "QES", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "PdfByteRangeSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Errors should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getErrors().isEmpty());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "PAdES", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Pdf-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, PAdES_baseline_B, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     *
     ***/
    @Test @Ignore //TODO: VAL-242
    public void SoapPdfCorrectValuesArePresentInvalidBaselineBSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        Document report = extractReportDom(post(validationRequestForDocument("hellopades-pades-b-sha256-auth.pdf")).andReturn().body().asString());
        assertTrue("validSignaturesCount should be zero", getQualifiedReportFromDom(report).getValidSignaturesCount()==0);
        assertEquals("SignatureFormat should match expected", "PAdES_BASELINE_B", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-FAILED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertEquals("SubIndication should match expected", "ReplaceWithCorrectValue", getQualifiedReportFromDom(report).getSignatures().get(0).getSubIndication());
        assertEquals("SignatureLevel should match expected", "QES", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "PAdES", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Xroad-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xroad-simple container
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: xroad-simple.asice
     *
     ***/
    @Test @Ignore //TODO: VAL-315
    public void SoapXroadCorrectValuesArePresentValidSimpleSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        Document report = extractReportDom(post(validationRequestForDocumentExtended(encodedString, "xroad-simple.asice", "XROAD","")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getValidSignaturesCount(), getQualifiedReportFromDom(report).getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("There should be no subIndication", getQualifiedReportFromDom(report).getSignatures().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "ASIC_E", getQualifiedReportFromDom(report).getSignatureForm());
    }

    /***
     *
     * TestCaseID: Xroad-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of values in Validation Report, xroad-simple invalid container
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: xroad-simple.asice
     *
     ***/
    @Test @Ignore //TODO: VAL-323
    public void SoapXroadCorrectValuesArePresentInvalidSimpleSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("invalid-digest.asice"));
        Document report = extractReportDom(post(validationRequestForDocumentExtended(encodedString, "invalid-digest.asice", "XROAD","")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getQualifiedReportFromDom(report).getValidSignaturesCount(), getQualifiedReportFromDom(report).getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-FAILED", getQualifiedReportFromDom(report).getSignatures().get(0).getIndication());
        assertTrue("There should be no subIndication", getQualifiedReportFromDom(report).getSignatures().get(0).getSubIndication().isEmpty());
        assertEquals("SignatureLevel should match expected", "", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureLevel());
        assertEquals("SignatureScopes should match expected", "FullSignatureScope", getQualifiedReportFromDom(report).getSignatures().get(0).getSignatureScopes().get(0).getScope());
        assertTrue("Warnings should be empty", getQualifiedReportFromDom(report).getSignatures().get(0).getWarnings().isEmpty());
        assertEquals("SignatureForm should match expected", "ASIC_E", getQualifiedReportFromDom(report).getSignatureForm());
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}