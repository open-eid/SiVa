package ee.openeid.siva.validation.document.builder;

import ee.openeid.siva.validation.document.ValidationDocument;
import eu.europa.esig.dss.MimeType;
import org.apache.commons.lang.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Use for testing purposes only
 */
public class DummyValidationDocumentBuilder {

    private ValidationDocument validationDocument;

    private DummyValidationDocumentBuilder() {
        validationDocument = new ValidationDocument();
    }

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
        validationDocument.setMimeType(parseFileExtension(name));
        return this;
    }

    private MimeType parseFileExtension(final String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equalsIgnoreCase(fileExtension, "pdf")) {
            return MimeType.PDF;
        } else if (StringUtils.equalsIgnoreCase(fileExtension, "bdoc") || StringUtils.equalsIgnoreCase(fileExtension, "asice")) {
            return MimeType.ASICE;
        } else if (StringUtils.equalsIgnoreCase(fileExtension, "ddoc")) {
            return MimeType.XML;
        }

        return MimeType.fromFileName(filename);

    }

    public ValidationDocument build() {
        return validationDocument;
    }
}
