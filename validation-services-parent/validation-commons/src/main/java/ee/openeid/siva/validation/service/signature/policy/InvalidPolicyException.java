package ee.openeid.siva.validation.service.signature.policy;

import java.util.Set;

public class InvalidPolicyException extends RuntimeException {

    public InvalidPolicyException(String message) {
        super(message);
    }

    public InvalidPolicyException(String policyName, Set<String> availablePolicies) {
        this("Invalid signature policy: " + policyName + "; Available abstractPolicies: " + availablePolicies );
    }
}
