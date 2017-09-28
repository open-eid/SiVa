package ee.openeid.siva.signature.exception;

public class SignatureServiceException extends RuntimeException {

    public SignatureServiceException(String message, Exception e) {
        super(message, e);
    }

    public SignatureServiceException(String message) {
        super(message);
    }

} 
