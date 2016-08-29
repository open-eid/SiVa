package ee.openeid.siva.validation.exception;


public class MalformedDocumentException extends RuntimeException {
    private static final String MESSAGE = "document malformed or not matching documentType";

    public MalformedDocumentException(Exception e) {
        super(MESSAGE, e);
    }

    public MalformedDocumentException() {
        super(MESSAGE);
    }
}
