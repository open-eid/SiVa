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

package ee.openeid.tsl.configuration;

import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;

import ee.openeid.tsl.keystore.DSSKeyStoreFactoryBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({
        TSLLoaderConfigurationProperties.class,
        TSLValidationKeystoreProperties.class
})
public class TSLLoaderConfiguration {
    private TSLValidationKeystoreProperties keystoreProperties;

    @Bean
    public DSSKeyStoreFactoryBean dssKeyStore() {
        DSSKeyStoreFactoryBean dssKeyStoreFactoryBean = new DSSKeyStoreFactoryBean();
        dssKeyStoreFactoryBean.setKeyStoreType(keystoreProperties.getType());
        dssKeyStoreFactoryBean.setKeyStoreFilename(keystoreProperties.getFilename());
        dssKeyStoreFactoryBean.setKeyStorePassword(keystoreProperties.getPassword());
        return dssKeyStoreFactoryBean;
    }

    @Bean
    public TrustedListsCertificateSource trustedListSource(KeyStoreCertificateSource keyStoreCertificateSource) {
        return new TrustedListsCertificateSource();
    }

    @Autowired
    public void setKeystoreProperties(TSLValidationKeystoreProperties keystoreProperties) {
        this.keystoreProperties = keystoreProperties;
    }
}
