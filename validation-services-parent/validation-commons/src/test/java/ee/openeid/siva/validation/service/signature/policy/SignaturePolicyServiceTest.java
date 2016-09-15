package ee.openeid.siva.validation.service.signature.policy;


import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SignaturePolicyServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenSignaturePolicesDoNotContainDefaultPolicyThenThrowException() {
        expectedException.expect(DefaultPolicyNotDefinedException.class);
        ValidationPolicy pol1 = createValidationPolicy("pol1", "desc1");
        SignaturePolicyService<ValidationPolicy> signaturePolicyService = createSignaturePolicyService("RANDOM_PREFIX" + "pol1", pol1);
        signaturePolicyService.getPolicy("pol1");
    }

    @Test
    public void gettingSignaturePoliciesReturnsAllPoliciesWithUniqueNames() {
        ValidationPolicy pol1 = createValidationPolicy("pol1", "desc1");
        ValidationPolicy pol2 = createValidationPolicy("pol2", "desc2");
        ValidationPolicy pol3 = createValidationPolicy("pol2", "desc3");
        SignaturePolicyService<ValidationPolicy> signaturePolicyService = createSignaturePolicyService("pol1", pol1, pol2, pol3);
        assertTrue(signaturePolicyService.getSignaturePolicies().size() == 2);
        assertEquals(pol1, signaturePolicyService.getSignaturePolicies().get("pol1"));
        assertEquals(pol2, signaturePolicyService.getSignaturePolicies().get("pol2"));
        assertEquals("desc2", signaturePolicyService.getSignaturePolicies().get("pol2").getDescription());
    }

    @Test
    public void gettingPolicyWithoutSpecifyingNameReturnsDefaultPolicy() {
        ValidationPolicy pol1 = createValidationPolicy("pol1", "desc1");
        ValidationPolicy pol2 = createValidationPolicy("pol2", "desc2");
        SignaturePolicyService<ValidationPolicy> signaturePolicyService = createSignaturePolicyService("pol1", pol1, pol2);
        assertEquals(pol1, signaturePolicyService.getDefaultPolicy());
        assertEquals(pol1, signaturePolicyService.getPolicy(""));
    }

    private ValidationPolicy createValidationPolicy(String pol1, String description) {
        return new ValidationPolicy(pol1, description, null);
    }

    private SignaturePolicyService<ValidationPolicy> createSignaturePolicyService(String defaultPolicy, ValidationPolicy... policyPaths) {
        List<ValidationPolicy> policies = new ArrayList<>();
        stream(policyPaths).forEach(policies::add);
        SignaturePolicyProperties<ValidationPolicy> signaturePolicyProperties = new SignaturePolicyProperties<>();
        signaturePolicyProperties.setAbstractPolicies(policies);
        signaturePolicyProperties.setAbstractDefaultPolicy(defaultPolicy);
        return new SignaturePolicyService<>(signaturePolicyProperties);
    }
}
