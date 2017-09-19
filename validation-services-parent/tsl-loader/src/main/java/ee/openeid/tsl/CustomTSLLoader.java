package ee.openeid.tsl;


import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("customTslLoader")
@Profile("test")
public class CustomTSLLoader extends TSLLoader{
    private List<String> testTrustedTerritories =  Arrays.asList("BE", "BG", "CY", "CZ","DE","DK", "EE", "ES", "FI", "FR", "GR", "HU","HR","IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT","NO","NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK");

    @Override
    @Autowired
    public void setTslLoaderConfigurationProperties(TSLLoaderConfigurationProperties configurationProperties) {
        configurationProperties.setTrustedTerritories(testTrustedTerritories);
        super.setTslLoaderConfigurationProperties(configurationProperties);
    }
}
