package ee.openeid.siva.validation.service;


import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;

public interface ValidationService {
    QualifiedReport validateDocument(ValidationDocument wsDocument);
}
