/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.ddoc.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.KeyInfo;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;

public class DDOCQualifiedReportBuilder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String FULL_DOCUMENT = "Full document";
    private static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";
    private static final String DDOC_HASHCODE_SIGNATURE_FORM_SUFFIX = "_hashcode";
    private static final String HASHCODE_CONTENT_TYPE = "HASHCODE";
    private static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
    private static final int ERR_DF_INV_HASH_GOOD_ALT_HASH = 173;
    private static final int ERR_ISSUER_XMLNS = 176;

    private SignedDoc signedDoc;
    private String documentName;
    private Date validationTime;
    private ValidationPolicy validationPolicy;

    public DDOCQualifiedReportBuilder(SignedDoc signedDoc, String documentName, Date validationTime, ValidationPolicy validationPolicy) {
        this.signedDoc = signedDoc;
        this.documentName = documentName;
        this.validationTime = validationTime;
        this.validationPolicy = validationPolicy;
    }

    public QualifiedReport build() {
        QualifiedReport qualifiedReport = new QualifiedReport();
        qualifiedReport.setPolicy(createReportPolicy(validationPolicy));
        qualifiedReport.setValidationTime(getDateFormatterWithGMTZone().format(validationTime));
        qualifiedReport.setValidationWarnings(ddocValidationWarnings());
        qualifiedReport.setDocumentName(documentName);
        qualifiedReport.setSignatureForm(getSignatureForm());
        qualifiedReport.setSignaturesCount(getSignatures(signedDoc).size());
        qualifiedReport.setSignatures(createSignaturesForReport(signedDoc));
        qualifiedReport.setValidSignaturesCount(
                qualifiedReport.getSignatures()
                        .stream()
                        .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                        .collect(Collectors.toList())
                        .size());

        return qualifiedReport;
    }

    private List<ValidationWarning> ddocValidationWarnings() {
        ValidationWarning validationWarning = new ValidationWarning();
        validationWarning.setContent("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa");
        return Collections.singletonList(validationWarning);
    }

    private SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    }

    @SuppressWarnings("unchecked")
    private List<Signature> getSignatures(SignedDoc signedDoc) {
        if (signedDoc.getSignatures() == null) {
            return new ArrayList<>();
        }
        return signedDoc.getSignatures();
    }

    @SuppressWarnings("unchecked")
    private List<SignatureValidationData> createSignaturesForReport(SignedDoc signedDoc) {
        List<Signature> signatures = getSignatures(signedDoc);
        return signatures.stream().map(this::createSignatureValidationData).collect(Collectors.toList());
    }

    private SignatureValidationData createSignatureValidationData(Signature signature) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        signatureValidationData.setId(signature.getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat());
        signatureValidationData.setSignedBy(getCertificateCN(signature.getKeyInfo()));

        ErrorsAndWarningsWrapper wrapper = getErrorsAndWarnings(signature);
        signatureValidationData.setErrors(wrapper.errors);
        signatureValidationData.setWarnings(wrapper.warnings);

        signatureValidationData.setSignatureScopes(getSignatureScopes());
        signatureValidationData.setClaimedSigningTime(getDateFormatterWithGMTZone().format(signature.getSignedProperties().getSigningTime()));
        signatureValidationData.setIndication(getIndication(signature));

        //report fields that are not applicable for ddoc
        signatureValidationData.setSignatureLevel("");
        signatureValidationData.setInfo(createEmptySignatureInfo());
        signatureValidationData.setSubIndication("");
        signatureValidationData.setCountryCode(CertUtil.getCountryCode(signature.getKeyInfo().getSignersCertificate()));

        return signatureValidationData;
    }

    private String getCertificateCN(KeyInfo keyInfo) {
        return keyInfo != null ? org.cryptacular.util.CertUtil.subjectCN(keyInfo.getSignersCertificate()) : null;
    }

    private String getSignatureForm() {
        return DDOC_SIGNATURE_FORM_PREFIX + signedDoc.getVersion() + getSignatureFormSuffix();
    }

    private String getSignatureFormSuffix() {
        return isHashcodeForm() ? DDOC_HASHCODE_SIGNATURE_FORM_SUFFIX : StringUtils.EMPTY;
    }

    @SuppressWarnings("unchecked")
    private boolean isHashcodeForm() {
        List<DataFile> dataFiles = signedDoc.getDataFiles();
        return dataFiles != null && !dataFiles.isEmpty() && dataFiles
                .stream()
                .filter(df -> StringUtils.equalsIgnoreCase(HASHCODE_CONTENT_TYPE, df.getContentType()))
                .count() > 0;
    }

    private Info createEmptySignatureInfo() {
        Info info = new Info();
        info.setBestSignatureTime("");
        return info;
    }

    private String getSignatureFormat() {
        return signedDoc.getFormat().replaceAll("-", "_") + "_" + signedDoc.getVersion();
    }

    @SuppressWarnings("unchecked")
    private ErrorsAndWarningsWrapper getErrorsAndWarnings(Signature signature) {
        List<DigiDocException> digidocErrors = signature.validate();
        digidocErrors.addAll(signature.verify(signedDoc, true, true));
        ErrorsAndWarningsWrapper wrapper = new ErrorsAndWarningsWrapper();
        wrapper.errors = digidocErrors
                        .stream()
                        .filter(dde -> !isWarning(dde))
                        .map(this::mapDigiDocExceptionToError)
                        .collect(Collectors.toList());
        wrapper.warnings = digidocErrors
                        .stream()
                        .filter(this::isWarning)
                        .map(this::mapDigiDocExceptionToWarning)
                        .collect(Collectors.toList());
        return wrapper;
    }

    private Warning mapDigiDocExceptionToWarning(DigiDocException dde) {
        Warning warning = new Warning();
        warning.setDescription(emptyWhenNull(dde.getMessage()));
        return warning;
    }

    private boolean isWarning(DigiDocException dde) {
        return Arrays.asList(ERR_DF_INV_HASH_GOOD_ALT_HASH, ERR_ISSUER_XMLNS).contains(dde.getCode());
    }

    private Error mapDigiDocExceptionToError(DigiDocException dde) {
        Error error = new Error();
        error.setContent(emptyWhenNull(dde.getMessage()));
        return error;
    }

    @SuppressWarnings("unchecked")
    private List<SignatureScope> getSignatureScopes() {
        List<DataFile> dataFiles = signedDoc.getDataFiles();
        if (dataFiles == null) {
            return Collections.emptyList();
        }
        return dataFiles
                .stream()
                .map(this::mapDataFile)
                .collect(Collectors.toList());
    }

    private SignatureScope mapDataFile(DataFile dataFile) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(dataFile.getFileName());
        signatureScope.setContent(FULL_DOCUMENT);
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        return signatureScope;
    }

    @SuppressWarnings("unchecked")
    private SignatureValidationData.Indication getIndication(Signature signature) {
        if (containsErrors(signature.validate())) {
            // TODO: Should we always return 'INDETERMINATE' in this case or are there cases when we should give here 'TOTAL_FAILED'?
            return SignatureValidationData.Indication.INDETERMINATE;
        } else if (containsErrors(signature.verify(signedDoc, true, true))) {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }

        return SignatureValidationData.Indication.TOTAL_PASSED;
    }

    private boolean containsErrors(List<DigiDocException> exceptions) {
        return !exceptions.isEmpty() && exceptions.stream().filter(e -> !isWarning(e)).count() != 0;
    }

    private class ErrorsAndWarningsWrapper {
        private List<Error> errors;
        private List<Warning> warnings;
    }

}
