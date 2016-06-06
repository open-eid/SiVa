package ee.openeid.validation.service.pdf.validator.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.report.Conclusion;
import eu.europa.esig.dss.validation.report.Reports;
import eu.europa.esig.dss.validation.report.SimpleReport;
import org.apache.commons.lang.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;

public class PDFQualifiedReportBuilder {

    private static final String NAME_ID_ATTRIBUTE = "NameId";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String SCOPE_ATTRIBUTE = "scope";
    private static final String BEST_SIGNATURE_TIME_ATTRIBUTE = "BestSignatureTime";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private Reports dssReports;
    private SimpleReport simpleReport;
    private ZonedDateTime validationTime;
    private String documentName;

    public PDFQualifiedReportBuilder(Reports dssReports, ZonedDateTime validationTime, String documentName) {
        this.dssReports = dssReports;
        this.simpleReport = dssReports.getSimpleReport();
        this.validationTime = validationTime;
        this.documentName = documentName;
    }

    public QualifiedReport build() {
        QualifiedReport report = new QualifiedReport();
        report.setPolicy(Policy.SIVA_DEFAULT);
        report.setValidationTime(parseValidationTimeToString());
        report.setDocumentName(documentName);
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
        signatureValidationData.setSignatureFormat(simpleReport.getSignatureFormat(signatureId));
        signatureValidationData.setSignatureLevel(simpleReport.getSignatureLevel(signatureId).name());
        signatureValidationData.setSignedBy(parseSignedBy(signatureId));
        signatureValidationData.setIndication(parseIndication(signatureId));
        signatureValidationData.setSubIndication(parseSubIndication(signatureId));
        signatureValidationData.setClaimedSigningTime(parseClaimedSigningTime(signatureId));
        signatureValidationData.setSignatureScopes(parseSignatureScopes(signatureId));
        signatureValidationData.setErrors(parseSignatureErrors(signatureId));
        signatureValidationData.setWarnings(parseSignatureWarnings(signatureId));
        signatureValidationData.setInfo(parseSignatureInfo(signatureId));
        return signatureValidationData;
    }

    private Info parseSignatureInfo(String signatureId) {
        Info info = new Info();
        List<Conclusion.BasicInfo> dssInfo = simpleReport.getInfo(signatureId);
        if (dssInfo != null && !dssInfo.isEmpty()) {
            info.setBestSignatureTime(emptyWhenNull(dssInfo.get(0).getAttributeValue(BEST_SIGNATURE_TIME_ATTRIBUTE)));
        }
        return info;
    }

    private List<Error> parseSignatureErrors(String signatureId) {
        return simpleReport.getErrors(signatureId)
                .stream()
                .map(this::mapDssError)
                .collect(Collectors.toList());
    }

    private Error mapDssError(Conclusion.BasicInfo dssError) {
        Error error = new Error();
        error.setContent(emptyWhenNull(dssError.getValue()));
        error.setNameId(emptyWhenNull(dssError.getAttributeValue(NAME_ID_ATTRIBUTE)));
        return error;
    }

    private List<Warning> parseSignatureWarnings(String signatureId) {
        return simpleReport.getWarnings(signatureId)
                .stream()
                .map(this::mapDssWarning)
                .collect(Collectors.toList());
    }

    private Warning mapDssWarning(Conclusion.BasicInfo dssWarning) {
        Warning warning = new Warning();
        warning.setDescription(emptyWhenNull(dssWarning.getValue()));
        warning.setNameId(emptyWhenNull(dssWarning.getAttributeValue(NAME_ID_ATTRIBUTE)));
        return warning;
    }

    private List<SignatureScope> parseSignatureScopes(String signatureId) {
        return simpleReport.getElements("/SimpleReport/Signature[@Id='%s']/SignatureScopes/SignatureScope/", signatureId)
                .stream()
                .map(this::parseSignatureScope)
                .collect(Collectors.toList());
    }

    private SignatureScope parseSignatureScope(XmlDom signatureScopeDom) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setContent(emptyWhenNull(signatureScopeDom.getText()));
        signatureScope.setName(emptyWhenNull(signatureScopeDom.getAttribute(NAME_ATTRIBUTE)));
        signatureScope.setScope(emptyWhenNull(signatureScopeDom.getAttribute(SCOPE_ATTRIBUTE)));
        return signatureScope;
    }

    private String parseClaimedSigningTime(String signatureId) {
        return emptyWhenNull(simpleReport.getValue("/SimpleReport/Signature[@Id='%s']/SigningTime/text()", signatureId));
    }

    private SignatureValidationData.Indication parseIndication(String signatureId) {
        String indication = simpleReport.getIndication(signatureId);
        if (StringUtils.equalsIgnoreCase(indication, Indication.VALID)) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else if (StringUtils.equalsIgnoreCase(indication, Indication.INVALID)) {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        } else {
            return SignatureValidationData.Indication.INDETERMINATE;
        }
    }

    private String parseSubIndication(String signatureId) {
        if (parseIndication(signatureId) == SignatureValidationData.Indication.TOTAL_PASSED) {
            return "";
        }
        return emptyWhenNull(simpleReport.getSubIndication(signatureId));
    }

    private String parseSignedBy(String signatureId) {
        return emptyWhenNull(simpleReport.getValue("/SimpleReport/Signature[@Id=\'%s\']/SignedBy/text()", signatureId));
    }

    private String parseValidationTimeToString() {
        return getFormattedTimeValue(validationTime);
    }

    private String getFormattedTimeValue(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return zonedDateTime.format(formatter);
    }
}
