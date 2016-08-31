package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import ee.openeid.siva.webapp.request.validation.annotations.ValidDocumentType;
import ee.openeid.siva.webapp.request.validation.annotations.ValidFilename;
import ee.openeid.siva.webapp.request.validation.annotations.ValidSignaturePolicy;
import lombok.Data;

@Data
public class JSONValidationRequest implements ValidationRequest {

    @ValidBase64String
    private String document;

    @ValidFilename
    private String filename;

    @ValidDocumentType
    private String documentType;

    @ValidSignaturePolicy
    private String signaturePolicy;

}
