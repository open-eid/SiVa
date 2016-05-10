package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;

abstract class AbstractValidationProxy implements ValidationProxy {

    @Override
    public String validate(final ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = createValidationDocument(proxyDocument);
        QualifiedValidationResult validationResult = validateInService(validationDocument);
        String report = getReport(validationResult, proxyDocument.getReportType());
        if (proxyDocument.getRequestProtocol() == RequestProtocol.JSON) {
            report = toJSON(report);
        }
        return report;
    }

    abstract QualifiedValidationResult validateInService(ValidationDocument validationDocument);
    abstract String toJSON(String report);

    private ValidationDocument createValidationDocument(ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName(proxyDocument.getName());
        validationDocument.setBytes(proxyDocument.getBytes());
        validationDocument.setMimeType(proxyDocument.getDocumentType().getMimeType());
        return validationDocument;
    }

    private String getReport(QualifiedValidationResult validationResult, ReportType reportType) {
        if (reportType == ReportType.DETAILED) {
            return validationResult.getDetailedReport();
        } else if (reportType == ReportType.DIAGNOSTICDATA) {
            return validationResult.getDiagnosticData();
        } else {
            return validationResult.getSimpleReport();
        }
    }
}
