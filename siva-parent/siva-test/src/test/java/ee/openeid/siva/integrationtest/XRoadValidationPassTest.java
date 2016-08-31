package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XRoadValidationPassTest extends SiVaRestTests {

    @Override
    protected String getTestFilesDirectory() {
        return "xroad/";
    }

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
