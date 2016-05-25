package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DdocValidationFail extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "ddoc/live/timemark/";

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
     * File:
     ***/
    @Test
    public void InvalidSignature() {
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
     * File:
     ***/
    @Test @Ignore
    public void InvalidMultipleSignatures() {
        assertAllSignaturesAreInvalid(postForReport("needfile.ddoc"));
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
     * File:
     ***/
    @Test @Ignore
    public void InvalidAndValidMultipleSignatures() {
        assertSomeSignaturesAreValid(postForReport("needfile.ddoc"),1);
    }


    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
