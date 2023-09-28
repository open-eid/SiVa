/*
 * Copyright 2017 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.validation.service.timemark.configuration.BDOCSignaturePolicyProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
@Component
public class BDOCConfigurationService {

    private final BDOCSignaturePolicyProperties properties;
    private final BDOCSignaturePolicyService policyService;
    private final Configuration configuration;

    private final Map<String, PolicyConfigurationWrapper> policyList = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

    @Autowired
    public BDOCConfigurationService(BDOCSignaturePolicyService policyService,
            BDOCSignaturePolicyProperties properties,
            Configuration configuration) {
        this.policyService = policyService;
        this.properties = properties;
        this.configuration = configuration;
    }

    @PostConstruct
    protected void loadAllBDOCConfigurations() {
        properties.getAbstractPolicies().forEach(policy -> {
            Configuration tempConfiguration = configuration.copy();
            tempConfiguration.setTSL(configuration.getTSL()); // Use TSL from the central configuration bean
            tempConfiguration.setValidationPolicy(policyService.getAbsolutePath(policy.getName()));
            log.info("Adding BDOC validation policy: {}", policy.getName());
            policyList.putIfAbsent(policy.getName(), new PolicyConfigurationWrapper(tempConfiguration, policy));
        });
    }

    public PolicyConfigurationWrapper loadPolicyConfiguration(String policyName) {
        return getExistingOrDefaultPolicy(policyName);
    }

    private PolicyConfigurationWrapper getExistingOrDefaultPolicy(String policyName) {
        return StringUtils.isEmpty(policyName) ?
                loadExistingPolicy(properties.getDefaultPolicy()) :
                loadExistingPolicy(policyName);
    }

    private PolicyConfigurationWrapper loadExistingPolicy(String policyName) {
        if (!policyList.containsKey(policyName)) {
            throw new InvalidPolicyException(policyName, policyList.keySet());
        }
        return policyList.get(policyName);
    }

}
