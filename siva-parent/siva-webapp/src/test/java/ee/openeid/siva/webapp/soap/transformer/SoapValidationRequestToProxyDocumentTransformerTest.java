package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.webapp.soap.DocumentType;
import ee.openeid.siva.webapp.soap.SoapValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SoapValidationRequestToProxyDocumentTransformerTest {

    private SoapValidationRequestToProxyDocumentTransformer transformer = new SoapValidationRequestToProxyDocumentTransformer();

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        String documentContent = "ZmlsZWNvbnRlbnQ=";
        SoapValidationRequest validationRequest = createSoapValidationRequest(documentContent, DocumentType.BDOC, "file.bdoc", "some policy");
        Assert.assertEquals(validationRequest.getDocument(), Base64.encodeBase64String(transformer.transform(validationRequest).getBytes()));
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
