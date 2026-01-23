/*
 * Copyright 2024 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;
import ee.openeid.validation.service.timestamptoken.configuration.TimeStampTokenSignaturePolicyProperties;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {BaseTimeStampTokenValidationServiceTest.TestConfiguration.class})
abstract class BaseTimeStampTokenValidationServiceTest {
    TimeStampTokenValidationService validationService;
    final TimeStampTokenSignaturePolicyProperties policyProperties = new TimeStampTokenSignaturePolicyProperties();

    @Autowired
    TrustedListsCertificateSource trustedListsCertificateSource;

    @BeforeEach
    void setUp() {
        validationService = new TimeStampTokenValidationService();
        policyProperties.initPolicySettings();
        ConstraintLoadingSignaturePolicyService signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policyProperties);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);
        ReportConfigurationProperties reportConfigurationProperties = new ReportConfigurationProperties(true);
        validationService.setReportConfigurationProperties(reportConfigurationProperties);
    }

    static ValidationDocument buildValidationDocument(String testFile) {
        return TestUtils.buildValidationDocument(TestUtils.TEST_FILES_LOCATION, testFile);
    }

    @Import({
            TSLLoaderConfiguration.class,
            GenericValidationServiceConfiguration.class
    })
    public static class TestConfiguration {

        @Bean
        public ProxyConfig proxyConfig() {
            return new ProxyConfig();
        }

        @Bean
        public TSLLoader tslLoader() {
            return new TSLLoader("testName");
        }

        @Bean
        public TSLRefresher tslRefresher() {
            return new TSLRefresher();
        }

        @Bean
        public TSLValidationJobFactory tslValidationJobFactory() {
            return new TSLValidationJobFactory();
        }
    }
}
