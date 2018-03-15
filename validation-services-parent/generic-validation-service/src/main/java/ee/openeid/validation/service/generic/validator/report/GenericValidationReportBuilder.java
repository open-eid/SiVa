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

package ee.openeid.validation.service.generic.validator.report;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.FORMAT_NOT_FOUND;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getValidationTime;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.processSignatureIndications;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.valueNotKnown;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.jaxb.diagnostic.XmlSignatureScope;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.reports.wrapper.CertificateWrapper;
import eu.europa.esig.dss.validation.reports.wrapper.TimestampWrapper;

public class GenericValidationReportBuilder {

    private static final String SIGNATURE_FORMAT_ALLOWED = "LT";
    private static final String SIGNATURE_FORMAT_NOT_ALLOWED = "B";

    protected eu.europa.esig.dss.validation.reports.Reports dssReports;
    protected ValidationDocument validationDocument;
    protected ConstraintDefinedPolicy validationPolicy;
    protected ValidationLevel validationLevel;
    protected boolean isReportSignatureEnabled;

    public GenericValidationReportBuilder(eu.europa.esig.dss.validation.reports.Reports dssReports, ValidationLevel validationLevel, ValidationDocument validationDocument, ConstraintDefinedPolicy policy, boolean isReportSignatureEnabled) {
        this.dssReports = dssReports;
        this.validationDocument = validationDocument;
        this.validationPolicy = policy;
        this.validationLevel = validationLevel;
        this.isReportSignatureEnabled = isReportSignatureEnabled;
    }

    public Reports build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        processSignatureIndications(validationConclusion, validationPolicy.getName());
        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        validationConclusion.setValidationLevel(validationLevel.name());
        DetailedReport detailedReport = new DetailedReport(validationConclusion, dssReports.getDetailedReportJaxb());
        return new Reports(simpleReport, detailedReport);
    }

    protected ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());
        validationConclusion.setSignatureForm(getContainerType());
        validationConclusion.setValidationWarnings(Collections.emptyList());
        validationConclusion.setSignatures(buildSignatureValidationDataList());
        validationConclusion.setSignaturesCount(validationConclusion.getSignatures().size());
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
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
        signatureValidationData.setSignatureFormat(changeSignatureFormat(dssReports.getSimpleReport().getSignatureFormat(signatureId), signatureId));
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

    protected String changeSignatureFormat(String signatureFormat, String signatureId) {
        String modifiedSignatureFormat = signatureFormat;
        if (dssReports.getSimpleReport().getErrors(signatureId).contains(FORMAT_NOT_FOUND))
            modifiedSignatureFormat = modifiedSignatureFormat.replace(SIGNATURE_FORMAT_ALLOWED, SIGNATURE_FORMAT_NOT_ALLOWED);
        modifiedSignatureFormat = modifiedSignatureFormat.replace("-", "_");
        return modifiedSignatureFormat;
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
        Date signingDate = dssReports.getSimpleReport().getSigningTime(signatureId);
        if (signingDate == null)
            return valueNotKnown();
        return emptyWhenNull(ReportBuilderUtils.getDateFormatterWithGMTZone().format(signingDate));
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

    private String getCountryCode() {
        String signingCertId = dssReports.getDiagnosticData().getSigningCertificateId();
        Optional<String> countryCode = dssReports.getDiagnosticData().getUsedCertificates().stream()
            .filter(cert -> cert.getId().equals(signingCertId))
            .map(CertificateWrapper::getCountryName)
            .findFirst();
        return countryCode.orElse(null);
    }
}
