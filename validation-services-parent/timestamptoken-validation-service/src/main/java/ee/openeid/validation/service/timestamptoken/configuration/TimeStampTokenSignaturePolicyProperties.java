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

package ee.openeid.validation.service.timestamptoken.configuration;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.ADES_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.QES_POLICY;


@Getter
@Setter
@ConfigurationProperties(prefix = "siva.timestamp.signature-policy")
public class TimeStampTokenSignaturePolicyProperties extends SignaturePolicyProperties<ValidationPolicy> {

    private String defaultPolicy;
    private List<ValidationPolicy> policies = new ArrayList<>();

    @PostConstruct
    public void initPolicySettings() {
        setPolicyValue();
        setPoliciesValue();
    }

    private void setPoliciesValue() {
        List<ValidationPolicy> abstractPolicies = getDefaultTimeStampPolicies();
        setAbstractPolicies(abstractPolicies);
    }

    private List<ValidationPolicy> getDefaultTimeStampPolicies() {
        return Collections.unmodifiableList(Stream.of(ADES_POLICY, QES_POLICY).collect(Collectors.toList()));
    }

    private void setPolicyValue() {
        final String policyName = defaultPolicy == null ? QES_POLICY.getName() : defaultPolicy;
        setAbstractDefaultPolicy(policyName);
    }


}
