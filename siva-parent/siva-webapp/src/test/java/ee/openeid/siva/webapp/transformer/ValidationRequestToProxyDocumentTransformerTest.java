/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.testutils.MockValidationRequestBuilder;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ValidationRequestToProxyDocumentTransformerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_PDF_FILE = "test-files/sample.pdf";
    private ValidationRequestToProxyDocumentTransformer transformer = new ValidationRequestToProxyDocumentTransformer();
    private MockValidationRequestBuilder.MockValidationRequest validationRequest;

    @Before
    public void setUp() throws Exception {
        setValidPdfValidationRequest();
    }

    @Test
    public void fileNameRemainsUnchanged() {
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    public void pdfTypeIsCorrectlyTransformedToDocumentType() {
        assertEquals(DocumentType.PDF, transformer.transform(validationRequest).getDocumentType());
    }

    @Test
    public void bdocTypeIsCorrectlyTransformedToDocumentType() {
        validationRequest.setType("bdoc");
        assertEquals(DocumentType.BDOC, transformer.transform(validationRequest).getDocumentType());
    }

    @Test
    public void ddocTypeIsCorrectlyTransformedToDocumentType() {
        validationRequest.setType("ddoc");
        assertEquals(DocumentType.DDOC, transformer.transform(validationRequest).getDocumentType());
    }

    @Test
    public void xroadTypeIsCorrectlyTransformedToDocumentType() {
        validationRequest.setType("xroad");
        assertEquals(DocumentType.XROAD, transformer.transform(validationRequest).getDocumentType());
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        Assert.assertEquals(validationRequest.getDocument(), Base64.encodeBase64String(proxyDocument.getBytes()));
    }

    @Test
    public void unsupportedTypeThrowsException() {
        validationRequest = MockValidationRequestBuilder.aValidationRequest().withType("unsupported").build();
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("type = unsupported is unsupported");
        transformer.transform(validationRequest);
    }

    @Test
    public void signaturePolicyRemainsUnchanged() {
        ProxyDocument proxyDocument = transformer.transform(validationRequest);
        Assert.assertEquals(validationRequest.getSignaturePolicy(), proxyDocument.getSignaturePolicy());
    }

    private void setValidPdfValidationRequest() throws Exception {
        Path filepath = Paths.get(getClass().getClassLoader().getResource(VALID_PDF_FILE).toURI());
        validationRequest = MockValidationRequestBuilder
                .aValidationRequest()
                .withType("pdf")
                .withSignaturePolicy("POL")
                .withDocument(filepath)
                .build();
    }
}
