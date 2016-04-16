package ee.openeid.siva.webapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.openeid.siva.model.ValidationRequest;

//TODO: Need to add some restrictions when we know what restrictions we need on the request
public class JSONValidationRequest implements ValidationRequest {

    @JsonProperty("document")
    private String base64Document;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("document-type")
    private String type;

    @JsonProperty("report-type")
    private String reportType;

    public String getBase64Document() {
        return base64Document;
    }

    public void setBase64Document(String base64Document) {
        this.base64Document = base64Document;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
}
