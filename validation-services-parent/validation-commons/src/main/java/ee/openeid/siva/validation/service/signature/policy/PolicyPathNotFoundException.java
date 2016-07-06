package ee.openeid.siva.validation.service.signature.policy;

public class PolicyPathNotFoundException extends RuntimeException {
    PolicyPathNotFoundException(String message) {
        super(message);
    }
}
