package ee.openeid.validation.service.pdf.validator.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import eu.europa.esig.dss.jaxb.diagnostic.XmlSignatureScopeType;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.validation.reports.wrapper.TimestampWrapper;
import org.apache.commons.lang.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;

public class PDFQualifiedReportBuilder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String PDF_SIGNATURE_FORM = "PAdES";

    private Reports dssReports;
    private ZonedDateTime validationTime;
    private String documentName;
    private ValidationPolicy validationPolicy;

    public PDFQualifiedReportBuilder(Reports dssReports, ZonedDateTime validationTime, String documentName, ValidationPolicy policy) {
        this.dssReports = dssReports;
        this.validationTime = validationTime;
        this.documentName = documentName;
        this.validationPolicy = policy;
    }

    public QualifiedReport build() {
        QualifiedReport report = new QualifiedReport();
        report.setPolicy(createReportPolicy(validationPolicy));
        report.setValidationTime(parseValidationTimeToString());
        report.setDocumentName(documentName);
        report.setSignatureForm(PDF_SIGNATURE_FORM);
        report.setSignatures(buildSignatureValidationDataList());
        report.setSignaturesCount(report.getSignatures().size());
        report.setValidSignaturesCount(report.getSignatures()
                .stream()
                .filter(vd -> StringUtils.equals(vd.getIndication(), SignatureValidationData.Indication.TOTAL_PASSED.toString()))
                .collect(Collectors.toList())
                .size());

        return report;
    }

    private List<SignatureValidationData> buildSignatureValidationDataList() {
        return dssReports.getSimpleReport().getSignatureIdList()
                .stream()
                .map(this::buildSignatureValidationData)
                .collect(Collectors.toList());
    }

    private SignatureValidationData buildSignatureValidationData(String signatureId) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();
        signatureValidationData.setId(signatureId);
        signatureValidationData.setSignatureFormat(dssReports.getSimpleReport().getSignatureFormat(signatureId));
        signatureValidationData.setSignatureLevel(dssReports.getSimpleReport().getSignatureLevel(signatureId).name());
        signatureValidationData.setSignedBy(parseSignedBy(signatureId));
        signatureValidationData.setIndication(parseIndication(signatureId));
        signatureValidationData.setSubIndication(parseSubIndication(signatureId));
        signatureValidationData.setClaimedSigningTime(parseClaimedSigningTime(signatureId));
        signatureValidationData.setSignatureScopes(parseSignatureScopes(signatureId));
        signatureValidationData.setErrors(parseSignatureErrors(signatureId));
        signatureValidationData.setWarnings(parseSignatureWarnings(signatureId));
        signatureValidationData.setInfo(parseSignatureInfo(signatureId));
        signatureValidationData.setCountryCode(getCountryCode());

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
        warning.setDescription(emptyWhenNull(dssWarning));
        return warning;
    }

    private List<SignatureScope> parseSignatureScopes(String signatureId) {
        return dssReports.getDiagnosticData().getSignatureById(signatureId).getSignatureScopes().getSignatureScope()
                .stream()
                .map(this::parseSignatureScope)
                .collect(Collectors.toList());
    }

    private SignatureScope parseSignatureScope(XmlSignatureScopeType dssSignatureScope) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setContent(emptyWhenNull(dssSignatureScope.getValue()));
        signatureScope.setName(emptyWhenNull(dssSignatureScope.getName()));
        signatureScope.setScope(emptyWhenNull(dssSignatureScope.getScope()));
        return signatureScope;
    }

    private String parseClaimedSigningTime(String signatureId) {
        return emptyWhenNull(ReportBuilderUtils.getDateFormatterWithGMTZone().format(dssReports.getSimpleReport().getSigningTime(signatureId)));
    }

    private SignatureValidationData.Indication parseIndication(String signatureId) {
        Indication indication = dssReports.getSimpleReport().getIndication(signatureId);
        if (StringUtils.equalsIgnoreCase(indication.name(), Indication.TOTAL_PASSED.name())) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (StringUtils.equalsIgnoreCase(indication.name(), Indication.TOTAL_FAILED.name())) {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        } else {
            return SignatureValidationData.Indication.INDETERMINATE;
        }
    }

    private String parseSubIndication(String signatureId) {
        if (parseIndication(signatureId) == SignatureValidationData.Indication.TOTAL_PASSED) {
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
                .filter( cert -> cert.getId().equals(signingCertId) )
                .map( cert -> cert.getCountryName() )
                .findFirst();
        return countryCode.isPresent() ? countryCode.get() : null;
    }
}
