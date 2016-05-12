package ee.openeid.siva.validation.service.bdoc;

import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import lombok.Data;
import org.digidoc4j.ValidationResult;

@Data
public class BDOCValidationResult implements QualifiedValidationResult {

    private String simpleReport;
    private String detailedReport;
    private String diagnosticData;

    private QualifiedReport qualifiedReport;

    public BDOCValidationResult(ValidationResult validationResult) {
        setSimpleReport(validationResult.getReport());
        setDetailedReport(validationResult.getReport());
        setDiagnosticData(validationResult.getReport());
    }
}
