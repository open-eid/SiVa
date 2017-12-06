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
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
