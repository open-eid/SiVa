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

package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.webapp.soap.DocumentType;
import ee.openeid.siva.webapp.soap.SoapValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SoapValidationRequestToProxyDocumentTransformerTest {

    private SoapValidationRequestToProxyDocumentTransformer transformer = new SoapValidationRequestToProxyDocumentTransformer();

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        String documentContent = "ZmlsZWNvbnRlbnQ=";
        SoapValidationRequest validationRequest = createSoapValidationRequest(documentContent, DocumentType.BDOC, "file.bdoc", "some policy");
        assertEquals(validationRequest.getDocument(), Base64.encodeBase64String(transformer.transform(validationRequest).getBytes()));
    }

    @Test
    public void pdfTypeIsCorrectlyTransformedToDocumentType() {
        DocumentType docType = DocumentType.PDF;
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", docType, "file.pdf", "some policy");
        assertEquals(validationRequest.getDocumentType().name(), transformer.transform(validationRequest).getDocumentType().name());
    }

    @Test
    public void bdocTypeIsCorrectlyTransformedToDocumentType() {
        DocumentType docType = DocumentType.BDOC;
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", docType, "file.bdoc", "some policy");
        assertEquals(validationRequest.getDocumentType().name(), transformer.transform(validationRequest).getDocumentType().name());
    }

    @Test
    public void ddocTypeIsCorrectlyTransformedToDocumentType() {
        DocumentType docType = DocumentType.DDOC;
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", docType, "file.ddoc", "some policy");
        assertEquals(validationRequest.getDocumentType().name(), transformer.transform(validationRequest).getDocumentType().name());
    }

    @Test
    public void xroadTypeIsCorrectlyTransformedToDocumentType() {
        DocumentType docType = DocumentType.XROAD;
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", docType, "file.asice", "some policy");
        assertEquals(validationRequest.getDocumentType().name(), transformer.transform(validationRequest).getDocumentType().name());
    }

    @Test
    public void fileNameRemainsUnchanged() {
        String filename = "random file name.bdoc";
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", DocumentType.BDOC, filename, "some policy");
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    public void signaturePolicyRemainsUnchanged() {
        String policy = "policy";
        SoapValidationRequest validationRequest = createSoapValidationRequest("Ymxh", DocumentType.BDOC, "file.bdoc", policy);
        assertEquals(validationRequest.getSignaturePolicy(), transformer.transform(validationRequest).getSignaturePolicy());
    }

    private SoapValidationRequest createSoapValidationRequest(String document, DocumentType docType, String filename, String signaturePolicy) {
        SoapValidationRequest validationRequest = new SoapValidationRequest();
        validationRequest.setDocument(document);
        validationRequest.setDocumentType(docType);
        validationRequest.setFilename(filename);
        validationRequest.setSignaturePolicy(signaturePolicy);
        return validationRequest;
    }

}
