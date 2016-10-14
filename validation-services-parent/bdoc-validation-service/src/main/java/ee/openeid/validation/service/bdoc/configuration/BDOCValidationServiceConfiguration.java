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

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.validation.service.bdoc.signature.policy.BDOCSignaturePolicyService;
import ee.openeid.validation.service.bdoc.signature.policy.PolicyConfigurationWrapper;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.apache.commons.lang.StringUtils;
import org.digidoc4j.Configuration;
import org.digidoc4j.TSLCertificateSource;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties({
    BDOCSignaturePolicyProperties.class,
    BDOCValidationServiceProperties.class
})
public class BDOCValidationServiceConfiguration {

    @Bean
    public ConstraintLoadingSignaturePolicyService signaturePolicyService(BDOCSignaturePolicyProperties properties) {
        return new ConstraintLoadingSignaturePolicyService(properties);
    }

    @Bean
    public PolicyConfigurationWrapper policyConfiguration(TrustedListsCertificateSource trustedListSource, BDOCSignaturePolicyService bdocSignaturePolicyService) {
        Configuration configuration = new Configuration();
        ConstraintDefinedPolicy policy = bdocSignaturePolicyService.getPolicy(StringUtils.EMPTY);
        configuration.setValidationPolicy(bdocSignaturePolicyService.getAbsolutePath(policy.getName()));
        TSLCertificateSource tslCertificateSource = TSLUtils.createTSLFromTrustedCertSource(trustedListSource);
        configuration.setTSL(tslCertificateSource);
        return new PolicyConfigurationWrapper(configuration, policy);
    }
}
