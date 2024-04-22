package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Signature;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.X509Cert;
import org.digidoc4j.impl.asic.AsicSignature;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getValidationTime;
import static ee.openeid.validation.service.timemark.report.TimemarkContainerValidationReportBuilder.REPORT_INDICATION_INDETERMINATE;
import static ee.openeid.validation.service.timemark.util.SignatureScopeParser.getAsicSignatureScopes;
import static ee.openeid.validation.service.timemark.util.SignatureCertificateParser.getCertificateList;
import static ee.openeid.validation.service.timemark.util.SignatureInfoParser.getInfo;
import static ee.openeid.validation.service.timemark.util.SigningCertificateParser.parseSignedBy;
import static ee.openeid.validation.service.timemark.util.SigningCertificateParser.parseSubjectDistinguishedName;
import static ee.openeid.validation.service.timemark.util.ValidationErrorMapper.getErrors;
import static ee.openeid.validation.service.timemark.util.ValidationErrorMapper.getWarnings;
import static java.util.Collections.emptyList;

public class TimemarkHashcodeValidationReportBuilder {
  private final ValidationResult validationResult;
  private final ConstraintDefinedPolicy validationPolicy;
  private final Signature signature;
  private final ValidationDocument validationDocument;
  private final boolean isReportSignatureEnabled;


  public TimemarkHashcodeValidationReportBuilder(ValidationResult validationResult,
                                                 ConstraintDefinedPolicy validationPolicy,
                                                 Signature signature,
                                                 ValidationDocument validationDocument,
                                                 boolean isReportSignatureEnabled) {
    this.validationResult = validationResult;
    this.validationPolicy = validationPolicy;
    this.signature = signature;
    this.validationDocument = validationDocument;
    this.isReportSignatureEnabled = isReportSignatureEnabled;
  }

  public Reports build() {
    final var validationConclusion = getValidationConclusion();
    final var simpleReport = new SimpleReport(validationConclusion);
    return new Reports(simpleReport, null, null);
  }

  private ValidationConclusion getValidationConclusion() {
    ValidationConclusion validationConclusion = new ValidationConclusion();
    validationConclusion.setPolicy(createReportPolicy(validationPolicy));
    validationConclusion.setValidationTime(getValidationTime());
    validationConclusion.setValidationWarnings(emptyList());
    validationConclusion.setSignatures(List.of(buildSignatureValidationData()));
    validationConclusion.setSignaturesCount(validationConclusion.getSignatures().size());
    validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
    validationConclusion.setValidSignaturesCount((int) validationConclusion.getSignatures()
      .stream()
      .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString())).count());
    return validationConclusion;
  }

  private SignatureValidationData buildSignatureValidationData() {
    SignatureValidationData signatureValidationData = new SignatureValidationData();
    signatureValidationData.setId(signature.getId());
    signatureValidationData.setSignatureFormat(getSignatureFormat());
    signatureValidationData.setSignatureMethod(signature.getSignatureMethod());
    signatureValidationData.setSignatureLevel(getSignatureLevel());
    signatureValidationData.setSignedBy(parseSignedBy(signature.getSigningCertificate()));
    signatureValidationData.setSubjectDistinguishedName(parseSubjectDistinguishedName(signature.getSigningCertificate()));
    signatureValidationData.setErrors(getErrors(Stream.of(validationResult.getErrors())));
    signatureValidationData.setWarnings(getWarnings(Stream.of(validationResult.getWarnings())));
    signatureValidationData.setSignatureScopes(getSignatureScopes());
    signatureValidationData.setClaimedSigningTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(signature.getClaimedSigningTime()));
    signatureValidationData.setInfo(getInfo(signature));
    signatureValidationData.setIndication(getIndication());
    signatureValidationData.setSubIndication(getSubIndication());
    signatureValidationData.setCountryCode(getCountryCode(signature));
    signatureValidationData.setCertificates(getCertificateList(signature));
    return signatureValidationData;
  }

  private String getCountryCode(Signature signature) {
    return signature.getSigningCertificate().getSubjectName(X509Cert.SubjectName.C);
  }

  private List<String> getDataFileNames() {
    return validationDocument.getDatafiles()
      .stream()
      .map(Datafile::getFilename)
      .toList();
  }

  private String getSignatureLevel() {
    return getAsicSignatureSimpleReport()
      .map(simpleReport -> simpleReport.getSignatureQualification(signature.getUniqueId()))
      .map(Enum::name)
      .orElse(null);
  }

  private String getSignatureFormat() {
    return getAsicSignatureSimpleReport()
      .map(simpleReport -> simpleReport.getSignatureFormat(signature.getUniqueId()))
      .map(Enum::name)
      .orElse(null);
  }

  private Optional<eu.europa.esig.dss.simplereport.SimpleReport> getAsicSignatureSimpleReport() {
    return signature instanceof AsicSignature
      ? Optional.of(((AsicSignature) signature).getDssValidationReport().getReports().getSimpleReport())
      : Optional.empty();
  }

  private List<SignatureScope> getSignatureScopes() {
    return signature instanceof AsicSignature
      ? getAsicSignatureScopes((AsicSignature) signature, getDataFileNames())
      : emptyList();
  }

  private SignatureValidationData.Indication getIndication() {
    if (validationResult.isValid() && validationResult.getErrors().isEmpty()) {
      return SignatureValidationData.Indication.TOTAL_PASSED;
    }
    if (isIndeterminate() && validationResult.getErrors().isEmpty()) {
      return SignatureValidationData.Indication.INDETERMINATE;
    }
    return SignatureValidationData.Indication.TOTAL_FAILED;
  }

  private boolean isIndeterminate() {
    return getAsicSignatureSimpleReport()
      .map(simpleReport -> simpleReport.getIndication(signature.getUniqueId()))
      .map(Enum::name)
      .filter(REPORT_INDICATION_INDETERMINATE::equals)
      .isPresent();
  }

  private String getSubIndication() {
    if (SignatureValidationData.Indication.TOTAL_PASSED.equals(getIndication())) {
      return "";
    }
    return getAsicSignatureSimpleReport()
      .map(simpleReport -> simpleReport.getSubIndication(signature.getUniqueId()))
      .map(Enum::name)
      .orElse("");

  }
}
