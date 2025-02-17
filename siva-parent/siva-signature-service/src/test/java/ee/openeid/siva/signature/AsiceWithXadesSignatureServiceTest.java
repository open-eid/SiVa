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

package ee.openeid.siva.signature;

import ee.openeid.siva.signature.configuration.Pkcs12Properties;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import ee.openeid.siva.signature.exception.SignatureServiceException;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsiceWithXadesSignatureServiceTest {

    AsiceWithXadesSignatureService asiceSignatureService;

    @BeforeEach
    public void setUp() {

        SignatureServiceConfigurationProperties properties = new SignatureServiceConfigurationProperties();
        properties.setSignatureLevel("XAdES_BASELINE_B");
        properties.setTspUrl("http://demo.sk.ee/tsa");
        properties.setOcspUrl("http://demo.sk.ee/ocsp");
        Pkcs12Properties pkcs12Properties = new Pkcs12Properties();
        pkcs12Properties.setPath("classpath:sign_ECC_from_TEST_of_ESTEID2018.p12");
        pkcs12Properties.setPassword("1234");
        properties.setPkcs12(pkcs12Properties);
        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        asiceSignatureService = new AsiceWithXadesSignatureService(properties, trustedListsCertificateSource);
    }

    @Test
    void AsiceSignatureServiceNotConfiguredWithProperties_shouldThrowException() {
        asiceSignatureService = new AsiceWithXadesSignatureService(null, new TrustedListsCertificateSource());
        byte[] dataToSign = "Hello".getBytes(StandardCharsets.UTF_8);

        SignatureServiceException caughtException = assertThrows(
                SignatureServiceException.class, () -> asiceSignatureService.getSignature(dataToSign, "hello.txt", "application/text")
        );
        assertEquals("Signature configuration properties not set!", caughtException.getMessage());
    }

    @Test
    void AsiceSignatureServiceNotConfiguredWithPkcs12Properties_shouldThrowException() {
        asiceSignatureService.getProperties().setPkcs12(null);
        byte[] dataToSign = "Hello".getBytes(StandardCharsets.UTF_8);

        SignatureServiceException caughtException = assertThrows(
                SignatureServiceException.class, () -> asiceSignatureService.getSignature(dataToSign, "hello.txt", "application/text")
        );
        assertEquals("Either Pkcs11 or Pkcs12 must be configured! Currently there is none configured..", caughtException.getMessage());

    }

    @Test
    void AsiceSignatureServiceConfiguredWithInvalidPkcs12Keystore_shouldThrowException() {
        asiceSignatureService.getProperties().getPkcs12().setPath("classpath:invalid.p12");
        byte[] dataToSign = "Hello".getBytes(StandardCharsets.UTF_8);

        SignatureServiceException caughtException = assertThrows(
                SignatureServiceException.class, () -> asiceSignatureService.getSignature(dataToSign, "hello.txt", "application/text")
        );
        assertEquals("Error reading keystore from path: classpath:invalid.p12", caughtException.getMessage());
    }

    @Test
    void AsiceSignatureServiceConfiguredWithInvalidPkcs12Password_shouldThrowException() {
        asiceSignatureService.getProperties().getPkcs12().setPassword("invalid password");
        byte[] dataToSign = "Hello".getBytes(StandardCharsets.UTF_8);

        DSSException caughtException = assertThrows(
                DSSException.class, () -> asiceSignatureService.getSignature(dataToSign, "hello.txt", "application/text")
        );
        assertEquals("Unable to instantiate KeyStoreSignatureTokenConnection", caughtException.getMessage());
    }

    @Test
    void AsiceSignatureServiceConfiguredWithInvalidSignatureLevel_shouldThrowException() {
        asiceSignatureService.getProperties().setSignatureLevel("SOME_INVALID_LEVEL");
        byte[] dataToSign = "Hello".getBytes(StandardCharsets.UTF_8);

        SignatureServiceException caughtException = assertThrows(
                SignatureServiceException.class, () -> asiceSignatureService.getSignature(dataToSign, "hello.txt", "application/text")
        );
        assertThat(caughtException.getMessage(), Matchers
                .startsWith("Invalid signature level - 'SOME_INVALID_LEVEL'! Valid signature levels:"));
    }

    @Test
    void AsiceSignatureServiceConfiguredCorrectly_withXadesBaselineProfileB_shouldPass() throws IOException {
        byte[] signature = asiceSignatureService.getSignature("Hello".getBytes(), "hello.txt", "application/text");
        assertNotNull(signature);
    }

} 
