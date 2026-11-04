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

package ee.openeid.siva.validation.validator;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import ee.openeid.siva.validation.configuration.AIALoaderConfiguration;
import ee.openeid.siva.validation.configuration.AIALoaderProperties;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.Protocol;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.unit.DataSize;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

class AIASourceFactoryTest {

    private static final List<Protocol> HTTP_AND_HTTPS = List.of(Protocol.HTTP, Protocol.HTTPS);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    private static KeyPair keyPair;
    private static byte[] issuerCertificate;

    private String baseUrl;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .failOnUnmatchedRequests(true)
            .build();

    @BeforeAll
    static void createIssuerCertificate() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
        issuerCertificate = certificate(null).getEncoded();
    }

    @BeforeEach
    void setUp() {
        baseUrl = wireMock.baseUrl();
    }

    @Test
    void create_WhenCalledTwice_ReturnsNewInstances() {
        AIASourceFactory factory = createFactory(HTTP_AND_HTTPS, issuerCertificate.length, READ_TIMEOUT);

        assertThat(factory.create(), not(sameInstance(factory.create())));
    }

    @Test
    void getCertificatesByAIA_WhenResponseIsExactlyMaxSize_ReturnsIssuer() throws Exception {
        stubFor(get("/issuer.crt.pem").willReturn(ok().withBody(issuerCertificate)));
        AIASource aiaSource = createFactory(HTTP_AND_HTTPS, issuerCertificate.length, READ_TIMEOUT).create();

        Set<CertificateToken> certificates = aiaSource.getCertificatesByAIA(certificate(baseUrl + "/issuer.crt.pem"));

        assertThat(certificates, hasSize(1));
        assertThat(certificates.iterator().next().getEncoded(), is(issuerCertificate));
    }

    @Test
    void getCertificatesByAIA_WhenFileUrl_ReturnsEmpty(@TempDir Path tempDir) throws Exception {
        String url = writeIssuerFile(tempDir);
        AIASource aiaSource = createFactory(HTTP_AND_HTTPS, issuerCertificate.length, READ_TIMEOUT).create();

        assertThat(aiaSource.getCertificatesByAIA(certificate(url)), empty());
    }

    @Test
    void getCertificatesByAIA_WhenFileProtocolIsAccepted_ReturnsIssuer(@TempDir Path tempDir) throws Exception {
        String url = writeIssuerFile(tempDir);
        AIASource aiaSource = createFactory(List.of(Protocol.FILE), issuerCertificate.length, READ_TIMEOUT).create();

        assertThat(aiaSource.getCertificatesByAIA(certificate(url)), hasSize(1));
    }

    @Test
    void getCertificatesByAIA_WhenResponseIsOverMaxSize_ReturnsEmpty() throws Exception {
        stubFor(get("/issuer.crt.pem").willReturn(ok().withBody(issuerCertificate)));
        AIASource aiaSource = createFactory(HTTP_AND_HTTPS, issuerCertificate.length - 1, READ_TIMEOUT).create();

        assertThat(aiaSource.getCertificatesByAIA(certificate(baseUrl + "/issuer.crt.pem")), empty());
    }

    @Test
    void getCertificatesByAIA_WhenResponseIsSlowerThanReadTimeout_ReturnsEmpty() throws Exception {
        stubFor(get("/slow/issuer.crt.pem").willReturn(ok().withBody(issuerCertificate).withFixedDelay(2000)));
        AIASource aiaSource = createFactory(HTTP_AND_HTTPS, issuerCertificate.length, Duration.ofMillis(200)).create();

        assertThat(aiaSource.getCertificatesByAIA(certificate(baseUrl + "/slow/issuer.crt.pem")), empty());
    }

    private static String writeIssuerFile(Path directory) throws Exception {
        return Files.write(directory.resolve("issuer.crt.pem"), issuerCertificate).toUri().toString();
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

    private static AIASourceFactory createFactory(List<Protocol> acceptedProtocols, int maxResponseSize, Duration readTimeout) {
        AIALoaderProperties properties = new AIALoaderProperties();
        properties.setAcceptedProtocols(acceptedProtocols);
        properties.setConnectTimeout(Duration.ofSeconds(1));
        properties.setReadTimeout(readTimeout);
        properties.setMaxResponseSize(DataSize.ofBytes(maxResponseSize));
        return new AIALoaderConfiguration().aiaSourceFactory(properties, new ProxyConfig());
    }
}
