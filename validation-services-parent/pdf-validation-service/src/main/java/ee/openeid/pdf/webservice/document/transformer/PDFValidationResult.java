package ee.openeid.pdf.webservice.document.transformer;

import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import eu.europa.esig.dss.validation.report.Reports;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    @Override
    public QualifiedReport getQualifiedReport() {
        throw new NotImplementedException(); //TODO: implement with VAL-180
    }
}
