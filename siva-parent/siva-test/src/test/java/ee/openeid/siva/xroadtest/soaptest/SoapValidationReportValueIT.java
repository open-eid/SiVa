package ee.openeid.siva.xroadtest.soaptest;

import ee.openeid.siva.soaptest.SiVaSoapTests;
import ee.openeid.siva.xroadtest.configuration.XroadIntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(XroadIntegrationTest.class)
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
     * TestCaseID: Xroad-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocumentExtended(encodedString, "xroad-simple.asice", "XROAD", "POLv3")).andReturn().body().asString());
        assertEquals("validSignaturesCount should equal with signaturesCount", getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount(), getValidationReportFromDom(report).getValidationConclusion().getSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-PASSED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("SignatureLevel should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertEquals("SignatureForm should match expected", "ASiC-E", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    /**
     *
     * TestCaseID: Xroad-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
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
        Document report = extractReportDom(post(validationRequestForDocumentExtended(encodedString, "invalid-digest.asice", "XROAD", "POLv3")).andReturn().body().asString());
        assertEquals("validSignaturesCount is zero", new Integer(0), getValidationReportFromDom(report).getValidationConclusion().getValidSignaturesCount());
        assertEquals("SignatureFormat should match expected", "XAdES_BASELINE_LT", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureFormat());
        assertEquals("Indication should match expected", "TOTAL-FAILED", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getIndication().value());
        assertEquals("Error message should match expected", "InvalidSignatureValue: Signature is not valid", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getErrors().getError().get(0).getContent());
        assertEquals("SignatureLevel should match expected", "", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getSignatureLevel());
        assertTrue("Warnings should be empty", getValidationReportFromDom(report).getValidationConclusion().getSignatures().getSignature().get(0).getWarnings().getWarning().isEmpty());
        assertEquals("SignatureForm should match expected", "ASiC-E", getValidationReportFromDom(report).getValidationConclusion().getSignatureForm());
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
