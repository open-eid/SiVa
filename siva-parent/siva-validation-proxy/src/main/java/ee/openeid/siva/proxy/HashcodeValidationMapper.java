package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.validation.document.SignatureFile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HashcodeValidationMapper {
  public List<ValidationDocument> mapToValidationDocuments(ProxyHashcodeDataSet proxyRequest) {
    return proxyRequest.getSignatureFiles()
      .stream()
      .map(signatureFile -> createValidationDocument(proxyRequest.getSignaturePolicy(), signatureFile))
      .toList();
  }

  private ValidationDocument createValidationDocument(String signaturePolicy, SignatureFile signatureFile) {
    ValidationDocument validationDocument = new ValidationDocument();
    validationDocument.setSignaturePolicy(signaturePolicy);
    validationDocument.setBytes(signatureFile.getSignature());
    validationDocument.setDatafiles(signatureFile.getDatafiles());
    return validationDocument;
  }

  Reports mergeReportsToOne(List<Reports> reportsList) {
    int signaturesCount = 0;
    int validSignaturesCount = 0;
    Reports response = null;
    for (Reports reports : reportsList) {
      ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
      if (signaturesCount == 0) {
        response = reports;
        validSignaturesCount = validationConclusion.getValidSignaturesCount();
      } else {
        response.getSimpleReport().getValidationConclusion().getSignatures().addAll(validationConclusion.getSignatures());
        validSignaturesCount = validSignaturesCount + validationConclusion.getValidSignaturesCount();
      }
      signaturesCount = signaturesCount + validationConclusion.getSignaturesCount();
    }
    if (response != null) {
      ValidationConclusion validationConclusion = response.getSimpleReport().getValidationConclusion();
      validationConclusion.setSignaturesCount(signaturesCount);
      validationConclusion.setValidSignaturesCount(validSignaturesCount);
    }
    return response;
  }
}
