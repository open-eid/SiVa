package ee.openeid.siva.validation.service.signature.policy;

public class InvalidPolicyException extends RuntimeException {
    public InvalidPolicyException(String message) {
        super(message);
    }
}
