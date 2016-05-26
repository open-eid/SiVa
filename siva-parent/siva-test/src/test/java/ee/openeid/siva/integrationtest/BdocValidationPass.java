package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BdocValidationPass extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    /***
     * TestCaseID: Bdoc-ValidationPass-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with single valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_ID_sig.bdoc
     ***/
    @Test
    public void validSignature() {
        assertAllSignaturesAreValid(postForReport("Valid_ID_sig.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationPass-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     ***/
    @Test
    public void validMultipleSignatures() {
        assertAllSignaturesAreValid(postForReport("Valid_IDCard_MobID_signatures.bdoc"));
    }

    /***
     * TestCaseID: Bdoc-ValidationPass-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Bdoc with warning on signature
     *
     * Expected Result: The document should pass the validation but warning should be returned
     *
     * File: 23147_weak-warning-sha1.bdoc
     ***/
    @Test @Ignore //TODO: VAL-207
    public void validSignatureWithWarning() {
        assertAllSignaturesAreValid(postForReport("23147_weak-warning-sha1.bdoc"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
