package ee.openeid.siva.webapp.transformer;


import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.siva.webapp.mimetype.MimeTypeResolver;
import ee.openeid.siva.webapp.request.model.JSONValidationRequest;
import eu.europa.esig.dss.MimeType;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class RequestToJsonDocumentTransformer {

    public JSONDocument transform(JSONValidationRequest validationRequest) {
        JSONDocument jsonDocument = new JSONDocument();
        jsonDocument.setName(validationRequest.getFilename());
        jsonDocument.setMimeType(mimeTypeFromString(validationRequest.getType()));
        jsonDocument.setBytes(base64ToBytes(validationRequest.getBase64Document()));
        return jsonDocument;
    }

    private byte[] base64ToBytes(String base64File) {
        return Base64.decodeBase64(base64File);
    }

    private MimeType mimeTypeFromString(String mimeType) {
        return MimeTypeResolver.mimeTypeFromString(mimeType);
    }

}
