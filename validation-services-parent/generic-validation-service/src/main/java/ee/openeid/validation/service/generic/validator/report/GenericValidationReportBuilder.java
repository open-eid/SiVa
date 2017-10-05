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

package ee.openeid.validation.service.generic.validator.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.jaxb.diagnostic.XmlSignatureScope;
import eu.europa.esig.dss.validation.SignatureQualification;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.reports.wrapper.CertificateWrapper;
import eu.europa.esig.dss.validation.reports.wrapper.TimestampWrapper;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;

public class GenericValidationReportBuilder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String QES_POLICY = "POLv4";
    private static final String SIGNATURE_LEVEL_ERROR = "Signature/seal level do not meet the minimal level required by applied policy";
    private static final String SIGNATURE_LEVEL_WARNING = "The signature is not in the Qualified Electronic Signature level";
    private eu.europa.esig.dss.validation.reports.Reports dssReports;
    private ZonedDateTime validationTime;
    private ValidationDocument validationDocument;
    private ConstraintDefinedPolicy validationPolicy;
    private ValidationLevel validationLevel;

    public GenericValidationReportBuilder(eu.europa.esig.dss.validation.reports.Reports dssReports, ZonedDateTime validationTime, ValidationLevel validationLevel, ValidationDocument validationDocument, ConstraintDefinedPolicy policy) {
        this.dssReports = dssReports;
        this.validationTime = validationTime;
        this.validationDocument = validationDocument;
        this.validationPolicy = policy;
        this.validationLevel = validationLevel;
    }

    public Reports build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        processSignatureIndications(validationConclusion);

        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        validationConclusion.setValidationLevel(validationLevel.name());
        DetailedReport detailedReport = new DetailedReport(validationConclusion, dssReports.getDetailedReportJaxb());
        return new Reports(simpleReport, detailedReport);
    }

    void processSignatureIndications(ValidationConclusion validationConclusion) {
        if (QES_POLICY.equals(validationPolicy.getName())) {
            for (SignatureValidationData signature : validationConclusion.getSignatures()) {
                if (SignatureValidationData.Indication.TOTAL_PASSED.toString().equals(signature.getIndication())) {
                    String signatureLevel = signature.getSignatureLevel();
                    if (SignatureQualification.ADESEAL_QC.name().equals(signatureLevel) || SignatureQualification.QES.name().equals(signatureLevel)
                            || SignatureQualification.QESIG.name().equals(signatureLevel) || SignatureQualification.QESEAL.name().equals(signatureLevel)) {
                        continue;
                    } else if (SignatureQualification.ADESIG_QC.name().equals(signatureLevel)) {
                        signature.getWarnings().add(getSignatureLevelWarning());
                        continue;
                    }
                    signature.setIndication(SignatureValidationData.Indication.TOTAL_FAILED);
                    signature.getErrors().add(getSignatureLevelNotAcceptedError());
                    validationConclusion.setValidSignaturesCount(validationConclusion.getValidSignaturesCount() - 1);
                }
            }
        }
    }

    private Error getSignatureLevelNotAcceptedError() {
        Error error = new Error();
        error.setContent(SIGNATURE_LEVEL_ERROR);
        return error;
    }

    private Warning getSignatureLevelWarning() {
        Warning warning = new Warning();
        warning.setContent(SIGNATURE_LEVEL_WARNING);
        return warning;
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(parseValidationTimeToString());
        validationConclusion.setSignatureForm(getContainerType());
        validationConclusion.setValidationWarnings(Collections.emptyList());
        validationConclusion.setSignatures(buildSignatureValidationDataList());
        validationConclusion.setSignaturesCount(validationConclusion.getSignatures().size());
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(validationDocument.getName(), validationDocument.getBytes()));
        validationConclusion.setValidSignaturesCount(validationConclusion.getSignatures()
                .stream()
                .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                .collect(Collectors.toList())
                .size());
        return validationConclusion;
    }

    private List<SignatureValidationData> buildSignatureValidationDataList() {
        return dssReports.getSimpleReport().getSignatureIdList()
                .stream()
                .map(this::buildSignatureValidationData)
                .collect(Collectors.toList());
    }

    private String getContainerType() {
        if (dssReports.getDiagnosticData().getContainerInfo() != null)
            return dssReports.getDiagnosticData().getContainerInfo().getContainerType();
        return null;
    }

    private SignatureValidationData buildSignatureValidationData(String signatureId) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        signatureValidationData.setId(signatureId);
        signatureValidationData.setSignatureFormat(dssReports.getSimpleReport().getSignatureFormat(signatureId));
        signatureValidationData.setSignatureLevel(dssReports.getSimpleReport().getSignatureQualification(signatureId).name());
        signatureValidationData.setSignedBy(parseSignedBy(signatureId));
        signatureValidationData.setClaimedSigningTime(parseClaimedSigningTime(signatureId));
        signatureValidationData.setSignatureScopes(parseSignatureScopes(signatureId));
        signatureValidationData.setErrors(parseSignatureErrors(signatureId));
        signatureValidationData.setWarnings(parseSignatureWarnings(signatureId));
        signatureValidationData.setInfo(parseSignatureInfo(signatureId));
        signatureValidationData.setCountryCode(getCountryCode());
        signatureValidationData.setIndication(parseIndication(signatureId, signatureValidationData.getErrors()));
        signatureValidationData.setSubIndication(parseSubIndication(signatureId, signatureValidationData.getErrors()));

        return signatureValidationData;
    }

    private Info parseSignatureInfo(String signatureId) {
        List<TimestampWrapper> timeStamps = dssReports.getDiagnosticData().getSignatureById(signatureId).getTimestampList();
        Date bestSignatureTime = timeStamps.isEmpty() ? null : timeStamps.get(0).getProductionTime();
        Info info = new Info();
        info.setBestSignatureTime(bestSignatureTime == null ? "" : ReportBuilderUtils.getDateFormatterWithGMTZone().format(bestSignatureTime));
        return info;
    }

    private List<Error> parseSignatureErrors(String signatureId) {
        return dssReports.getSimpleReport().getErrors(signatureId)
                .stream()
                .map(this::mapDssError)
                .collect(Collectors.toList());
    }

    private Error mapDssError(String dssError) {
        Error error = new Error();
        error.setContent(emptyWhenNull(dssError));
        return error;
    }

    private List<Warning> parseSignatureWarnings(String signatureId) {
        return dssReports.getSimpleReport().getWarnings(signatureId)
                .stream()
                .map(this::mapDssWarning)
                .collect(Collectors.toList());
    }

    private Warning mapDssWarning(String dssWarning) {
        Warning warning = new Warning();
        warning.setContent(emptyWhenNull(dssWarning));
        return warning;
    }

    private List<SignatureScope> parseSignatureScopes(String signatureId) {
        return dssReports.getDiagnosticData().getSignatureById(signatureId).getSignatureScopes()
                .stream()
                .map(this::parseSignatureScope)
                .collect(Collectors.toList());
    }

    private SignatureScope parseSignatureScope(XmlSignatureScope dssSignatureScope) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setContent(emptyWhenNull(dssSignatureScope.getValue()));
        signatureScope.setName(emptyWhenNull(dssSignatureScope.getName()));
        signatureScope.setScope(emptyWhenNull(dssSignatureScope.getScope()));
        return signatureScope;
    }

    private String parseClaimedSigningTime(String signatureId) {
        return emptyWhenNull(ReportBuilderUtils.getDateFormatterWithGMTZone().format(dssReports.getSimpleReport().getSigningTime(signatureId)));
    }

    private SignatureValidationData.Indication parseIndication(String signatureId, List<Error> errors) {
        Indication dssIndication = dssReports.getSimpleReport().getIndication(signatureId);
        if (errors.isEmpty() && StringUtils.equalsIgnoreCase(dssIndication.name(), Indication.TOTAL_PASSED.name())) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (StringUtils.equalsIgnoreCase(dssIndication.name(), Indication.INDETERMINATE.name())) {
            return SignatureValidationData.Indication.INDETERMINATE;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    private String parseSubIndication(String signatureId, List<Error> errors) {
        if (parseIndication(signatureId, errors) == SignatureValidationData.Indication.TOTAL_PASSED) {
            return "";
        }
        SubIndication subindication = dssReports.getSimpleReport().getSubIndication(signatureId);
        return subindication != null ? subindication.name() : "";
    }

    private String parseSignedBy(String signatureId) {
        return emptyWhenNull(dssReports.getSimpleReport().getSignedBy(signatureId));
    }

    private String parseValidationTimeToString() {
        return getFormattedTimeValue(validationTime);
    }

    private String getFormattedTimeValue(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return zonedDateTime.format(formatter);
    }

    private String getCountryCode() {
        String signingCertId = dssReports.getDiagnosticData().getSigningCertificateId();
        Optional<String> countryCode = dssReports.getDiagnosticData().getUsedCertificates().stream()
                .filter(cert -> cert.getId().equals(signingCertId))
                .map(CertificateWrapper::getCountryName)
                .findFirst();
        return countryCode.orElse(null);
    }

    public void setValidationPolicy(ConstraintDefinedPolicy validationPolicy) {
        this.validationPolicy = validationPolicy;
    }
}
