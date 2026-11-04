/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.configuration.AIALoaderConfiguration;
import ee.openeid.siva.validation.configuration.AIALoaderProperties;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.validator.AIASourceFactory;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.tsl.configuration.TSLLoaderDefinitionRegisterer;
import ee.openeid.validation.service.timemark.configuration.TimemarkContainerValidationServiceConfiguration;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.BDOCSignaturePolicyService;
import ee.openeid.validation.service.timemark.tsl.TimemarkTrustedListsCertificateSource;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.CertificateExtensionsUtils;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static ee.openeid.validation.service.timemark.BDOCTestUtils.buildValidationDocument;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        TSLLoaderConfiguration.class,
        TSLLoaderDefinitionRegisterer.class,
        TSLRefresher.class,
        TSLValidationJobFactory.class,
        TimemarkTrustedListsCertificateSource.class,
        TimemarkContainerValidationServiceConfiguration.class,
        TimemarkContainerValidationService.class,
        BDOCSignaturePolicyService.class,
        ConstraintLoadingSignaturePolicyService.class,
        BDOCConfigurationService.class,
        ReportConfigurationProperties.class,
        ProxyConfig.class,
        AIALoaderConfiguration.class
})
@ActiveProfiles("test")
class TimemarkAIASourceTest {

    private static final String CONTAINER_WITH_FILE_AIA_URL = "xades-signature-certificate-aia-url-file-dev-zero.bdoc";
    private static final String FORBIDDEN_AIA_URL = "file:///dev/zero";

    @MockitoBean
    private AIASourceFactory aiaSourceFactory;
    @Autowired
    private AIALoaderProperties aiaLoaderProperties;
    @Autowired
    private ProxyConfig proxyConfig;
    @Autowired
    private TimemarkContainerValidationService validationService;

    @Test
    void validateDocument_WhenCertificateAiaUrlIsFileUrl_DoesNotFetchIt() {
        DefaultAIASource configuredSource = (DefaultAIASource) new AIALoaderConfiguration()
                .aiaSourceFactory(aiaLoaderProperties, proxyConfig).create();
        DataLoader dataLoader = mock(DataLoader.class);
        configuredSource.setDataLoader(dataLoader);
        AIASource aiaSource = spy(configuredSource);
        when(aiaSourceFactory.create()).thenReturn(aiaSource);

        Reports reports = assertDoesNotThrow(
                () -> validationService.validateDocument(buildValidationDocument(CONTAINER_WITH_FILE_AIA_URL)));

        verify(aiaSource, atLeastOnce()).getCertificatesByAIA(argThat(certificate ->
                certificate != null && CertificateExtensionsUtils.getCAIssuersAccessUrls(certificate)
                        .contains(FORBIDDEN_AIA_URL)));
        verify(dataLoader, never()).get(FORBIDDEN_AIA_URL);
        assertThat(reports.getSimpleReport().getValidationConclusion().getSignaturesCount(), is(1));
    }
}
