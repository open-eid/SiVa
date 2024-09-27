/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.ADES_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.QES_POLICY;


@Getter
@Setter
@ConfigurationProperties(prefix = "siva.timestamp.signature-policy")
public class TimeStampTokenSignaturePolicyProperties extends SignaturePolicyProperties<ConstraintDefinedPolicy> {

    private static final String TST_QES_CONSTRAINT = "tst_constraint_qes.xml";
    private static final String TST_ADES_CONSTRAINT = "tst_constraint_ades.xml";

    private String defaultPolicy;
    private List<ConstraintDefinedPolicy> policies = new ArrayList<>();

    @PostConstruct
    public void initPolicySettings() {
        if (defaultPolicy == null) {
            setDefaultPolicy(QES_POLICY.getName());
        }
        if (policies.isEmpty()) {
            setPolicies(getDefaultTimeStampPolicies());
        }
        setAbstractDefaultPolicy(getDefaultPolicy());
        setAbstractPolicies(getPolicies());
    }

    private List<ConstraintDefinedPolicy> getDefaultTimeStampPolicies() {
        return List.of(getAdesPolicy(), getQesPolicy());
    }

    private ConstraintDefinedPolicy getQesPolicy() {
        return createConstraintDefinedPolicy(QES_POLICY, TST_QES_CONSTRAINT);
    }

    private ConstraintDefinedPolicy getAdesPolicy() {
        return createConstraintDefinedPolicy(ADES_POLICY, TST_ADES_CONSTRAINT);
    }

    private ConstraintDefinedPolicy createConstraintDefinedPolicy(ValidationPolicy validationPolicy, String constraintPath) {
        ConstraintDefinedPolicy constraintDefinedPolicy = new ConstraintDefinedPolicy(validationPolicy);
        constraintDefinedPolicy.setConstraintPath(constraintPath);
        return constraintDefinedPolicy;
    }

}
