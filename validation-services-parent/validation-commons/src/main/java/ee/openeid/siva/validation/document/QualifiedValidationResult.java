package ee.openeid.siva.validation.document;

import ee.openeid.siva.validation.document.report.QualifiedReport;

public interface QualifiedValidationResult {

    String getSimpleReport();
    String getDetailedReport();
    String getDiagnosticData();
    QualifiedReport getQualifiedReport();
}
