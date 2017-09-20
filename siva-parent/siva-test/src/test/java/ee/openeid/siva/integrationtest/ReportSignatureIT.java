package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

@Category(IntegrationTest.class)
public class ReportSignatureIT extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }

    @Test
    public void validationReportSignatureInResponse() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf"))
                .then()
                .body("validationReportSignature", not(isEmptyOrNullString()));
    }

} 
