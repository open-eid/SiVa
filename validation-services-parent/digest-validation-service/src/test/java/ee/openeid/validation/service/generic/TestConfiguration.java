package ee.openeid.validation.service.generic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;

@Configuration
@Import({TSLLoaderConfiguration.class, GenericValidationServiceConfiguration.class, ReportConfigurationProperties.class})
public class TestConfiguration {

    @Bean
    public DigestValidationService digestValidationService() {
        return new DigestValidationService();
    }

}
