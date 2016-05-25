package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DdocValidationPass extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "ddoc/live/timemark/";

    /***
     * TestCaseID: Ddoc-ValidationPass-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with single valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: 23734-ddoc13-13basn1.ddoc
     ***/
    @Test
    public void validSingleSignature() {
        assertAllSignaturesAreValid(postForReport("23734-ddoc13-13basn1.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationPass-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: igasugust1.1.ddoc
     ***/
    @Test
    public void validMultipleSignatures() {
        assertAllSignaturesAreValid(postForReport("igasugust1.1.ddoc"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
