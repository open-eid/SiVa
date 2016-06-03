package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DdocValidationFail extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with single invalid signature
     *
     * Expected Result: The document should fail the validation
     *
     * File: test1-ddoc-revoked.ddoc
     ***/
    @Test
    public void ddocInvalidSignature() {
        assertAllSignaturesAreInvalid(postForReport("test1-ddoc-revoked.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with multiple invalid signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleInvalidSignatures.ddoc
     ***/
    @Test
    public void ddocInvalidMultipleSignatures() {
        assertAllSignaturesAreInvalid(postForReport("multipleInvalidSignatures.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with multiple signatures both valid and invalid
     *
     * Expected Result: The document should fail the validation
     *
     * File: multipleValidAndInvalidSignatures.ddoc
     ***/
    @Test
    public void ddocInvalidAndValidMultipleSignatures() {
        assertSomeSignaturesAreValid(postForReport("multipleValidAndInvalidSignatures.ddoc"),2);
    }

    /***
     * TestCaseID: Ddoc-ValidationFail-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with no signatures
     *
     * Expected Result: The document should fail the validation
     *
     * File: DdocContainerNoSignature.bdoc
     ***/
    @Test
    public void ddocNoSignatures() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreInvalid(postForReport("DdocContainerNoSignature.ddoc"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
