package ee.openeid.siva.webapp.request.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.openeid.siva.webapp.request.ValidationRequest;
import ee.openeid.siva.webapp.request.validation.json.ValidDocumentType;
import ee.openeid.siva.webapp.request.validation.json.ValidFilename;
import ee.openeid.siva.webapp.request.validation.json.ValidReportType;
import lombok.Data;

@Data
public class JSONValidationRequest implements ValidationRequest {

    @JsonProperty("document")
    private String base64Document;

    @JsonProperty("filename")
    @ValidFilename
    private String filename;

    @JsonProperty("documentType")
    @ValidDocumentType
    private String documentType;

    @JsonProperty("reportType")
    @ValidReportType
    private String reportType;

}
