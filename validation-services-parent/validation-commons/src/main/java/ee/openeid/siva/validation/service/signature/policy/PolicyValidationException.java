package ee.openeid.siva.validation.service.signature.policy;

public class PolicyValidationException extends RuntimeException {
    PolicyValidationException(Exception e) {
        super(e);
    }
}
