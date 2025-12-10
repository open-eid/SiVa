/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Slf4j
@Configuration
@EnableScheduling
public class TSLLoaderConfiguration {

    @Profile("test")
    @Bean
    public TSLValidationKeystoreProperties tslValidationKeystorePropertiesTest() {
        TSLValidationKeystoreProperties configurationProperties = new TSLValidationKeystoreProperties();
        configurationProperties.setFile(new ClassPathResource("test-siva-keystore.jks", getClass().getClassLoader()));
        return configurationProperties;
    }

    @Profile("!test")
    @Bean
    public TSLValidationKeystoreProperties tslValidationKeystorePropertiesProd() {
        TSLValidationKeystoreProperties configurationProperties = new TSLValidationKeystoreProperties();
        configurationProperties.setFile(new ClassPathResource("siva-keystore.jks", getClass().getClassLoader()));
        return configurationProperties;
    }

    @Bean
    public KeyStoreCertificateSource dssKeyStore(TSLValidationKeystoreProperties keystoreProperties) {
        final Resource resource = keystoreProperties.getFile();
        log.info("Loading {} trust-store from {}", keystoreProperties.getType(), resource);
        try (InputStream inputStream = resource.getInputStream()) {
            final char[] password = keystoreProperties.getPassword().toCharArray();
            return new KeyStoreCertificateSource(inputStream, keystoreProperties.getType(), password);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open LOTL trust-store from " + resource, e);
        }
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
