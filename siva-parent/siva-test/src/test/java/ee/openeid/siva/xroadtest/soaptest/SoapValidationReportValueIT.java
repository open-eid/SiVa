package ee.openeid.siva.xroadtest.soaptest;

import ee.openeid.siva.soaptest.SiVaSoapTests;
import ee.openeid.siva.xroadtest.configuration.XroadIntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

import static ee.openeid.siva.integrationtest.TestData.SOAP_VALIDATION_CONCLUSION_PREFIX;
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
        post(validationRequestForDocumentExtended(encodedString, "xroad-simple.asice", "XROAD", "POLv3")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.CommonName", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.SerialNumber", Matchers.is("70006317"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
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
        post(validationRequestForDocumentExtended(encodedString, "invalid-digest.asice", "XROAD", "POLv3")).
                then()
                .rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("0"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-FAILED"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Errors.Error[0].Content", Matchers.is("InvalidSignatureValue: Signature is not valid"))
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
