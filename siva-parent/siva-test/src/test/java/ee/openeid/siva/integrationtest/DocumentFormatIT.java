package ee.openeid.siva.integrationtest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class DocumentFormatIT extends SiVaRestTests {

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";

    /**
     *
     * TestCaseID: DocumentFormat-1
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of pdf document acceptance
     *
     * Expected Result: Pdf is accepted and correct signature validation is given
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     *
     */
    @Test
    public void PAdESDocumentShouldPass() {
        QualifiedReport report = postForReport("hellopades-pades-lt-sha256-sign.pdf");
        assertAllSignaturesAreValid(report);
        assertEquals("PAdES_BASELINE_LT", report.getSignatures().get(0).getSignatureFormat());
    }

    /**
     *
     * TestCaseID: DocumentFormat-2
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of bdoc document acceptance
     *
     * Expected Result: Bdoc is accepted and correct signature validation is given
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     */
    @Test
    public void AdESDocumentShouldPass() {
        QualifiedReport report = postForReport("Valid_IDCard_MobID_signatures.bdoc");
        assertAllSignaturesAreValid(report);
        assertEquals("XAdES_BASELINE_LT_TM", report.getSignatures().get(0).getSignatureFormat());
    }

    /**
     *
     * TestCaseID: DocumentFormat-3
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of txt document rejection
     *
     * Expected Result: Txt document is rejected and proper error message is given
     *
     * File: hellopades-pades-lt-sha256-sign.txt
     *
     */
    @Test
    public void TxtDocumentShouldFail() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.txt"))
                .then()
                .body("requestErrors[0].key", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid document type"));
    }

    /**
     *
     * TestCaseID: DocumentFormat-4
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of p7s document rejection
     *
     * Expected Result: P7s document is rejected and proper error message is given
     *
     * File: hellocades.p7s
     *
     */
    @Test
    public void CAdESDocumentShouldFail() {
        post(validationRequestFor("hellocades.p7s"))
                .then()
                .body("requestErrors[0].key", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid document type"));
    }

    /**
     *
     * TestCaseID: DocumentFormat-5
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of zip document rejection
     *
     * Expected Result: Zip document is rejected and proper error message is given
     *
     * File: 42.zip
     *
     */
    @Test
    public void ZipDocumentShouldFail() {
        post(validationRequestFor("42.zip"))
                .then()
                .body("requestErrors[0].key", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid document type"));
    }

    /**
     *
     * TestCaseID: DocumentFormat-6
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of doc document rejection
     *
     * Expected Result: Doc document is rejected and proper error message is given
     *
     * File: hellopades-pades-lt-sha256-sign.doc
     *
     */
    @Test
    public void DocDocumentShouldFail() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.doc"))
                .then()
                .body("requestErrors[0].key", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid document type"));
    }

    /**
     *
     * TestCaseID: DocumentFormat-7
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of XML document rejection
     *
     * Expected Result: XML document is rejected and proper error message is given
     *
     * File: XML.xml
     *
     */
    @Test
    public void XmlDocumentShouldFail() {
        post(validationRequestFor("XML.xml"))
                .then()
                .body("requestErrors[0].key", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid document type"));
    }

    /**
     *
     * TestCaseID: DocumentFormat-8
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Validation of png document rejection
     *
     * Expected Result: Png document is rejected and proper error message is given
     *
     * File: Picture.png
     *
     */

    @Test
    public void PngDocumentShouldFail() {
        post(validationRequestFor("Picture.png"))
                .then()
                .body("requestErrors[0].key", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("Invalid document type"));
    }
    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
