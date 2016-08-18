package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.xroad.configuration.XROADValidationServiceProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class XROADValidationServiceTest {

    private static final String XROAD_SIMPLE = "xroad-simple.asice";
    private static final String XROAD_BATCHSIGNATURE = "xroad-batchsignature.asice";

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
        ValidationDocument validationDocument = buildValidationDocument(XROAD_SIMPLE);

        validationDocument.setDataBase64Encoded(Base64.encodeBase64String(IOUtils.toByteArray(getFileStream(XROAD_SIMPLE))));
        QualifiedReport report = validationService.validateDocument(validationDocument);
        assertEquals("Riigi Infosüsteemi Amet", report.getSignatures().get(0).getSignedBy());
    }

    @Test
    public void validationReportForXroadBatchSignatureShouldHaveCorrectSignatureForm() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(XROAD_BATCHSIGNATURE);
        validationDocument.setDataBase64Encoded(Base64.encodeBase64String(IOUtils.toByteArray(getFileStream(XROAD_BATCHSIGNATURE))));

        QualifiedReport report = validationService.validateDocument(validationDocument);
        assertEquals("ASiC_E_batchsignature", report.getSignatureForm());
    }

    private ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(testFile)
                .withName(testFile)
                .build();
    }

    private InputStream getFileStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }
}
