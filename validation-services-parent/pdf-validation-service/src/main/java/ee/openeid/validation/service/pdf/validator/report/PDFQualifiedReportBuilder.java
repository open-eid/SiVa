package ee.openeid.validation.service.pdf.validator.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.report.Conclusion;
import eu.europa.esig.dss.validation.report.Reports;
import eu.europa.esig.dss.validation.report.SimpleReport;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PDFQualifiedReportBuilder {

    public static final String NAME_ID_ATTRIBUTE = "NameId";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String SCOPE_ATTRIBUTE = "scope";
    public static final String BEST_SIGNATURE_TIME_ATTRIBUTE = "BestSignatureTime";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private Reports dssReports;
    private SimpleReport simpleReport;
    private QualifiedReport report;
    private LocalDateTime validationTime;
    private String documentName;

    public PDFQualifiedReportBuilder(Reports dssReports, LocalDateTime validationTime, String documentName) {
        this.dssReports = dssReports;
        this.simpleReport = dssReports.getSimpleReport();
        this.validationTime = validationTime;
        this.documentName = documentName;
    }

    public QualifiedReport build() {
        report = new QualifiedReport();
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
            info.setNameId(dssInfo.get(0).getAttributeValue(NAME_ID_ATTRIBUTE));
            info.setBestSignatureTime(dssInfo.get(0).getAttributeValue(BEST_SIGNATURE_TIME_ATTRIBUTE));
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
        error.setContent(dssError.getValue());
        error.setNameId(dssError.getAttributeValue(NAME_ID_ATTRIBUTE));
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
        warning.setDescription(dssWarning.getValue());
        warning.setNameId(dssWarning.getAttributeValue(NAME_ID_ATTRIBUTE));
        return warning;
    }

    private List<SignatureScope> parseSignatureScopes(String signatureId) {
        return simpleReport.getElements("/SimpleReport/Signature[@Id='%s']/SignatureScopes/SignatureScope/", signatureId)
                .stream()
                .map(this::parseSignatureScope)
                .collect(Collectors.toList());
    }

    private SignatureScope parseSignatureScope(XmlDom signatureScopeDom) {
        SignatureScope scope = new SignatureScope();
        scope.setContent(signatureScopeDom.getText());
        scope.setName(signatureScopeDom.getAttribute(NAME_ATTRIBUTE));
        scope.setScope(signatureScopeDom.getAttribute(SCOPE_ATTRIBUTE));
        return scope;
    }

    private String parseClaimedSigningTime(String signatureId) {
        return simpleReport.getValue("/SimpleReport/Signature[@Id='%s']/SigningTime/text()", signatureId);
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

    private String parseSignedBy(String signatureId) {
        return simpleReport.getValue("/SimpleReport/Signature[@Id=\'%s\']/SignedBy/text()", signatureId);
    }

    private String parseValidationTimeToString() {
        return getFormattedTimeValue(validationTime);
    }

    private String getFormattedTimeValue(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT).withZone(ZoneId.of("GMT"));
        return localDateTime.format(formatter);
    }
}
