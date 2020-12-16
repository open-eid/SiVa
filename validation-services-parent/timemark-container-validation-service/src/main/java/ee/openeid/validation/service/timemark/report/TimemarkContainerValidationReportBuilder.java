/*
 * Copyright 2020 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.TimestampType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Container;
import org.digidoc4j.DataFile;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureProfile;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.X509Cert;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.digidoc4j.impl.asic.asice.AsicESignature;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;
import static org.digidoc4j.X509Cert.SubjectName.CN;

public abstract class TimemarkContainerValidationReportBuilder {

    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TimemarkContainerValidationReportBuilder.class);

    protected static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
    protected static final String FULL_DOCUMENT = "Digest of the document content";
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

    public TimemarkContainerValidationReportBuilder(Container container, ValidationDocument validationDocument, ValidationPolicy validationPolicy, ValidationResult validationResult, boolean isReportSignatureEnabled) {
        this.container = container;
        this.validationDocument = validationDocument;
        this.validationPolicy = validationPolicy;
        this.signatureValidationResults = validateAllSignatures(container);
        removeSignatureResults(validationResult);
        this.validationResult = validationResult;
        this.isReportSignatureEnabled = isReportSignatureEnabled;
    }

    static Warning createWarning(String content) {
        Warning warning = new Warning();
        warning.setContent(emptyWhenNull(content));
        return warning;
    }

    private static Warning mapDigidoc4JWarning(DigiDoc4JException digiDoc4JException) {
        Warning warning = new Warning();
        warning.setContent(emptyWhenNull(digiDoc4JException.getMessage()));
        return warning;
    }

    private static Error mapDigidoc4JException(DigiDoc4JException digiDoc4JException) {
        Error error = new Error();
        error.setContent(emptyWhenNull(digiDoc4JException.getMessage()));
        return error;
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
        signatureValidationData.setSignedBy(removeQuotes(signature.getSigningCertificate().getSubjectName(CN)));
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

    private SubjectDistinguishedName parseSubjectDistinguishedName(X509Cert signingCertificate) {
        String serialNumber = signingCertificate.getSubjectName(X509Cert.SubjectName.SERIALNUMBER);
        String commonName = signingCertificate.getSubjectName(CN);
        return SubjectDistinguishedName.builder()
                .serialNumber(serialNumber != null ? removeQuotes(serialNumber) : null)
                .commonName(commonName != null ? removeQuotes(commonName) : null)
                .build();
    }

    String removeQuotes(String subjectName) {
        return subjectName.replaceAll("^\"|\"$", "");
    }

    eu.europa.esig.dss.simplereport.SimpleReport getDssSimpleReport(AsicESignature bDocSignature) {
        return bDocSignature.getDssValidationReport().getReports().getSimpleReport();
    }

    private Info getInfo(Signature signature) {
        Info info = new Info();
        info.setBestSignatureTime(getBestSignatureTime(signature));
        if (signature.getProfile() == SignatureProfile.LT) {
            info.setTimestampCreationTime(getTimestampTime(signature));
        }
        info.setOcspResponseCreationTime(getOcspTime(signature));
        info.setTimeAssertionMessageImprint(getTimeAssertionMessageImprint(signature));
        info.setSignerRole(getSignerRole(signature));
        info.setSignatureProductionPlace(getSignatureProductionPlace(signature));
        return info;
    }

    private String getOcspTime(Signature signature) {
        return formatTime(signature.getOCSPResponseCreationTime());
    }

    private String getTimestampTime(Signature signature) {
        return formatTime(signature.getTimeStampCreationTime());
    }

    private String getBestSignatureTime(Signature signature) {
        return formatTime(signature.getTrustedSigningTime());
    }

    private String formatTime(Date date) {
        return date != null
                ? ReportBuilderUtils.getDateFormatterWithGMTZone().format(date)
                : null;
    }

    private String getTimeAssertionMessageImprint(Signature signature) {
        if (signature.getProfile() == SignatureProfile.LT) {
            TimestampWrapper timestamp = getBestTimestampWrapper(signature);
            try {
                return ReportBuilderUtils.parseTimeAssertionMessageImprint(timestamp);
            } catch (Exception e) {
                LOGGER.warn("Unable to parse time assertion message imprint from timestamp: ", e);
                return ""; //parse errors due to corrupted timestamp data should be present in validation errors already
            }
        }

        try {
            return StringUtils.defaultString(Base64.encodeBase64String(signature.getOCSPNonce()));
        } catch (DigiDoc4JException e) {
            LOGGER.warn("Unable to parse time assertion message imprint from OCSP nonce: ", e);
            return ""; //parse errors due to corrupted OCSP data should be present in validation errors already
        }
    }

    private TimestampWrapper getBestTimestampWrapper(Signature signature) {
        DiagnosticData diagnosticData = ((AsicESignature) signature).getDssValidationReport().getReports().getDiagnosticData();
        SignatureWrapper signatureWrapper = diagnosticData.getSignatureById(signature.getUniqueId());
        List<TimestampWrapper> timestamps = signatureWrapper.getTimestampListByType(TimestampType.SIGNATURE_TIMESTAMP);
        return timestamps.isEmpty() ? null : Collections.min(timestamps, Comparator.comparing(TimestampWrapper::getProductionTime));

    }

    private List<SignerRole> getSignerRole(Signature signature) {
        return signature.getSignerRoles().stream()
                .filter(StringUtils::isNotEmpty)
                .map(this::mapSignerRole)
                .collect(Collectors.toList());
    }

    private SignerRole mapSignerRole(String claimedRole) {
        SignerRole signerRole = new SignerRole();
        signerRole.setClaimedRole(claimedRole);
        return signerRole;
    }

    private SignatureProductionPlace getSignatureProductionPlace(Signature signature) {
        if (isSignatureProductionPlaceEmpty(signature)) {
            return null;
        }

        SignatureProductionPlace signatureProductionPlace = new SignatureProductionPlace();
        signatureProductionPlace.setCountryName(StringUtils.defaultString(signature.getCountryName()));
        signatureProductionPlace.setStateOrProvince(StringUtils.defaultString(signature.getStateOrProvince()));
        signatureProductionPlace.setCity(StringUtils.defaultString(signature.getCity()));
        signatureProductionPlace.setPostalCode(StringUtils.defaultString(signature.getPostalCode()));
        return signatureProductionPlace;
    }

    private boolean isSignatureProductionPlaceEmpty(Signature signature) {
        return StringUtils.isAllEmpty(
                signature.getCountryName(),
                signature.getStateOrProvince(),
                signature.getCity(),
                signature.getPostalCode());
    }

    private List<Warning> getWarnings(Signature signature) {
        ValidationResult signatureValidationResult = signatureValidationResults.get(signature.getUniqueId());
        List<Warning> warnings = Stream.of(signatureValidationResult.getWarnings(), this.validationResult.getWarnings())
                .flatMap(Collection::stream)
                .distinct()
                .map(TimemarkContainerValidationReportBuilder::mapDigidoc4JWarning)
                .collect(Collectors.toList());
        warnings.addAll(getExtraWarnings(signature));
        return warnings;
    }

    private List<Error> getErrors(Signature signature) {
        ValidationResult signatureValidationResult = signatureValidationResults.get(signature.getUniqueId());
        return Stream.of(signatureValidationResult.getErrors(), this.validationResult.getErrors())
                .flatMap(Collection::stream)
                .distinct()
                .map(TimemarkContainerValidationReportBuilder::mapDigidoc4JException)
                .collect(Collectors.toList());
    }

    private String getCountryCode(Signature signature) {
        return signature.getSigningCertificate().getSubjectName(X509Cert.SubjectName.C);
    }

    protected List<Certificate> getCertificateList(Signature signature) {
        List<Certificate> certificateList = new ArrayList<>();
        if (signature.getOCSPCertificate() != null) {
            X509Certificate x509Certificate = signature.getOCSPCertificate().getX509Certificate();
            certificateList.add(getCertificate(x509Certificate, CertificateType.REVOCATION));
        }
        if (signature.getSigningCertificate() != null) {
            X509Certificate x509Certificate = signature.getSigningCertificate().getX509Certificate();
            certificateList.add(getCertificate(x509Certificate, CertificateType.SIGNING));
        }
        return certificateList;
    }

    protected Certificate getCertificate(X509Certificate x509Certificate, CertificateType type) {
        Certificate certificate = new Certificate();
        certificate.setContent(CertUtil.encodeCertificateToBase64(x509Certificate));
        certificate.setCommonName(CertUtil.getCommonName(x509Certificate));
        certificate.setType(type);
        return certificate;
    }

    abstract void processSignatureIndications(ValidationConclusion validationConclusion, String policyName);

    abstract SignatureValidationData.Indication getIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults);

    abstract String getSubIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults);

    abstract String getSignatureLevel(Signature signature);

    abstract List<Warning> getExtraWarnings(Signature signature);

    abstract List<ValidationWarning> getExtraValidationWarnings();

    abstract List<SignatureScope> getSignatureScopes(Signature signature, List<String> dataFilenames);

    abstract String getSignatureForm();

    abstract String getSignatureFormat(SignatureProfile profile);

}
