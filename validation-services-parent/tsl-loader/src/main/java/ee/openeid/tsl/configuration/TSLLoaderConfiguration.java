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

package ee.openeid.tsl.configuration;

import ee.openeid.tsl.keystore.DSSKeyStoreFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;

@Configuration
@EnableScheduling
public class TSLLoaderConfiguration {

    @Profile("test")
    @Bean
    public TSLValidationKeystoreProperties tslValidationKeystorePropertiesTest() {
        TSLValidationKeystoreProperties configurationProperties = new TSLValidationKeystoreProperties();
        configurationProperties.setFilename("test-siva-keystore.jks");
        return configurationProperties;
    }

    @Profile("!test")
    @Bean
    public TSLValidationKeystoreProperties tslValidationKeystorePropertiesProd() {
        return new TSLValidationKeystoreProperties();
    }

    @Bean
    public DSSKeyStoreFactoryBean dssKeyStore(TSLValidationKeystoreProperties keystoreProperties) {
        DSSKeyStoreFactoryBean dssKeyStoreFactoryBean = new DSSKeyStoreFactoryBean();
        dssKeyStoreFactoryBean.setKeyStoreType(keystoreProperties.getType());
        dssKeyStoreFactoryBean.setKeyStoreFilename(keystoreProperties.getFilename());
        dssKeyStoreFactoryBean.setKeyStorePassword(keystoreProperties.getPassword());
        return dssKeyStoreFactoryBean;
    }

    @Profile("test")
    @Bean
    public TSLLoaderConfigurationProperties tslLoaderConfigurationPropertiesTest() {
        TSLLoaderConfigurationProperties configurationProperties = new TSLLoaderConfigurationProperties();
        configurationProperties.setUrl("https://open-eid.github.io/test-TL/tl-mp-test-EE.xml");
        configurationProperties.setTrustedTerritories(new ArrayList<>());
        return configurationProperties;
    }

    @Profile("!test")
    @Bean
    public TSLLoaderConfigurationProperties tslLoaderConfigurationPropertiesProd() {
        return new TSLLoaderConfigurationProperties();
    }

}
