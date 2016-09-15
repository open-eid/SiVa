package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

@Getter @Setter
public class SignaturePolicyService<T extends ValidationPolicy> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignaturePolicyService.class);

    private T defaultPolicy;
    private Map<String, T> signaturePolicies = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public SignaturePolicyService(SignaturePolicyProperties<T> signaturePolicyProperties) {
        LOGGER.info("Loading signature abstractPolicies for: " + signaturePolicyProperties.getClass().getSimpleName());
        loadSignaturePolicies(signaturePolicyProperties);
    }

    public T getPolicy(String policyName) {
        T policy = StringUtils.isEmpty(policyName) ? defaultPolicy : signaturePolicies.get(policyName);
        if (policy == null) {
            throw new InvalidPolicyException(policyName, signaturePolicies.keySet());
        }
        return policy;
    }

    void loadSignaturePolicies(SignaturePolicyProperties<T> signaturePolicyProperties) {
        signaturePolicyProperties.getAbstractPolicies().forEach(policy -> {
            LOGGER.info("Loading policy: " + policy);
            ValidationPolicy existingPolicyData = signaturePolicies.putIfAbsent(policy.getName(), policy);
            if (existingPolicyData == null) {
                LOGGER.info("Policy: " + policy + " loaded successfully");
            } else {
                LOGGER.error("Policy: " + policy + " was not loaded as it already exists");
            }
        });
        loadDefaultPolicy(signaturePolicyProperties);
    }

    void loadDefaultPolicy(SignaturePolicyProperties<T> signaturePolicyProperties) {
        String defaultPolicyName = signaturePolicyProperties.getAbstractDefaultPolicy();
        defaultPolicy = signaturePolicies.get(defaultPolicyName);
        if (defaultPolicy == null) {
            if (policiesContainDefaultPolicyReference(signaturePolicyProperties)) {
                throw new CannotLoadPolicyReferencedByDefaultPolicyException(defaultPolicyName);
            }
            throw new DefaultPolicyNotDefinedException();
        }
    }

    boolean policiesContainDefaultPolicyReference(SignaturePolicyProperties<T> signaturePolicyProperties) {
        return signaturePolicyProperties.getAbstractPolicies()
                .stream()
                .filter(pol -> StringUtils.equals(pol.getName(), signaturePolicyProperties.getAbstractDefaultPolicy()))
                .count() != 0;
    }
}
