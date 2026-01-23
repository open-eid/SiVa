/*
 * Copyright 2025 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.generic.configuration.GenericValidationServiceConfiguration;
import ee.openeid.validation.service.generic.configuration.properties.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.configuration.properties.TLevelSignatureFilterProperties;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPSourceFactory;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {GenericValidationServiceTestBase.TestConfiguration.class})
abstract class GenericValidationServiceTestBase {

    protected static final String TEST_FILES_LOCATION = "test-files/";

    protected GenericValidationService validationService;

    protected ConstraintLoadingSignaturePolicyService signaturePolicyService;
    @Autowired
    protected TrustedListsCertificateSource trustedListsCertificateSource;
    @Autowired
    protected GenericSignaturePolicyProperties policySettings;
    @Autowired
    protected ContainerValidatorFactory containerValidatorFactory;
    @Autowired
    protected OCSPSourceFactory ocspSourceFactory;

    @BeforeEach
    protected void setUp() {
        validationService = new GenericValidationService();
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        validationService.setContainerValidatorFactory(containerValidatorFactory);
        validationService.setOcspSourceFactory(ocspSourceFactory);
    }

    protected static ValidationDocument buildValidationDocument(String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

    @Import({
            // t-level properties must be explicitly imported before GenericValidationServiceConfiguration
            //  for the conditional CountryFilter bean to be initialized correctly, if configured.
            TLevelSignatureFilterProperties.class,

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
