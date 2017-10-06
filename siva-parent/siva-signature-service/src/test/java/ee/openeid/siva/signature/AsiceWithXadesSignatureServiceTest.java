package ee.openeid.siva.signature;

import ee.openeid.siva.signature.configuration.Pkcs12Properties;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import ee.openeid.siva.signature.exception.SignatureServiceException;
import eu.europa.esig.dss.DSSException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class AsiceWithXadesSignatureServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    AsiceWithXadesSignatureService asiceSignatureService;

    @Before
    public void setUp() {
        asiceSignatureService = new AsiceWithXadesSignatureService();
        SignatureServiceConfigurationProperties properties = new SignatureServiceConfigurationProperties();
        properties.setSignatureLevel("XAdES_BASELINE_B");
        properties.setTspUrl("http://demo.sk.ee/tsa");
        properties.setOcspUrl("http://demo.sk.ee/ocsp");
        Pkcs12Properties pkcs12Properties = new Pkcs12Properties();
        pkcs12Properties.setPath("classpath:test.p12");
        pkcs12Properties.setPassword("password");
        properties.setPkcs12(pkcs12Properties);
        asiceSignatureService.setProperties(properties);
    }

    @Test
    public void AsiceSignatureServiceNotConfiguredWithProperties_shouldThrowException() throws IOException {
        expectedException.expect(SignatureServiceException.class);
        expectedException.expectMessage("Signature configuration properties not set!");

        asiceSignatureService.setProperties(null);
        asiceSignatureService.getSignature("Hello".getBytes(), "hello.txt", "application/text");
    }

    @Test
    public void AsiceSignatureServiceNotConfiguredWithPkcs12Properties_shouldThrowException() throws IOException {
        expectedException.expect(SignatureServiceException.class);
        expectedException.expectMessage("Signature configuration properties not set!");

        asiceSignatureService.getProperties().setPkcs12(null);
        asiceSignatureService.getSignature("Hello".getBytes(), "hello.txt", "application/text");
    }

    @Test
    public void AsiceSignatureServiceConfiguredWithInvalidPkcs12Keystore_shouldThrowException() throws IOException {
        expectedException.expect(SignatureServiceException.class);
        expectedException.expectMessage("Error reading keystore from path: ");

        asiceSignatureService.getProperties().getPkcs12().setPath("classpath:invalid.p12");
        asiceSignatureService.getSignature("Hello".getBytes(), "hello.txt", "application/text");
    }

    @Test
    public void AsiceSignatureServiceConfiguredWithInvalidPkcs12Password_shouldThrowException() throws IOException {
        expectedException.expect(DSSException.class);
        expectedException.expectMessage("keystore password was incorrect");

        asiceSignatureService.getProperties().getPkcs12().setPassword("invalid password");
        asiceSignatureService.getSignature("Hello".getBytes(), "hello.txt", "application/text");
    }

    @Test
    public void AsiceSignatureServiceConfiguredWithInvalidSignatureLevel_shouldThrowException() throws IOException {
        expectedException.expect(SignatureServiceException.class);
        expectedException.expectMessage("Invalid signature level - 'SOME_INVALID_LEVEL'! Valid signature levels:");

        asiceSignatureService.getProperties().setSignatureLevel("SOME_INVALID_LEVEL");
        asiceSignatureService.getSignature("Hello".getBytes(), "hello.txt", "application/text");
    }

    @Test
    public void AsiceSignatureServiceConfiguredCorrectly_withXadesBaselineProfileB_shouldPass() throws IOException {
        byte[] signature = asiceSignatureService.getSignature("Hello".getBytes(), "hello.txt", "application/text");
        assertNotNull(signature);
    }

} 
