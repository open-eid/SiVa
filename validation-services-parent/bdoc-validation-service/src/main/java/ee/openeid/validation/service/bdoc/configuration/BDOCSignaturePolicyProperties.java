/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.bdoc.configuration;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
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

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.NO_TYPE_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.QES_POLICY;

@Getter @Setter
@ConfigurationProperties(prefix = "siva.bdoc.signaturePolicy")
public class BDOCSignaturePolicyProperties extends SignaturePolicyProperties<ConstraintDefinedPolicy> {

    private static final String QES_BDOC_CONSTRAINT = "bdoc_constraint_qes.xml";
    private static final String NO_TYPE_BDOC_CONSTRAINT = "bdoc_constraint_no_type.xml";

    private String defaultPolicy;
    private List<ConstraintDefinedPolicy> policies = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (defaultPolicy == null) {
            setDefaultPolicy(NO_TYPE_POLICY.getName());
        }
        if (policies.isEmpty()) {
            setPolicies(getDefaultBdocPolicies());
        }
        setAbstractDefaultPolicy(getDefaultPolicy());
        setAbstractPolicies(getPolicies());
    }

    private List<ConstraintDefinedPolicy> getDefaultBdocPolicies() {
        return Collections.unmodifiableList(Stream.of(getNoTypePolicy(), getQesPolicy()).collect(Collectors.toList()));
    }

    private ConstraintDefinedPolicy getQesPolicy() {
        return createConstraintDefinedPolicy(QES_POLICY, QES_BDOC_CONSTRAINT);
    }

    private ConstraintDefinedPolicy getNoTypePolicy() {
        return createConstraintDefinedPolicy(NO_TYPE_POLICY, NO_TYPE_BDOC_CONSTRAINT);
    }

    private ConstraintDefinedPolicy createConstraintDefinedPolicy(ValidationPolicy validationPolicy, String constraintPath) {
        ConstraintDefinedPolicy constraintDefinedPolicy = new ConstraintDefinedPolicy(validationPolicy);
        constraintDefinedPolicy.setConstraintPath(constraintPath);
        return constraintDefinedPolicy;
    }
}
