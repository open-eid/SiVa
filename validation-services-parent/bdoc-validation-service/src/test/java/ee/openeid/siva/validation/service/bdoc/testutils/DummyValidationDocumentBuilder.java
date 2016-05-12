package ee.openeid.siva.validation.service.bdoc.testutils;

import ee.openeid.siva.validation.document.ValidationDocument;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DummyValidationDocumentBuilder {

    private ValidationDocument validationDocument;

    public static DummyValidationDocumentBuilder aValidationDocument() {
        return new DummyValidationDocumentBuilder();
    }

    public DummyValidationDocumentBuilder withDocument(String filePath) throws Exception {
        Path documentPath = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
        validationDocument.setBytes(Files.readAllBytes(documentPath));
        return this;
    }

    public DummyValidationDocumentBuilder withName(String name) throws Exception {
        validationDocument.setName(name);
        return this;
    }

    private DummyValidationDocumentBuilder() {
        validationDocument = new ValidationDocument();
    }

    public ValidationDocument build() {
        return validationDocument;
    }
}
