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

public class PDFQualifiedReportBuilder {

    private static final String NAME_ID_ATTRIBUTE = "NameId";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String SCOPE_ATTRIBUTE = "scope";
    private static final String BEST_SIGNATURE_TIME_ATTRIBUTE = "BestSignatureTime";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private Reports dssReports;
    private SimpleReport simpleReport;
    private QualifiedReport report;
    private ZonedDateTime validationTime;
    private String documentName;

    public PDFQualifiedReportBuilder(Reports dssReports, ZonedDateTime validationTime, String documentName) {
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
            String bestSignatureTime = dssInfo.get(0).getAttributeValue(BEST_SIGNATURE_TIME_ATTRIBUTE);
            info.setBestSignatureTime(bestSignatureTime != null ? bestSignatureTime : "");
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
        String content = dssError.getValue();
        String nameId = dssError.getAttributeValue(NAME_ID_ATTRIBUTE);
        Error error = new Error();
        error.setContent(content != null ? content : "");
        error.setNameId(nameId != null ? nameId : "");
        return error;
    }

    private List<Warning> parseSignatureWarnings(String signatureId) {
        return simpleReport.getWarnings(signatureId)
                .stream()
                .map(this::mapDssWarning)
                .collect(Collectors.toList());
    }

    private Warning mapDssWarning(Conclusion.BasicInfo dssWarning) {
        String description = dssWarning.getValue();
        String nameId = dssWarning.getAttributeValue(NAME_ID_ATTRIBUTE);
        Warning warning = new Warning();
        warning.setDescription(description != null ? description : "");
        warning.setNameId(nameId != null ? nameId : "");
        return warning;
    }

    private List<SignatureScope> parseSignatureScopes(String signatureId) {
        return simpleReport.getElements("/SimpleReport/Signature[@Id='%s']/SignatureScopes/SignatureScope/", signatureId)
                .stream()
                .map(this::parseSignatureScope)
                .collect(Collectors.toList());
    }

    private SignatureScope parseSignatureScope(XmlDom signatureScopeDom) {
        String content = signatureScopeDom.getText();
        String name = signatureScopeDom.getAttribute(NAME_ATTRIBUTE);
        String scope = signatureScopeDom.getAttribute(SCOPE_ATTRIBUTE);

        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setContent(content != null ? content : "");
        signatureScope.setName(name != null ? name : "");
        signatureScope.setScope(scope != null ? scope : "");
        return signatureScope;
    }

    private String parseClaimedSigningTime(String signatureId) {
        String claimedSigningTime = simpleReport.getValue("/SimpleReport/Signature[@Id='%s']/SigningTime/text()", signatureId);
        return claimedSigningTime != null ? claimedSigningTime : "";
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
        String subIndication = simpleReport.getSubIndication(signatureId);
        return subIndication != null ? subIndication : "";
    }

    private String parseSignedBy(String signatureId) {
        String signedBy = simpleReport.getValue("/SimpleReport/Signature[@Id=\'%s\']/SignedBy/text()", signatureId);
        return signedBy != null ? signedBy : "";
    }

    private String parseValidationTimeToString() {
        return getFormattedTimeValue(validationTime);
    }

    private String getFormattedTimeValue(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return zonedDateTime.format(formatter);
    }
}
