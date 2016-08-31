package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.xroad.configuration.XROADValidationServiceProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class XROADTestUtils {

    static final String TEST_FILES_LOCATION = "test-files/";
    static final String XROAD_SIMPLE = "xroad-simple.asice";
    static final String XROAD_BATCHSIGNATURE = "xroad-batchsignature.asice";

    static ValidationDocument buildValidationDocument(String testFile) throws Exception {
        String fileLocation = TEST_FILES_LOCATION + testFile;
        ValidationDocument document = DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(fileLocation)
                .withName(testFile)
                .build();
        document.setDataBase64Encoded(Base64.encodeBase64String(IOUtils.toByteArray(getFileStream(fileLocation))));
        return document;
    }

    private static InputStream getFileStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }

    static XROADValidationService initializeXROADValidationService() {
        XROADValidationServiceProperties properties = new XROADValidationServiceProperties();
        properties.setDefaultPath();
        XROADValidationService validationService = new XROADValidationService();
        validationService.setProperties(properties);
        validationService.loadXroadConfigurationDirectory();
        return validationService;
    }

}
