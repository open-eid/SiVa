package ee.openeid.validation.service.bdoc.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.validation.service.bdoc.configuration.BDOCSignaturePolicyProperties;
import org.apache.commons.lang.StringUtils;
import org.digidoc4j.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class BDOCConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BDOCConfigurationService.class);
    private final Map<String, Configuration> policyList = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

    private Configuration configuration;
    private BDOCSignaturePolicyProperties properties;
    private BDOCSignaturePolicyService policyService;

    @PostConstruct
    private void loadAllBDOCConfigurations() {
        properties.getAbstractPolicies().entrySet().forEach(e -> {
            Configuration tempConfiguration = configuration.copy();
            tempConfiguration.setValidationPolicy(policyService.getAbsolutePath(e.getKey()));

            LOGGER.info("Adding BDOC validation policy: {}", e.getKey());
            policyList.putIfAbsent(e.getKey(), tempConfiguration);
        });
    }

    public Configuration loadConfiguration(String signaturePolicy) {
        return StringUtils.isEmpty(signaturePolicy) ? configuration : loadExistingPolicy(signaturePolicy);
    }

    private Configuration loadExistingPolicy(String policyName) {
        if (!policyList.containsKey(policyName)) {
            throw new InvalidPolicyException(policyName, policyList.keySet());
        }

        return policyList.get(policyName);
    }

    @Autowired
    public void setPolicyService(BDOCSignaturePolicyService policyService) {
        this.policyService = policyService;
    }

    @Autowired
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Autowired
    public void setProperties(BDOCSignaturePolicyProperties properties) {
        this.properties = properties;
    }
}
