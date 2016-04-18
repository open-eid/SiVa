package ee.openeid.siva.webapp.request.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.openeid.siva.webapp.request.ValidationRequest;
import lombok.Data;

//TODO: Need to add some restrictions when we know what restrictions we need on the request
@Data
public class JSONValidationRequest implements ValidationRequest {

    @JsonProperty("document")
    private String base64Document;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("documentType")
    private String type;

    @JsonProperty("reportType")
    private String reportType;

}
