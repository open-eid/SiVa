package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XRoadValidationPassIT extends SiVaRestTests {

    @Override
    protected String getTestFilesDirectory() {
        return "xroad/";
    }

    /**
     * TestCaseID: Xroad-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Bdoc with conformant EE signature
     *
     * Expected Result: Document should pass when signature policy is set to "ee"
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void validatingAnXroadDocumentShouldReturnAReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        QualifiedReport report = mapToReport(
                post(validationRequestWithValidKeys(encodedString, "xroad-simple.asice", "xroad", ""))
                        .body()
                        .asString()
        );
        assertAllSignaturesAreValid(report);
    }

}
