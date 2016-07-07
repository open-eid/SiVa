package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.*;
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
