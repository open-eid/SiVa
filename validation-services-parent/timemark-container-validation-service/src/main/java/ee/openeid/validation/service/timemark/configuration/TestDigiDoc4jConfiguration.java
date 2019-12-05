package ee.openeid.validation.service.timemark.configuration;


import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import org.digidoc4j.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestDigiDoc4jConfiguration {

    private TSLLoaderConfigurationProperties tslLoaderConfigurationProperties;

    @Bean
    private org.digidoc4j.Configuration getConfiguration() {
        org.digidoc4j.Configuration configuration = new org.digidoc4j.Configuration(Configuration.Mode.TEST);
        configuration.setTrustedTerritories();
        configuration.setSslTruststorePath(tslLoaderConfigurationProperties.getSslTruststorePath());
        configuration.setSslTruststorePassword(tslLoaderConfigurationProperties.getSslTruststorePassword());
        configuration.setSslTruststoreType(tslLoaderConfigurationProperties.getSslTruststoreType());
        configuration.setTslLocation(tslLoaderConfigurationProperties.getUrl());
        return configuration;
    }

    @Autowired
    public void setTslLoaderConfigurationProperties(TSLLoaderConfigurationProperties tslLoaderConfigurationProperties) {
        this.tslLoaderConfigurationProperties = tslLoaderConfigurationProperties;
    }

}

