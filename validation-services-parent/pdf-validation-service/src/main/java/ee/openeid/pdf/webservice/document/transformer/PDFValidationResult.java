package ee.openeid.pdf.webservice.document.transformer;

import ee.openeid.siva.validation.document.QualifiedValidationResult;
import eu.europa.esig.dss.validation.report.Reports;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PDFValidationResult implements QualifiedValidationResult {

    private String simpleReport;
    private String detailedReport;
    private String diagnosticData;

    public PDFValidationResult(Reports reports) {
        setSimpleReport(reports.getSimpleReport().toString());
        setDetailedReport(reports.getDetailedReport().toString());
        setDiagnosticData(reports.getDiagnosticData().toString());
    }
}
