package ee.openeid.siva.validation.exception;

public class ValidationServiceException extends RuntimeException {
    public ValidationServiceException(String validatorName, Throwable cause) {
        super("an error occurred in validator " + validatorName, cause);
    }
}
