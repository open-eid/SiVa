package ee.openeid.siva.integrationtest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.Matchers.equalTo;

@Category(IntegrationTest.class)
public class LargeFileTests extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "large_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /***
     * TestCaseID: PDF-LargeFiles-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: 9MB PDF files (PAdES Baseline LT).
     *
     * Expected Result: Validation report is returned
     *
     * File: scout_x4-manual-signed_lt_9mb.pdf
     ***/
    @Test
    public void pdfNineMegabyteFilesWithLtSignatureAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("scout_x4-manual-signed_lt_9mb.pdf"));
        post(validationRequestWithValidKeys(encodedString, "scout_x4-manual-signed_lt_9mb.pdf", "pdf",""))
                .then()
                .body("signatures[0].signatureFormat",equalTo("PAdES_BASELINE_LT"))
                .body("documentName",equalTo("scout_x4-manual-signed_lt_9mb.pdf"));
    }

    /***
     * TestCaseID: PDF-LargeFiles-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Large signed PDF files (PAdES Baseline LT).
     *
     * Expected Result: Bigger documents with valid signature should pass
     *
     * File: scout_x4-manual-signed_lt_9mb.pdf
     ***/
    @Test
    public void bdocEightMegabyteFilesWithLtSignatureAreAccepted() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("EE_SER-AEX-B-LT-V-44_8mb.asice"));
        post(validationRequestWithValidKeys(encodedString, "EE_SER-AEX-B-LT-V-44_8mb.asice", "bdoc",""))
                .then()
                .body("signatures[0].signatureFormat",equalTo("XAdES_BASELINE_LT"))
                .body("documentName",equalTo("EE_SER-AEX-B-LT-V-44_8mb.asice"));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
