package ee.openeid.siva.xroadtest.resttest;


import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.xroadtest.configuration.XroadIntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Category(XroadIntegrationTest.class)
public class ValidationReportValueVerificationIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Xroad-ValidationReportVerification-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xroad-simple container
     *
     * Expected Result: Report is returned with required elements
     *
     * File: xroad-simple.asice
     */
    @Test
    public void xroadAllElementsArePresentValidSimpleSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "xroad-simple.asice", "xroad", "POLv3"))
                .then()
                .rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaXroad.json"))
                .body("signatures[0].id", Matchers.is("signature"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].signedBy", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("70006317"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-04-27T12:15:42Z"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("xroad-simple.asice"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xroad-ValidationReportVerification-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xroad-batchsignature container
     *
     * Expected Result: Report is returned with required elements
     *
     * File: xroad-batchsignature.asice
     */
    @Test
    public void xroadAllElementsArePresentValidBatchSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-batchsignature.asice"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "xroad-batchsignature.asice", "xroad", "POLv3"))
                .then()
                .rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaXroad.json"))
                .body("signatures[0].id", Matchers.is("signature"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_BES"))
                .body("signatures[0].signedBy", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("70006317"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-04-27T12:26:53Z"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("signatureForm", Matchers.is("ASiC-E_batchsignature"))
                .body("validatedDocument.filename", Matchers.is("xroad-batchsignature.asice"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xroad-ValidationReportVerification-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xroad-attachment container
     *
     * Expected Result: Report is returned with required elements
     *
     * File: xroad-attachment.asice
     */
    @Test
    public void xroadAllElementsArePresentValidAttachmentSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-attachment.asice"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "xroad-attachment.asice", "xroad", "POLv3"))
                .then()
                .rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaXroad.json"))
                .body("signatures[0].id", Matchers.is("signature"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_BES"))
                .body("signatures[0].signatureLevel", Matchers.emptyOrNullString())
                .body("signatures[0].signedBy", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Riigi Infosüsteemi Amet"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("70006317"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-04-27T12:30:10Z"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("signatureForm", Matchers.is("ASiC-E_batchsignature"))
                .body("validatedDocument.filename", Matchers.is("xroad-attachment.asice"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xroad-ValidationReportVerification-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report with invalid xroad container
     *
     * Expected Result: Report is returned with required elements
     *
     * File: xroad-attachment.asice
     */
    @Test
    public void xroadAllElementsArePresentInvalidSignature() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("invalid-digest.asice"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "invalid-digest.asice", "xroad", "POLv3"))
                .then()
                .rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaXroad.json"))
                .body("signatures[0].id", Matchers.is("signature"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].errors.content", Matchers.hasItem("InvalidSignatureValue: Signature is not valid"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("invalid-digest.asice"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

}
