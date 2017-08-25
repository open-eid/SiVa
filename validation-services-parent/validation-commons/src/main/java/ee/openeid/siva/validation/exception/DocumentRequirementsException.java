package ee.openeid.siva.validation.exception;

public class DocumentRequirementsException extends RuntimeException {

    private static final String MESSAGE = "Document does not meet the requirements";

    public DocumentRequirementsException(Exception e) {
        super(MESSAGE, e);
    }

    public DocumentRequirementsException() {
        super(MESSAGE);
    }
}


