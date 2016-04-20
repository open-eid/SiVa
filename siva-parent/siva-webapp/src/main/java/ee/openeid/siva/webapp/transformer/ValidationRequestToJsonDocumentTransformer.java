package ee.openeid.siva.webapp.transformer;


import ee.openeid.pdf.webservice.json.PDFDocument;
import ee.openeid.siva.mimetype.MimeTypeResolver;
import ee.openeid.siva.webapp.request.ValidationRequest;
import eu.europa.esig.dss.MimeType;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class ValidationRequestToJSONDocumentTransformer {

    public PDFDocument transform(ValidationRequest validationRequest) {
        PDFDocument PDFDocument = new PDFDocument();
        PDFDocument.setName(validationRequest.getFilename());
        PDFDocument.setMimeType(mimeTypeFromString(validationRequest.getType()));
        PDFDocument.setBytes(base64ToBytes(validationRequest.getBase64Document()));
        PDFDocument.setReportType(validationRequest.getReportType());
        return PDFDocument;
    }

    private byte[] base64ToBytes(String base64File) {
        return Base64.decodeBase64(base64File);
    }

    private MimeType mimeTypeFromString(String mimeType) {
        return MimeTypeResolver.mimeTypeFromString(mimeType);
    }

}
