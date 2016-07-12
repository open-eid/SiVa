package ee.openeid.siva.validation.service.signature.policy.properties;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class SignaturePolicySettings {
    private String abstractDefaultPolicy;
    private Map<String, String> abstractPolicies = new HashMap<>();
}
