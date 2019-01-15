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

package ee.openeid.validation.service.timemark.configuration;

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import ee.openeid.validation.service.timemark.XMLEntityAttackValidator;
import ee.openeid.validation.service.timemark.signature.policy.BDOCSignaturePolicyService;
import ee.openeid.validation.service.timemark.signature.policy.PolicyConfigurationWrapper;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties({
    BDOCSignaturePolicyProperties.class,
    BDOCValidationServiceProperties.class,
    DDOCValidationServiceProperties.class,
    XMLEntityAttackValidator.class
})
public class TimemarkContainerValidationServiceConfiguration {

    @Autowired
    private TSLLoaderConfigurationProperties tslLoaderConfigurationProperties;

    @Bean(name = "timemarkPolicyService")
    public ConstraintLoadingSignaturePolicyService timemarkSignaturePolicyService(BDOCSignaturePolicyProperties properties) {
        return new ConstraintLoadingSignaturePolicyService(properties);
    }

    @Bean
    public PolicyConfigurationWrapper policyConfiguration(BDOCSignaturePolicyService bdocSignaturePolicyService, Configuration configuration) {
        configuration.setTslLocation(tslLoaderConfigurationProperties.getUrl());
        ConstraintDefinedPolicy policy = bdocSignaturePolicyService.getPolicy(StringUtils.EMPTY);
        configuration.setValidationPolicy(bdocSignaturePolicyService.getAbsolutePath(policy.getName()));
        return new PolicyConfigurationWrapper(configuration, policy);
    }
}
