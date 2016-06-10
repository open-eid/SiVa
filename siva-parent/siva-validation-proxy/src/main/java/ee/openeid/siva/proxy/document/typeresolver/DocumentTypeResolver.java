package ee.openeid.siva.proxy.document.typeresolver;

import ee.openeid.siva.proxy.document.DocumentType;

import java.util.Optional;

import static java.util.Arrays.stream;

public final class DocumentTypeResolver {

    private DocumentTypeResolver() {}

    public static DocumentType documentTypeFromString(String type) {
        Optional<DocumentType> documentType = stream(DocumentType.class.getEnumConstants())
                .filter(dt -> dt.name().equalsIgnoreCase(type))
                .findAny();

        if (!documentType.isPresent()) {
            throw new UnsupportedTypeException("type = " + type + " is unsupported");
        }
        return documentType.get();
    }

}
