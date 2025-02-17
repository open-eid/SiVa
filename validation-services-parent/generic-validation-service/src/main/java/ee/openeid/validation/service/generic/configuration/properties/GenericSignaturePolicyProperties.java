/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.configuration.properties;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "siva.europe.signature-policy")
public class GenericSignaturePolicyProperties extends SignaturePolicyProperties<ConstraintDefinedPolicy> {

    private static final String ADES_CONSTRAINT = "generic_constraint_ades.xml";
    private static final String QES_CONSTRAINT = "generic_constraint_qes.xml";

    private String defaultPolicy;
    private List<ConstraintDefinedPolicy> policies = new ArrayList<>();

    @PostConstruct
    public void initPolicySettings() {
        setPolicyValue();
        setPoliciesValue();
    }

    private void setPoliciesValue() {
        List<ConstraintDefinedPolicy> abstractPolicies = policies.isEmpty() ? getDefaultPdfPolicies() : policies;
        setAbstractPolicies(abstractPolicies);
    }

    private List<ConstraintDefinedPolicy> getDefaultPdfPolicies() {
        return Collections.unmodifiableList(Stream.of(getAdesPolicy(),  getQesPolicy()).collect(Collectors.toList()));
    }

    private void setPolicyValue() {
        final String policyName = defaultPolicy == null ? QES_POLICY.getName() : defaultPolicy;
        setAbstractDefaultPolicy(policyName);
    }

    private ConstraintDefinedPolicy getQesPolicy() {
        return createConstraintDefinedPolicy(QES_POLICY, QES_CONSTRAINT);
    }

    private ConstraintDefinedPolicy getAdesPolicy() {
        return createConstraintDefinedPolicy(ADES_POLICY, ADES_CONSTRAINT);
    }


    private ConstraintDefinedPolicy createConstraintDefinedPolicy(ValidationPolicy validationPolicy, String constraintPath) {
        ConstraintDefinedPolicy constraintDefinedPolicy = new ConstraintDefinedPolicy(validationPolicy);
        constraintDefinedPolicy.setConstraintPath(constraintPath);
        return constraintDefinedPolicy;
    }
}
