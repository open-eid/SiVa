package ee.openeid.siva.webapp.response;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import lombok.Data;

@Data
public class ValidationResponse {

    private QualifiedReport validationReport;

    private String validationReportSignature;

    public ValidationResponse() {
    }

    public ValidationResponse(QualifiedReport report) {
        validationReport = report;
    }

} 
