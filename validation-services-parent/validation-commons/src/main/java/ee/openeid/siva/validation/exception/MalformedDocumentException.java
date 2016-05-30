package ee.openeid.siva.validation.exception;


public class MalformedDocumentException extends RuntimeException {
    private static final String MESSAGE = "the document is malformed";

    public MalformedDocumentException(Exception e) {
        super(MESSAGE, e);
    }

    public MalformedDocumentException() {
        super(MESSAGE);
    }
}
