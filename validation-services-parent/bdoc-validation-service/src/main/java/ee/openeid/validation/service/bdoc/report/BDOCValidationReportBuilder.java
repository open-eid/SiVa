/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.bdoc.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.validation.SignatureQualification;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.*;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.digidoc4j.impl.asic.asice.AsicESignature;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;
import static org.digidoc4j.X509Cert.SubjectName.CN;

public class BDOCValidationReportBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BDOCValidationReportBuilder.class);

    private static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
    private static final String FULL_DOCUMENT = "Full document";
    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String REPORT_INDICATION_INDETERMINATE = "INDETERMINATE";
    private static final String BDOC_SIGNATURE_FORM = "ASiC-E";

    private Container container;
    private ValidationDocument validationDocument;
    private ValidationPolicy validationPolicy;
    private List<DigiDoc4JException> containerErrors;
    private boolean isReportSignatureEnabled;

    public BDOCValidationReportBuilder(Container container, ValidationDocument validationDocument, ValidationPolicy validationPolicy, List<DigiDoc4JException> containerErrors, boolean isReportSignatureEnabled) {
        this.container = container;
        this.validationDocument = validationDocument;
        this.validationPolicy = validationPolicy;
        this.containerErrors = containerErrors;
        this.isReportSignatureEnabled = isReportSignatureEnabled;
    }

    private static ValidationWarning createValidationWarning(String content) {
        ValidationWarning validationWarning = new ValidationWarning();
        validationWarning.setContent(emptyWhenNull(content));
        return validationWarning;
    }

    private static Warning mapDigidoc4JWarning(DigiDoc4JException digiDoc4JException) {
        Warning warning = new Warning();
        warning.setContent(emptyWhenNull(digiDoc4JException.getMessage()));
        return warning;
    }

    private static SignatureScope createFullSignatureScopeForDataFile(String filename) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(filename);
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        signatureScope.setContent(FULL_DOCUMENT);
        return signatureScope;
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
        return new Reports(simpleReport, detailedReport);
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());

        validationConclusion.setSignatureForm(BDOC_SIGNATURE_FORM);
        validationConclusion.setSignaturesCount(container.getSignatures().size());
        validationConclusion.setValidationWarnings(containerValidationWarnings());
        validationConclusion.setSignatures(createSignaturesForReport(container));
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
        validationConclusion.setValidSignaturesCount(
                validationConclusion.getSignatures()
                        .stream()
                        .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());
        return validationConclusion;
    }

    private List<ValidationWarning> containerValidationWarnings() {
        List<ValidationWarning> validationWarnings = containerErrors.stream().map(e -> createValidationWarning(e.getMessage())).collect(Collectors.toList());
        validationWarnings.addAll(getValidationWarningsForUnsignedDataFiles());
        return validationWarnings;
    }

    private List<ValidationWarning> getValidationWarningsForUnsignedDataFiles() {
        List<String> dataFilenames = container.getDataFiles().stream().map(DataFile::getName).collect(Collectors.toList());
        return container.getSignatures()
                .stream()
                .map(signature -> createValidationWarning(signature, getUnsignedFiles((AsicESignature) signature, dataFilenames)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ValidationWarning createValidationWarning(Signature signature, List<String> unsignedFiles) {
        if (unsignedFiles.isEmpty()) {
            return null;
        }
        String signedBy = removeQuotes(signature.getSigningCertificate().getSubjectName(CN));
        String commaSeparated = unsignedFiles.stream().collect(Collectors.joining(", "));
        String content = String.format("Signature %s has unsigned files: %s", signedBy, commaSeparated);
        return createValidationWarning(content);
    }

    private List<String> getUnsignedFiles(AsicESignature bDocSignature, List<String> dataFilenames) {
        List<String> uris = bDocSignature.getOrigin().getReferences()
                .stream()
                .map(reference -> decodeUriIfPossible(reference.getURI()))
                .filter(dataFilenames::contains)
                .collect(Collectors.toList());
        return dataFilenames.stream()
                .filter(df -> !uris.contains(df))
                .collect(Collectors.toList());
    }

    private List<SignatureValidationData> createSignaturesForReport(Container container) {
        List<String> dataFilenames = container.getDataFiles().stream().map(DataFile::getName).collect(Collectors.toList());
        return container.getSignatures().stream().map(sig -> createSignatureValidationData(sig, dataFilenames)).collect(Collectors.toList());
    }

    private SignatureValidationData createSignatureValidationData(Signature signature, List<String> dataFilenames) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        AsicESignature bDocSignature = (AsicESignature) signature;


        signatureValidationData.setId(bDocSignature.getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat(bDocSignature.getProfile()));
        signatureValidationData.setSignatureLevel(getSignatureLevel(bDocSignature));
        signatureValidationData.setSignedBy(removeQuotes(bDocSignature.getSigningCertificate().getSubjectName(CN)));
        signatureValidationData.setErrors(getErrors(bDocSignature));
        signatureValidationData.setSignatureScopes(getSignatureScopes(bDocSignature, dataFilenames));
        signatureValidationData.setClaimedSigningTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(bDocSignature.getClaimedSigningTime()));
        signatureValidationData.setWarnings(getWarnings(bDocSignature));
        signatureValidationData.setInfo(getInfo(bDocSignature));
        signatureValidationData.setIndication(getIndication(bDocSignature));
        signatureValidationData.setSubIndication(getSubIndication(bDocSignature));
        signatureValidationData.setCountryCode(getCountryCode(bDocSignature));

        return signatureValidationData;
    }

    private String removeQuotes(String subjectName) {
        return subjectName.replaceAll("^\"|\"$", "");
    }

    private String getSignatureLevel(AsicESignature bDocSignature) {
        SignatureQualification signatureLevel = getDssSimpleReport(bDocSignature).getSignatureQualification(bDocSignature.getId());
        return signatureLevel != null ? signatureLevel.name() : "";
    }

    private eu.europa.esig.dss.validation.reports.SimpleReport getDssSimpleReport(AsicESignature bDocSignature) {
        return bDocSignature.getDssValidationReport().getReport().getSimpleReport();
    }

    private SignatureValidationData.Indication getIndication(AsicESignature bDocSignature) {
        SignatureValidationResult validationResult = bDocSignature.validateSignature();
        if (validationResult.isValid()) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (REPORT_INDICATION_INDETERMINATE.equals(getDssSimpleReport(bDocSignature).getIndication(bDocSignature.getId()).name())) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    private String getSubIndication(AsicESignature bDocSignature) {
        if (getIndication(bDocSignature) == SignatureValidationData.Indication.TOTAL_PASSED) {
            return "";
        }
        SubIndication subindication = getDssSimpleReport(bDocSignature).getSubIndication(bDocSignature.getId());
        return subindication != null ? subindication.name() : "";
    }

    private Info getInfo(AsicESignature bDocSignature) {
        Info info = new Info();
        Date trustedTime = bDocSignature.getTrustedSigningTime();
        if (trustedTime != null) {
            info.setBestSignatureTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(trustedTime));
        } else {
            info.setBestSignatureTime("");
        }
        return info;
    }

    private List<Warning> getWarnings(AsicESignature bDocSignature) {
        return bDocSignature.validateSignature().getWarnings()
                .stream()
                .map(BDOCValidationReportBuilder::mapDigidoc4JWarning)
                .collect(Collectors.toList());

    }

    private List<SignatureScope> getSignatureScopes(AsicESignature bDocSignature, List<String> dataFilenames) {
        return bDocSignature.getOrigin().getReferences()
                .stream()
                .map(r -> decodeUriIfPossible(r.getURI()))
                .filter(dataFilenames::contains) //filters out Signed Properties
                .map(BDOCValidationReportBuilder::createFullSignatureScopeForDataFile)
                .collect(Collectors.toList());
    }

    private String decodeUriIfPossible(String uri) {
        try {
            return URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("datafile " + uri + " has unsupported encoding", e);
            return uri;
        }
    }

    private List<Error> getErrors(AsicESignature bDocSignature) {
        return bDocSignature.validateSignature().getErrors()
                .stream()
                .map(BDOCValidationReportBuilder::mapDigidoc4JException)
                .collect(Collectors.toList());
    }

    private String getSignatureFormat(SignatureProfile profile) {
        return XADES_FORMAT_PREFIX + profile.name();
    }

    private String getCountryCode(AsicESignature bDocSignature) {
        return bDocSignature.getSigningCertificate().getSubjectName(X509Cert.SubjectName.C);
    }

}
