/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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
import ee.openeid.validation.service.timemark.tsl.TimemarkTrustedListsCertificateSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_POLICY_SERVICE_BEAN_NAME;
import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME;

@RequiredArgsConstructor
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties({
        BDOCSignaturePolicyProperties.class,
        BDOCValidationServiceProperties.class
})
public class TimemarkContainerValidationServiceConfiguration {

    private final @NonNull BDOCValidationServiceProperties bdocValidationServiceProperties;

    @Bean(name = TM_POLICY_SERVICE_BEAN_NAME)
    public ConstraintLoadingSignaturePolicyService timemarkSignaturePolicyService(BDOCSignaturePolicyProperties properties) {
        return new ConstraintLoadingSignaturePolicyService(properties);
    }

    @Bean
    @Profile("test")
    public Configuration testDigiDoc4JConfiguration(
            @Qualifier(TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME) TimemarkTrustedListsCertificateSource trustedListsCertificateSource
    ) {
        Configuration configuration = Configuration.of(Configuration.Mode.TEST);
        configuration.loadConfiguration(getAdditionalConfigurationFilePath("/siva-digidoc4j-test.yaml"), true);
        configuration.setTSL(trustedListsCertificateSource);
        return configuration;
    }

    @Bean
    @Profile("!test")
    public Configuration prodDigiDoc4JConfiguration(
            @Qualifier(TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME) TimemarkTrustedListsCertificateSource trustedListsCertificateSource
    ) {
        Configuration configuration = Configuration.of(Configuration.Mode.PROD);
        configuration.loadConfiguration(getAdditionalConfigurationFilePath("/siva-digidoc4j.yaml"), true);
        configuration.setTSL(trustedListsCertificateSource);
        return configuration;
    }

    private String getAdditionalConfigurationFilePath(String defaultFilePath) {
        String filePath = StringUtils.defaultString(bdocValidationServiceProperties.getDigidoc4JConfigurationFile(), defaultFilePath);
        return new ClassPathResource(filePath, this.getClass().getClassLoader()).getPath();
    }

}
