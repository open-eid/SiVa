package ee.openeid.siva.validation.document.builder;

import ee.openeid.siva.validation.document.ValidationDocument;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Use for testing purposes only
 */
@TestOnly
public class DummyValidationDocumentBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyValidationDocumentBuilder.class);

    private ValidationDocument validationDocument;

    private DummyValidationDocumentBuilder() {
        validationDocument = new ValidationDocument();
    }

    public static DummyValidationDocumentBuilder aValidationDocument() {
        return new DummyValidationDocumentBuilder();
    }

    public DummyValidationDocumentBuilder withDocument(String filePath) {
        try {
            Path documentPath = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
            validationDocument.setBytes(Files.readAllBytes(documentPath));
        } catch (IOException e) {
            LOGGER.warn("FAiled to load dummy validation document with error: {}", e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOGGER.warn("Dummy document URL is invalid and ended with error: {}", e.getMessage(), e);
        }

        return this;
    }

    public DummyValidationDocumentBuilder withName(String name) {
        validationDocument.setName(name);
        return this;
    }

    public ValidationDocument build() {
        return validationDocument;
    }
}
