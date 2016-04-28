package ee.openeid.siva.validation.service;


import ee.openeid.siva.validation.document.ValidationDocument;

import java.util.Map;

public interface ValidationService {
    Map<String, String> validateDocument(ValidationDocument wsDocument);
}
