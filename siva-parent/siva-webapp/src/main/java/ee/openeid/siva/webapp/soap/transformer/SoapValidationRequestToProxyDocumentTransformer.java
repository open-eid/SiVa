package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.typeresolver.DocumentTypeResolver;
import ee.openeid.siva.webapp.soap.SoapValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class SoapValidationRequestToProxyDocumentTransformer {

    public ProxyDocument transform(SoapValidationRequest validationRequest) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(validationRequest.getFilename());
        proxyDocument.setDocumentType(DocumentTypeResolver.documentTypeFromString(validationRequest.getDocumentType().name()));
        proxyDocument.setBytes(Base64.decodeBase64(validationRequest.getDocument()));
        proxyDocument.setSignaturePolicy(validationRequest.getSignaturePolicy());
        return proxyDocument;
    }

}
