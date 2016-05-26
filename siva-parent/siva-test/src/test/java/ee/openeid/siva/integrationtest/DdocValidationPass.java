package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Ignore;
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
     * Title: Ddoc v1.0 with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: needfile1.0.ddoc
     ***/
    @Test @Ignore //TODO Need ddoc v1.0 file
    public void validMultipleSignaturesV10() {
        assertAllSignaturesAreValid(postForReport("needfile.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationPass-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc v1.1 with multiple valid signatures v1.1
     *
     * Expected Result: The document should pass the validation
     *
     * File: igasugust1.1.ddoc
     ***/
    @Test
    public void validMultipleSignaturesV11() {
        assertAllSignaturesAreValid(postForReport("igasugust1.1.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationPass-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc v1.2 with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: igasugust1.2.ddoc
     ***/
    @Test
    public void validMultipleSignaturesV12() {
        assertAllSignaturesAreValid(postForReport("igasugust1.2.ddoc"));
    }

    /***
     * TestCaseID: Ddoc-ValidationPass-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Ddoc v1.3 with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: igasugust1.3.ddoc
     ***/
    @Test
    public void validMultipleSignaturesV13() {
        assertAllSignaturesAreValid(postForReport("igasugust1.3.ddoc"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
