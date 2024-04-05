package ee.openeid.siva.validation.document.report.builder;

import ee.openeid.siva.validation.document.report.SignatureValidationData;

@FunctionalInterface
public interface SignatureValidationDataProcessor<T> {
    void process(SignatureValidationData signatureValidationData, T signatureIdentifier);
}
