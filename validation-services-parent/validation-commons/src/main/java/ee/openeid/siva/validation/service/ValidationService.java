package ee.openeid.siva.validation.service;


import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;

public interface ValidationService {
    QualifiedValidationResult validateDocument(ValidationDocument validationDocument);
}
