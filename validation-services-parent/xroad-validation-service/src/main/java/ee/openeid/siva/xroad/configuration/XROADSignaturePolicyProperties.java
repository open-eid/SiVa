package ee.openeid.siva.xroad.configuration;


import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.NO_TYPE_POLICY;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "siva.xroad.signaturePolicy")
public class XROADSignaturePolicyProperties extends SignaturePolicyProperties<ValidationPolicy> {

    private String defaultPolicy;
    private List<ValidationPolicy> policies = new ArrayList<>();

    @PostConstruct
    public void initPolicySettings() {
        setPolicyValue();
        setPoliciesValue();
    }

    private void setPoliciesValue() {
        List<ValidationPolicy> abstractPolicies = policies.isEmpty() ? getDefaultDdocPolicies() : policies;
        setAbstractPolicies(abstractPolicies);
    }

    private List<ValidationPolicy> getDefaultDdocPolicies() {
        return Collections.singletonList(NO_TYPE_POLICY);
    }

    private void setPolicyValue() {
        final String policyName = defaultPolicy == null ? NO_TYPE_POLICY.getName() : defaultPolicy;
        setAbstractDefaultPolicy(policyName);
    }


}
