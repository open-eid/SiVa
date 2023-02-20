/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.validation.service.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignaturePolicyServiceTest {

    @Test
    public void whenSignaturePolicesDoNotContainDefaultPolicyThenThrowException() {
        assertThrows(
            DefaultPolicyNotDefinedException.class, () -> {
                ValidationPolicy pol1 = createValidationPolicy("pol1", "desc1");
                SignaturePolicyService<ValidationPolicy> signaturePolicyService = createSignaturePolicyService("RANDOM_PREFIX" + "pol1", pol1);
                signaturePolicyService.getPolicy("pol1");
            }
        );
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
