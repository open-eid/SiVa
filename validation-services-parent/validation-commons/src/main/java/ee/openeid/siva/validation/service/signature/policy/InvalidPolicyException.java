package ee.openeid.siva.validation.service.signature.policy;

public class InvalidPolicyException extends RuntimeException {
    InvalidPolicyException(String message) {
        super(message);
    }
}
