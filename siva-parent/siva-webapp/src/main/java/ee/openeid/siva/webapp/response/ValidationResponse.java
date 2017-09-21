package ee.openeid.siva.webapp.response;

import ee.openeid.siva.validation.document.report.SimpleReport;
import lombok.Data;

@Data
public class ValidationResponse {

    private SimpleReport validationReport;

    private String validationReportSignature;

    public ValidationResponse() {
    }

    public ValidationResponse(SimpleReport report) {
        validationReport = report;
    }

} 
