/*
 * Copyright 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.annotation.LoadableTsl;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles("test")
@SpringBootTest(classes = {
        TSLLoaderDefinitionRegistererSpringBootTest.ConfigurationForTesting.class,
        TSLLoaderDefinitionRegisterer.class,
        TSLLoaderConfiguration.class,
        TSLValidationJobFactory.class
})
class TSLLoaderDefinitionRegistererSpringBootTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void loadApplicationContext_WhenLoadableTslBeansExist_TslLoaderBeansForEachTslAreCreated() {
        Map<String, TSLLoader> tslLoaderBeans = applicationContext.getBeansOfType(TSLLoader.class);

        assertThat(tslLoaderBeans, aMapWithSize(3));
        assertThat(tslLoaderBeans, hasEntry(equalTo("tslCertificateSourceWithAnnotationLoader"), notNullValue()));
        assertThat(tslLoaderBeans.get("tslCertificateSourceWithAnnotationLoader").getTslName(), equalTo("test-tsl-name"));
        assertThat(tslLoaderBeans, hasEntry(equalTo("shortNameLoader"), notNullValue()));
        assertThat(tslLoaderBeans.get("shortNameLoader").getTslName(), equalTo("another-tsl-name"));
    }

    @TestConfiguration
    static class ConfigurationForTesting {

        @Bean
        TrustedListsCertificateSource tslCertificateSourceWithoutAnnotation() {
            return new TrustedListsCertificateSource();
        }

        @Bean
        @LoadableTsl(name = "test-tsl-name")
        TrustedListsCertificateSource tslCertificateSourceWithAnnotation() {
            return new TrustedListsCertificateSource();
        }

        @Bean(name = "shortName")
        @LoadableTsl(name = "another-tsl-name")
        TrustedListsCertificateSource anotherTslCertificateSourceWithAnnotation() {
            return new TrustedListsCertificateSource();
        }

        @Bean
        ProxyConfig proxyConfig() {
            return new ProxyConfig();
        }

    }

}
