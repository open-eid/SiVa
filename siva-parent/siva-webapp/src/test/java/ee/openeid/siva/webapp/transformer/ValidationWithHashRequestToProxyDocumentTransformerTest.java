package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.testutils.MockValidationWithHashRequestBuilder;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ValidationWithHashRequestToProxyDocumentTransformerTest {

    private static final String SIGNATURE_FILE = "test-files/signatures0.xml";
    private ValidationWithHashRequestToProxyDocumentTransformer transformer = new ValidationWithHashRequestToProxyDocumentTransformer();
    private MockValidationWithHashRequestBuilder.MockValidationWithHashRequest validationWithHashRequest;

    @Before
    public void setUp() throws Exception {
        setValidationWithHashRequest();
    }

    @Test
    public void filenameRemainsUnchanged() {
        ProxyDocument proxyDocument = transformer.transform(validationWithHashRequest);
        assertEquals(validationWithHashRequest.getFilename(), proxyDocument.getName());
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        ProxyDocument proxyDocument = transformer.transform(validationWithHashRequest);
        assertEquals(validationWithHashRequest.getSignature(), Base64.encodeBase64String(proxyDocument.getBytes()));
    }

    @Test
    public void signaturePolicyRemainsUnchanged() {
        ProxyDocument proxyDocument = transformer.transform(validationWithHashRequest);
        assertEquals(validationWithHashRequest.getSignaturePolicy(), proxyDocument.getSignaturePolicy());
    }

    private void setValidationWithHashRequest() throws Exception {
        Path filepath = Paths.get(getClass().getClassLoader().getResource(SIGNATURE_FILE).toURI());
        validationWithHashRequest = MockValidationWithHashRequestBuilder
                .aValidationWithHashRequest()
                .withSignaturePolicy("POL")
                .withSignature(filepath)
                .build();
    }
}
