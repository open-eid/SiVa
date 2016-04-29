package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DocumentFormatTests extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";

    @Test
    public void PAdESDocumentShouldPass() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", "simple"))
                .then()
                .body("SimpleReport.ValidSignaturesCount", Matchers.is(1))
                .body("SimpleReport.Signature.SignatureFormat", Matchers.is("PAdES_BASELINE_LT"));

    }

    @Test
    public void CAdESDocumentShouldFail() {
        post(validationRequestFor("hellocades.p7s", "simple"))
                .then()
                .body("requestErrors[0].field", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("invalid documentType"));
    }

    @Test
    public void ZipDocumentShouldFail() {
        post(validationRequestFor("42.zip", "simple"))
                .then()
                .body("requestErrors[0].field", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("invalid documentType"));
    }

    @Test
    public void DocDocumentShouldFail() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.doc", "simple"))
                .then()
                .body("requestErrors[0].field", Matchers.is("documentType"))
                .body("requestErrors[0].message", Matchers.containsString("invalid documentType"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
