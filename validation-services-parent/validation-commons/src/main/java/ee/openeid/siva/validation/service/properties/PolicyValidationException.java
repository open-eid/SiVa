package ee.openeid.siva.validation.service.properties;

public class PolicyValidationException extends RuntimeException {
    PolicyValidationException(Exception e) {
        super(e);
    }
}
