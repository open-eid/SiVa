package ee.openeid.siva.integrationtest;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Created by julia on 19.04.2017.
 */


public class DdocGetDataFilesIT  extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/get_data_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-1
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:Ddoc with data file used.
     *
     * Expected Result: The data file should be returned.
     *
     * File:ddoc_1_3.xml.ddoc
     */

    @Test
    public void getDataFileFromDdoc(){

        postForDataFiles(dataFilesRequest("ddoc_1_3.xml.ddoc"))
                .then()
                .body("dataFiles[0].fileName",Matchers.is("test2007.txt"))
                .body("dataFiles[0].mimeType",Matchers.is("text/plain"))
                .body("dataFiles[0].base64",Matchers.is("dGVzdA==\n\n"))
                .body("dataFiles[0].size",Matchers.is(4));


    }

    /**
     * TestCaseID:  Ddoc-Get-Data-Files-2
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:Bdoc with data file used.
     *
     * Expected Result: The error message  should be returned.
     *
     * File:BDOC-TS.bdoc
     */

    @Test
    public void getDataFileFromBdocShouldFail(){

        setTestFilesDirectory("bdoc/live/timestamp/");
        postForDataFiles(dataFilesRequest("BDOC-TS.bdoc"))
                .then()
                .body("requestErrors[0].message",Matchers.is(INVALID_DOCUMENT_TYPE_DDOC))
                .body("requestErrors[0].key",Matchers.is(DOCUMENT_TYPE));

    }

    /**
     * TestCaseID:  Ddoc-Get-Data-Files-3
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:Pdf file used.
     *
     * Expected Result: The error message  should be returned.
     *
     * File:hellopades-lt-b.pdf
     * */
    @Test
    public void getDataFileFromPdfShouldFail(){

        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        postForDataFiles(dataFilesRequest("hellopades-lt-b.pdf"))
                .then()
                .body("requestErrors[0].message",Matchers.is(INVALID_DOCUMENT_TYPE_DDOC))
                .body("requestErrors[0].key",Matchers.is(DOCUMENT_TYPE));

    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
