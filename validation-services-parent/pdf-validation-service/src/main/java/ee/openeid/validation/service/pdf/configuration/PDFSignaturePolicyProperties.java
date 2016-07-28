package ee.openeid.validation.service.pdf.configuration;

import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties( prefix = "siva.pdf.signaturePolicy")
public class PDFSignaturePolicyProperties extends SignaturePolicyProperties {
    protected static final String DEFAULT_PDF_POLICY = "pdf_constraint.xml";
    private static final Map<String, String> DEFAULT_PDF_POLICIES = Collections.unmodifiableMap(Stream.of(
            new SimpleEntry<>("EE", DEFAULT_PDF_POLICY)
    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));

    private String defaultPolicy;
    private Map<String, String> policies = new HashMap<>();

    @PostConstruct
    public void initPolicySettings() {
        getPolicyValue();
        getPoliciesValue();
    }

    private void getPoliciesValue() {
        Map<String, String> policiesMap = policies.isEmpty() ? DEFAULT_PDF_POLICIES : policies;
        setAbstractPolicies(policiesMap);
    }

    private void getPolicyValue() {
        final String policyName = defaultPolicy == null ? DEFAULT_PDF_POLICY : defaultPolicy;
        setAbstractDefaultPolicy(policyName);
    }
}
