/*
 * Copyright 2017 - 2022 Riigi Infosüsteemide Amet
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
import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Configuration;
import org.digidoc4j.ExternalConnectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties({
        BDOCSignaturePolicyProperties.class,
        BDOCValidationServiceProperties.class
})
public class TimemarkContainerValidationServiceConfiguration {

    private final TSLLoaderConfigurationProperties tslLoaderConfigurationProperties;
    private final BDOCValidationServiceProperties bdocValidationServiceProperties;

    @Autowired
    public TimemarkContainerValidationServiceConfiguration(TSLLoaderConfigurationProperties tslLoaderConfigurationProperties,
                                                           BDOCValidationServiceProperties bdocValidationServiceProperties) {
        this.tslLoaderConfigurationProperties = tslLoaderConfigurationProperties;
        this.bdocValidationServiceProperties = bdocValidationServiceProperties;
    }

    @Bean(name = "timemarkPolicyService")
    public ConstraintLoadingSignaturePolicyService timemarkSignaturePolicyService(BDOCSignaturePolicyProperties properties) {
        return new ConstraintLoadingSignaturePolicyService(properties);
    }

    @Bean
    @Profile("test")
    public Configuration testDigiDoc4JConfiguration() {
        Configuration configuration = createConfiguration(Configuration.Mode.TEST);
        configuration.loadConfiguration(getAdditionalConfigurationFilePath("/siva-digidoc4j-test.yaml"), true);
        configuration.setTrustedTerritories();
        return configuration;
    }

    @Bean
    @Profile("!test")
    public Configuration prodDigiDoc4JConfiguration() {
        Configuration configuration = createConfiguration(Configuration.Mode.PROD);
        configuration.loadConfiguration(getAdditionalConfigurationFilePath("/siva-digidoc4j.yaml"), true);
        configuration.setTrustedTerritories(tslLoaderConfigurationProperties.getTrustedTerritories().toArray(String[]::new));
        return configuration;
    }

    private Configuration createConfiguration(Configuration.Mode mode) {
        Configuration configuration = new Configuration(mode);
        configuration.setSslTruststorePathFor(ExternalConnectionType.TSL, tslLoaderConfigurationProperties.getSslTruststorePath());
        configuration.setSslTruststorePasswordFor(ExternalConnectionType.TSL, tslLoaderConfigurationProperties.getSslTruststorePassword());
        configuration.setSslTruststoreTypeFor(ExternalConnectionType.TSL, tslLoaderConfigurationProperties.getSslTruststoreType());
        configuration.setLotlLocation(tslLoaderConfigurationProperties.getUrl());
        return configuration;
    }

    private String getAdditionalConfigurationFilePath(String defaultFilePath) {
        String filePath = StringUtils.defaultString(bdocValidationServiceProperties.getDigidoc4JConfigurationFile(), defaultFilePath);
        return new ClassPathResource(filePath, this.getClass().getClassLoader()).getPath();
    }

}
