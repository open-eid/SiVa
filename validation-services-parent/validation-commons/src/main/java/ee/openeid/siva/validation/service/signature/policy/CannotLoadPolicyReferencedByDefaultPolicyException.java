package ee.openeid.siva.validation.service.signature.policy;

public class CannotLoadPolicyReferencedByDefaultPolicyException extends InvalidPolicyException {

    public CannotLoadPolicyReferencedByDefaultPolicyException(String defaultPolicyName) {
        super("The policy referenced by default policy: " + defaultPolicyName + " cannot be loaded");
    }
}
