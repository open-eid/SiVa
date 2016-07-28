package ee.openeid.validation.service.bdoc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.AbstractMap.SimpleEntry;

@ConfigurationProperties(prefix = "siva.bdoc.signaturePolicy")
public class BDOCSignaturePolicyProperties extends ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties {
    private static final String DEFAULT_BDOC_POLICY = "bdoc_constraint.xml";
    private static final Map<String, String> DEFAULT_BDOC_POLICIES = Collections.unmodifiableMap(Stream.of(
            new AbstractMap.SimpleEntry<>("EE", DEFAULT_BDOC_POLICY)
    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));

    private String defaultPolicy;
    private Map<String, String> policies = new HashMap<>();

    @PostConstruct
    public void init() {
        if (defaultPolicy == null) {
            setAbstractDefaultPolicy(DEFAULT_BDOC_POLICY);
        } else {
            setAbstractDefaultPolicy(defaultPolicy);
        }

        if (policies.isEmpty()) {
            setAbstractPolicies(DEFAULT_BDOC_POLICIES);
        } else {
            setAbstractPolicies(policies);
        }
    }
}
