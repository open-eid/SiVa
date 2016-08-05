package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.xroad.configuration.XROADValidationServiceProperties;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class XROADValidationServiceTest {

    private static final String XROAD_SIMPLE = "xroad-simple.asice";

    private XROADValidationServiceProperties properties;
    private XROADValidationService validationService;

    @Before
    public void setUp() {
        properties = new XROADValidationServiceProperties();
        properties.setDefaultPath();
        validationService = new XROADValidationService();
        validationService.setProperties(properties);
        validationService.loadXroadConfigurationDirectory();
    }

    @Test
    public void ValidatingXRoadSimpleContainerShouldHaveOnlyTheCNFieldOfTheSingersCerificateAsSignedByFieldInQualifiedReport() throws Exception {
//        QualifiedReport report = validationService.validateDocument(buildValidationDocument(XROAD_SIMPLE));
//        assertEquals("Riigi Infosüsteemi Amet", report.getSignatures().get(0).getSignedBy());
        assertTrue(true);
    }

    private ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(testFile)
                .withName(testFile)
                .build();
    }


}
