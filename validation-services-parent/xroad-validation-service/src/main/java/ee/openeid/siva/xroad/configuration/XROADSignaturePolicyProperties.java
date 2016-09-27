/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
