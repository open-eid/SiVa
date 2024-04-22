/*
 * Copyright 2020 - 2024 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.DiagnosticReport;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.timemark.util.SignatureCertificateParser;
import ee.openeid.validation.service.timemark.util.ValidationErrorMapper;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Container;
import org.digidoc4j.DataFile;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureProfile;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.X509Cert;
import org.digidoc4j.impl.asic.asice.AsicESignature;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getValidationTime;
import static ee.openeid.validation.service.timemark.util.SignatureInfoParser.getInfo;
import static ee.openeid.validation.service.timemark.util.SigningCertificateParser.parseSignedBy;
import static ee.openeid.validation.service.timemark.util.SigningCertificateParser.parseSubjectDistinguishedName;

public abstract class TimemarkContainerValidationReportBuilder {

    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TimemarkContainerValidationReportBuilder.class);

    protected static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    protected static final String REPORT_INDICATION_INDETERMINATE = "INDETERMINATE";
    protected static final String BDOC_SIGNATURE_FORM = "ASiC-E";
    protected static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";
    protected static final String DDOC_HASHCODE_SIGNATURE_FORM_SUFFIX = "_hashcode";
    protected static final String HASHCODE_CONTENT_TYPE = "HASHCODE";

    Container container;
    private ValidationDocument validationDocument;
    private ValidationPolicy validationPolicy;
    ValidationResult validationResult;
    private boolean isReportSignatureEnabled;
    private Map<String, ValidationResult> signatureValidationResults;

    protected TimemarkContainerValidationReportBuilder(Container container, ValidationDocument validationDocument, ValidationPolicy validationPolicy, ValidationResult validationResult, boolean isReportSignatureEnabled) {
        this.container = container;
        this.validationDocument = validationDocument;
        this.validationPolicy = validationPolicy;
        this.signatureValidationResults = validateAllSignatures(container);
        removeSignatureResults(validationResult);
        this.validationResult = validationResult;
        this.isReportSignatureEnabled = isReportSignatureEnabled;
    }

    public Reports build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        processSignatureIndications(validationConclusion, validationPolicy.getName());

        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        DetailedReport detailedReport = new DetailedReport(validationConclusion, null);
        DiagnosticReport diagnosticReport = new DiagnosticReport(validationConclusion, null);
        return new Reports(simpleReport, detailedReport, diagnosticReport);
    }

    private void removeSignatureResults(ValidationResult validationResult) {
        removeSignatureErrors(validationResult);
        removeSignatureWarning(validationResult);
    }

    private void removeSignatureErrors(ValidationResult validationResult) {
        validationResult.getErrors().removeIf(exception -> signatureValidationResults.values().stream()
                .anyMatch(sigResult -> sigResult.getErrors().stream()
                        .anyMatch(e -> e.getMessage().equals(exception.getMessage()))));
    }

    private void removeSignatureWarning(ValidationResult validationResult) {
        validationResult.getWarnings().removeIf(exception -> signatureValidationResults.values().stream()
                .anyMatch(sigResult -> sigResult.getWarnings().stream()
                        .anyMatch(e -> e.getMessage().equals(exception.getMessage()))));
    }

    private Map<String, ValidationResult> validateAllSignatures(Container container) {
        Map<String, ValidationResult> results = new HashMap<>();
        for (Signature signature : container.getSignatures()) {
            results.put(signature.getUniqueId(), signature.validateSignature());
        }
        return results;
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());
        validationConclusion.setSignatureForm(getSignatureForm());
        validationConclusion.setSignaturesCount(container.getSignatures().size());
        List<SignatureValidationData> signaturesValidationResult = createSignaturesForReport(container);
        validationConclusion.setSignatures(signaturesValidationResult);
        validationConclusion.setValidationWarnings(containerValidationWarnings());
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
        validationConclusion.setValidSignaturesCount(
                (int) validationConclusion.getSignatures()
                        .stream()
                        .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                        .count());
        return validationConclusion;
    }

    List<ValidationWarning> containerValidationWarnings() {
        return getExtraValidationWarnings();
    }

    private List<SignatureValidationData> createSignaturesForReport(Container container) {
        List<String> dataFilenames = container.getDataFiles().stream().map(DataFile::getName).collect(Collectors.toList());
        return container.getSignatures().stream().map(sig -> createSignatureValidationData(sig, dataFilenames)).collect(Collectors.toList());
    }

    private SignatureValidationData createSignatureValidationData(Signature signature, List<String> dataFilenames) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();

        signatureValidationData.setId(signature.getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat(signature.getProfile()));
        signatureValidationData.setSignatureMethod(signature.getSignatureMethod());
        signatureValidationData.setSignatureLevel(getSignatureLevel(signature));
        signatureValidationData.setSignedBy(parseSignedBy(signature.getSigningCertificate()));
        signatureValidationData.setSubjectDistinguishedName(parseSubjectDistinguishedName(signature.getSigningCertificate()));
        signatureValidationData.setErrors(getErrors(signature));
        signatureValidationData.setSignatureScopes(getSignatureScopes(signature, dataFilenames));
        signatureValidationData.setClaimedSigningTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(signature.getClaimedSigningTime()));
        signatureValidationData.setWarnings(getWarnings(signature));
        signatureValidationData.setInfo(getInfo(signature));
        signatureValidationData.setIndication(getIndication(signature, signatureValidationResults));
        signatureValidationData.setSubIndication(getSubIndication(signature, signatureValidationResults));
        signatureValidationData.setCountryCode(getCountryCode(signature));
        signatureValidationData.setCertificates(getCertificateList(signature));

        return signatureValidationData;
    }

    eu.europa.esig.dss.simplereport.SimpleReport getDssSimpleReport(AsicESignature bDocSignature) {
        return bDocSignature.getDssValidationReport().getReports().getSimpleReport();
    }

    private List<Warning> getWarnings(Signature signature) {
        ValidationResult signatureValidationResult = signatureValidationResults.get(signature.getUniqueId());
        return ValidationErrorMapper.getWarnings(Stream.of(
          signatureValidationResult.getWarnings(),
          this.validationResult.getWarnings()
        ));
    }

    private List<Error> getErrors(Signature signature) {
        ValidationResult signatureValidationResult = signatureValidationResults.get(signature.getUniqueId());
        return ValidationErrorMapper.getErrors(Stream.of(
          signatureValidationResult.getErrors(),
          this.validationResult.getErrors()
        ));
    }

    private String getCountryCode(Signature signature) {
        return signature.getSigningCertificate().getSubjectName(X509Cert.SubjectName.C);
    }

    protected List<Certificate> getCertificateList(Signature signature) {
        return SignatureCertificateParser.getCertificateList(signature);
    }

    abstract void processSignatureIndications(ValidationConclusion validationConclusion, String policyName);

    abstract SignatureValidationData.Indication getIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults);

    abstract String getSubIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults);

    abstract String getSignatureLevel(Signature signature);

    abstract List<ValidationWarning> getExtraValidationWarnings();

    abstract List<SignatureScope> getSignatureScopes(Signature signature, List<String> dataFilenames);

    abstract String getSignatureForm();

    abstract String getSignatureFormat(SignatureProfile profile);

}
