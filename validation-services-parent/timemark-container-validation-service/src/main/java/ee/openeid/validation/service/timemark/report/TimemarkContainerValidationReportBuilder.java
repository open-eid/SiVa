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

package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.validation.SignatureQualification;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;

import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.*;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.digidoc4j.impl.asic.asice.AsicESignature;
import org.digidoc4j.impl.ddoc.DDocContainer;
import org.digidoc4j.impl.ddoc.DDocFacade;
import org.digidoc4j.impl.ddoc.DDocSignature;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;
import static org.digidoc4j.X509Cert.SubjectName.CN;

public class TimemarkContainerValidationReportBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TimemarkContainerValidationReportBuilder.class);

    private static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
    private static final String FULL_DOCUMENT = "Full document";
    private static final String XADES_FORMAT_PREFIX = "XAdES_BASELINE_";
    private static final String REPORT_INDICATION_INDETERMINATE = "INDETERMINATE";
    private static final String BDOC_SIGNATURE_FORM = "ASiC-E";
    private static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";
    private static final String DDOC_HASHCODE_SIGNATURE_FORM_SUFFIX = "_hashcode";
    private static final String HASHCODE_CONTENT_TYPE = "HASHCODE";
    public static final String DDOC_TIMESTAMP_WARNING = "Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa";


    private Container container;
    private ValidationDocument validationDocument;
    private ValidationPolicy validationPolicy;
    private List<DigiDoc4JException> containerErrors;
    private boolean isReportSignatureEnabled;

    public TimemarkContainerValidationReportBuilder(Container container, ValidationDocument validationDocument, ValidationPolicy validationPolicy, List<DigiDoc4JException> containerErrors, boolean isReportSignatureEnabled) {
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
        if (!(container instanceof DDocContainer)) {
            processSignatureIndications(validationConclusion, validationPolicy.getName());
        }
        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        DetailedReport detailedReport = new DetailedReport(validationConclusion, null);
        return new Reports(simpleReport, detailedReport);
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());

        validationConclusion.setSignatureForm(getSignatureForm());
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
        validationWarnings.removeIf(s -> container.getSignatures().stream().anyMatch(sig -> getErrors(sig).stream().anyMatch(err -> err.getContent().equals(s.getContent()))));
        validationWarnings.addAll(getValidationWarningsForUnsignedDataFiles());
        return validationWarnings;
    }

    private List<ValidationWarning> getValidationWarningsForUnsignedDataFiles() {
        if (!(container instanceof DDocContainer)) {
            List<String> dataFilenames = container.getDataFiles().stream().map(DataFile:: getName).collect(Collectors.toList());
            return container.getSignatures()
                    .stream()
                    .map(signature -> createValidationWarning(signature, getUnsignedFiles((AsicESignature) signature, dataFilenames)))
                    .filter(Objects :: nonNull)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
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
                .filter(dataFilenames :: contains)
                .collect(Collectors.toList());
        return dataFilenames.stream()
                .filter(df -> !uris.contains(df))
                .collect(Collectors.toList());
    }

    private List<SignatureValidationData> createSignaturesForReport(Container container) {
        List<String> dataFilenames = container.getDataFiles().stream().map(DataFile :: getName).collect(Collectors.toList());
        return container.getSignatures().stream().map(sig -> createSignatureValidationData(sig, dataFilenames)).collect(Collectors.toList());
    }

    private SignatureValidationData createSignatureValidationData(Signature signature, List<String> dataFilenames) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();

        signatureValidationData.setId(signature.getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat(signature.getProfile()));
        signatureValidationData.setSignatureLevel(getSignatureLevel(signature));
        signatureValidationData.setSignedBy(removeQuotes(signature.getSigningCertificate().getSubjectName(CN)));
        signatureValidationData.setErrors(getErrors(signature));
        signatureValidationData.setSignatureScopes(getSignatureScopes(signature, dataFilenames));
        signatureValidationData.setClaimedSigningTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(signature.getClaimedSigningTime()));
        signatureValidationData.setWarnings(getWarnings(signature));
        signatureValidationData.setInfo(getInfo(signature));
        signatureValidationData.setIndication(getIndication(signature));
        signatureValidationData.setSubIndication(getSubIndication(signature));
        signatureValidationData.setCountryCode(getCountryCode(signature));

        return signatureValidationData;
    }

    private String removeQuotes(String subjectName) {
        return subjectName.replaceAll("^\"|\"$", "");
    }

    private String getSignatureLevel(Signature signature) {
        if (signature instanceof AsicESignature) {
            SignatureQualification signatureLevel = getDssSimpleReport((AsicESignature) signature).getSignatureQualification(signature.getId());
            return signatureLevel != null ? signatureLevel.name() : "";
        }
        return null;
    }

    private eu.europa.esig.dss.validation.reports.SimpleReport getDssSimpleReport(AsicESignature bDocSignature) {
        return bDocSignature.getDssValidationReport().getReports().getSimpleReport();
    }

    private SignatureValidationData.Indication getIndication(Signature signature) {
        ValidationResult validationResult = signature.validateSignature();
        if (validationResult.isValid()) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (signature instanceof AsicESignature && REPORT_INDICATION_INDETERMINATE.equals(getDssSimpleReport((AsicESignature) signature).getIndication(signature.getId()).name())) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    private String getSubIndication(Signature signature) {
        if (signature instanceof AsicESignature) {
            if (getIndication(signature) == SignatureValidationData.Indication.TOTAL_PASSED) {
                return "";
            }
            SubIndication subindication = getDssSimpleReport((AsicESignature) signature).getSubIndication(signature.getId());
            return subindication != null ? subindication.name() : "";
        }
        return "";
    }

    private Info getInfo(Signature signature) {
        Info info = new Info();
        Date trustedTime = signature.getTrustedSigningTime();
        if (trustedTime != null) {
            info.setBestSignatureTime(ReportBuilderUtils.getDateFormatterWithGMTZone().format(trustedTime));
        } else {
            info.setBestSignatureTime("");
        }
        return info;
    }

    private List<Warning> getWarnings(Signature signature) {
        return signature.validateSignature().getWarnings()
                .stream()
                .map(TimemarkContainerValidationReportBuilder:: mapDigidoc4JWarning)
                .collect(Collectors.toList());

    }

    private List<SignatureScope> getSignatureScopes(Signature signature, List<String> dataFilenames) {
        if (signature instanceof DDocSignature) {
            return dataFilenames
                    .stream()
                    .map(this :: mapDataFile)
                    .collect(Collectors.toList());
        } else {
            AsicESignature bDocSignature = (AsicESignature) signature;
            return bDocSignature.getOrigin().getReferences()
                    .stream()
                    .map(r -> decodeUriIfPossible(r.getURI()))
                    .filter(dataFilenames :: contains) //filters out Signed Properties
                    .map(TimemarkContainerValidationReportBuilder:: createFullSignatureScopeForDataFile)
                    .collect(Collectors.toList());
        }

    }

    private SignatureScope mapDataFile(String filename) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(filename);
        signatureScope.setContent(FULL_DOCUMENT);
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        return signatureScope;
    }

    private String decodeUriIfPossible(String uri) {
        try {
            return URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("datafile " + uri + " has unsupported encoding", e);
            return uri;
        }
    }

    private List<Error> getErrors(Signature signature) {
        return signature.validateSignature().getErrors()
                .stream()
                .map(TimemarkContainerValidationReportBuilder:: mapDigidoc4JException)
                .collect(Collectors.toList());
    }

    private String getSignatureFormat(SignatureProfile profile) {
        if (container instanceof DDocContainer) {
            DDocFacade dDocFacade = ((DDocContainer) container).getDDoc4JFacade();
            return dDocFacade.getFormat().replaceAll("-", "_") + "_" + dDocFacade.getVersion();
        }
        return XADES_FORMAT_PREFIX + profile.toString();
    }

    private String getSignatureForm() {
        if (container instanceof DDocContainer) {
            return getDigidocXmlSignatureForm();
        }
        return BDOC_SIGNATURE_FORM;
    }

    private String getDigidocXmlSignatureForm() {
        return DDOC_SIGNATURE_FORM_PREFIX + ((DDocContainer) container).getDDoc4JFacade().getVersion() + getSignatureFormSuffix();
    }

    private String getSignatureFormSuffix() {
        return isHashcodeForm() ? DDOC_HASHCODE_SIGNATURE_FORM_SUFFIX : StringUtils.EMPTY;
    }

    private boolean isHashcodeForm() {
        DDocFacade ddocFacade = ((DDocContainer) container).getDDoc4JFacade();
        return ddocFacade.getDataFiles().stream().anyMatch(dataFile -> {
                    if (dataFile instanceof DigestDataFile) {
                        return HASHCODE_CONTENT_TYPE.equals(((DigestDataFile) dataFile).getContentType());
                    }
                    return false;
                }
        );
    }

    private String getCountryCode(Signature signature) {
        return signature.getSigningCertificate().getSubjectName(X509Cert.SubjectName.C);
    }

}
