package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.timemark.report.TimemarkHashcodeValidationReportBuilder;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.PolicyConfigurationWrapper;
import eu.europa.esig.dss.enumerations.MimeType;
import lombok.SneakyThrows;
import org.digidoc4j.Configuration;
import org.digidoc4j.DetachedXadesSignatureBuilder;
import org.digidoc4j.DigestAlgorithm;
import org.digidoc4j.DigestDataFile;
import org.digidoc4j.Signature;
import org.digidoc4j.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TimemarkHashcodeValidationService implements ValidationService {
  private final BDOCConfigurationService bdocConfigurationService;
  private final ReportConfigurationProperties reportConfigurationProperties;

  @Autowired
  public TimemarkHashcodeValidationService(BDOCConfigurationService bdocConfigurationService,
                                           ReportConfigurationProperties reportConfigurationProperties) {
    this.bdocConfigurationService = bdocConfigurationService;
    this.reportConfigurationProperties = reportConfigurationProperties;
  }

  @Override
  public Reports validateDocument(ValidationDocument validationDocument) {
    final PolicyConfigurationWrapper configuration = loadConfiguration(validationDocument);
    final Signature signature = createSignature(validationDocument, configuration.getConfiguration());
    final ValidationResult validationResult = signature.validateSignature();
    return new TimemarkHashcodeValidationReportBuilder(
      validationResult,
      configuration.getPolicy(),
      signature,
      validationDocument,
      reportConfigurationProperties.isReportSignatureEnabled()
    ).build();
  }

  private Signature createSignature(ValidationDocument validationDocument, Configuration configuration) {
    final DetachedXadesSignatureBuilder signatureBuilder = DetachedXadesSignatureBuilder.withConfiguration(configuration);
    addDataFiles(signatureBuilder, validationDocument);
    return signatureBuilder.openAdESSignature(validationDocument.getBytes());
  }

  private PolicyConfigurationWrapper loadConfiguration(ValidationDocument validationDocument) {
    return bdocConfigurationService.loadPolicyConfiguration(validationDocument.getSignaturePolicy());
  }

  @SneakyThrows
  private void addDataFiles(DetachedXadesSignatureBuilder signatureBuilder, ValidationDocument validationDocument) {
    for (final Datafile dataFile : validationDocument.getDatafiles()) {
      signatureBuilder.withDataFile(new DigestDataFile(
        dataFile.getFilename(),
        DigestAlgorithm.valueOf(dataFile.getHashAlgo().toUpperCase()),
        Base64.getDecoder().decode(dataFile.getHash()),
        MimeType.fromFileName(dataFile.getFilename()).getMimeTypeString()
      ));
    }
  }
}
