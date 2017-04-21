package ee.openeid.siva.soaptest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;

/**
 * Created by julia on 20.04.2017.
 */

@Category(IntegrationTest.class)
public class SoapValidationRequestForDataFilesIT extends SiVaSoapTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/get_data_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


    //empty values
    @Test
    public void soapValidationRequestForDataFilesEmptyInputs() {
        postDataFiles(validationRequestForDocumentDataFilesExtended("", ""))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("Envelope.Body.Fault.faultcode", Matchers.is(CLIENT_FAULT))
                .body("Envelope.Body.Fault.faultstring",Matchers.is(DOCUMENT_NOT_BASE64));
    }

    //document type changed to bdoc
    @Test
    public void soapValidationRequestForDataFIlesDocumentTypeChangedToBdoc() {
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("18912.ddoc"));
        postDataFiles(validationRequestForDocumentDataFilesExtended(encodedString, "bdoc"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("Envelope.Body.Fault.faultcode",Matchers.is(CLIENT_FAULT))
                .body("Envelope.Body.Fault.faultstring",Matchers.is(INVALID_DOCUMENT_TYPE_DDOC));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
