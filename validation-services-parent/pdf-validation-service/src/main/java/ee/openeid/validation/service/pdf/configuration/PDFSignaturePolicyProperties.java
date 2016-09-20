package ee.openeid.validation.service.pdf.configuration;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.NO_TYPE_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.QES_POLICY;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "siva.pdf.signaturePolicy")
public class PDFSignaturePolicyProperties extends SignaturePolicyProperties<ConstraintDefinedPolicy> {

    private static final String NO_TYPE_CONSTRAINT = "pdf_constraint_no_type.xml";
    private static final String QES_CONSTRAINT = "pdf_constraint_qes.xml";

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
        return Collections.unmodifiableList(Stream.of(getNoTypePolicy(), getQesPolicy()).collect(Collectors.toList()));
    }

    private void setPolicyValue() {
        final String policyName = defaultPolicy == null ? NO_TYPE_POLICY.getName() : defaultPolicy;
        setAbstractDefaultPolicy(policyName);
    }

    private ConstraintDefinedPolicy getQesPolicy() {
        return createConstraintDefinedPolicy(QES_POLICY, QES_CONSTRAINT);
    }

    private ConstraintDefinedPolicy getNoTypePolicy() {
        return createConstraintDefinedPolicy(NO_TYPE_POLICY, NO_TYPE_CONSTRAINT);
    }

    private ConstraintDefinedPolicy createConstraintDefinedPolicy(ValidationPolicy validationPolicy, String constraintPath) {
        ConstraintDefinedPolicy constraintDefinedPolicy = new ConstraintDefinedPolicy(validationPolicy);
        constraintDefinedPolicy.setConstraintPath(constraintPath);
        return constraintDefinedPolicy;
    }
}
