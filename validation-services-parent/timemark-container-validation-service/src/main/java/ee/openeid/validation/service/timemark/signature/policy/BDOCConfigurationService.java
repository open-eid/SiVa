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

package ee.openeid.validation.service.timemark.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.validation.service.timemark.configuration.BDOCSignaturePolicyProperties;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.apache.commons.lang3.StringUtils;
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

    private TrustedListsCertificateSource trustedListSource;

    @PostConstruct
    protected void loadAllBDOCConfigurations() {
        properties.getAbstractPolicies().forEach(policy -> {
            Configuration tempConfiguration = policyConfiguration.getConfiguration().copy();
            tempConfiguration.setValidationPolicy(policyService.getAbsolutePath(policy.getName()));
            LOGGER.info("Adding BDOC validation policy: {}", policy.getName());
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

    @Autowired
    public void setTrustedListSource(TrustedListsCertificateSource trustedListSource) {
        this.trustedListSource = trustedListSource;
    }
}
