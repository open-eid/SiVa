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

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import ee.openeid.siva.validation.configuration.AIALoaderConfiguration;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.tsl.configuration.TSLLoaderDefinitionRegisterer;
import ee.openeid.validation.service.timemark.configuration.TimemarkContainerValidationServiceConfiguration;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.BDOCSignaturePolicyService;
import ee.openeid.validation.service.timemark.tsl.TimemarkTrustedListsCertificateSource;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.digidoc4j.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
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

    private static KeyPair keyPair;

    @Autowired
    private BDOCConfigurationService bdocConfigurationService;

    @BeforeAll
    static void createIssuerCertificate() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
    }

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Test
    void aiaSource_WhenResponseExceedsMaxSize_StopsReading(CapturedOutput output) throws Exception {
        wireMock.stubFor(get("/ca.crt.pem").willReturn(ok().withBody(new byte[1024 * 1024 + 1])));

        assertThat(aiaSource().getCertificatesByAIA(certificate(wireMock.baseUrl() + "/ca.crt.pem")), empty());
        assertThat(output.getAll(), containsString("Cannot fetch data limit=1048576"));
    }

    private AIASource aiaSource() {
        Configuration configuration = bdocConfigurationService.loadPolicyConfiguration(null).getConfiguration();
        return new org.digidoc4j.impl.AiaSourceFactory(configuration).create();
    }

    private static CertificateToken certificate(String caIssuersUrl) throws Exception {
        X500Name name = new X500Name("CN=Test");
        Instant now = Instant.now();
        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                name, BigInteger.ONE, Date.from(now.minus(Duration.ofDays(1))), Date.from(now.plus(Duration.ofDays(1))),
                name, keyPair.getPublic()
        );
        if (caIssuersUrl != null) {
            builder.addExtension(Extension.authorityInfoAccess, false, new AuthorityInformationAccess(new AccessDescription(
                    AccessDescription.id_ad_caIssuers, new GeneralName(GeneralName.uniformResourceIdentifier, caIssuersUrl))));
        }
        byte[] encoded = builder.build(new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate())).getEncoded();
        return DSSUtils.loadCertificate(encoded);
    }
}
