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

    private final Map<String, PolicyConfigurationWrapper> policyList = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
    private PolicyConfigurationWrapper policyConfiguration;
    private BDOCSignaturePolicyProperties properties;
    private BDOCSignaturePolicyService policyService;

    @PostConstruct
    private void loadAllBDOCConfigurations() {
        properties.getAbstractPolicies().forEach(policy -> {
            Configuration tempConfiguration = policyConfiguration.getConfiguration().copy();
            tempConfiguration.setValidationPolicy(policyService.getAbsolutePath(policy.getName()));
            LOGGER.info("Adding BDOC validation policy: {}", policy.getName());
            policyList.putIfAbsent(policy.getName(), new PolicyConfigurationWrapper(tempConfiguration, policy));
        });
    }

    public PolicyConfigurationWrapper loadPolicyConfiguration(String policyName) {
        return StringUtils.isEmpty(policyName) ? policyConfiguration : loadExistingPolicy(policyName);
    }

    private PolicyConfigurationWrapper loadExistingPolicy(String policyName) {
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
    public void setConfiguration(PolicyConfigurationWrapper policyConfiguration) {
        this.policyConfiguration = policyConfiguration;
    }

    @Autowired
    public void setProperties(BDOCSignaturePolicyProperties properties) {
        this.properties = properties;
    }
}
