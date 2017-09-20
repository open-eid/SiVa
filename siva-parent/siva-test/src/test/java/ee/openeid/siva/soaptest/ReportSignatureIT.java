package ee.openeid.siva.soaptest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class ReportSignatureIT extends SiVaSoapTests {

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(TEST_FILES_DIRECTORY);
    }

    @Test
    public void validationReportSignatureInResponse() {
        Document report = extractValidateDocumentResponseDom(post(validationRequestForDocument("hellopades-pades-lt-sha256-sign.pdf")).andReturn().body().asString());
        Assert.assertThat(getValidateDocumentResponseFromDom(report).getValidationReportSignature(), not(isEmptyOrNullString()));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
