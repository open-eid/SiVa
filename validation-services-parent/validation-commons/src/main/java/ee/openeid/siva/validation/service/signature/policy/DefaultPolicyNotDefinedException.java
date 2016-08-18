package ee.openeid.siva.validation.service.signature.policy;

public class DefaultPolicyNotDefinedException extends InvalidPolicyException {

    public DefaultPolicyNotDefinedException() {
        super("Default policy is not defined in signature abstractPolicies.");
    }
}
