package ee.openeid.siva.validation.document;

public interface QualifiedValidationResult {

    String getSimpleReport();
    String getDetailedReport();
    String getDiagnosticData();

}
