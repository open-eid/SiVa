package ee.openeid.validation.service.timestamptoken.validator.report;

import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getDateFormatterWithGMTZone;

public class TimeStampTokenQualifiedReportBuilder {

    private static final String ASICS_SIGNATURE_FORMAT = "ASICS";

    private String documentName;
    private Date validationTime;
    private ValidationPolicy validationPolicy;
    private TimeStampTokenValidationData timeStampTokenValidationData;

    public TimeStampTokenQualifiedReportBuilder(String documentName, Date validationTime, ValidationPolicy validationPolicy, TimeStampTokenValidationData timeStampTokenValidationData) {
        this.documentName = documentName;
        this.validationTime = validationTime;
        this.validationPolicy = validationPolicy;
        this.timeStampTokenValidationData = timeStampTokenValidationData;
    }

    public QualifiedReport build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        return new QualifiedReport(new SimpleReport(validationConclusion), new DetailedReport(validationConclusion, null));
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setDocumentName(documentName);
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getDateFormatterWithGMTZone().format(validationTime));
        validationConclusion.setSignatureForm(ASICS_SIGNATURE_FORMAT);
        List<TimeStampTokenValidationData> timeStampTokenValidationDataList = new ArrayList<>();
        timeStampTokenValidationDataList.add(timeStampTokenValidationData);
        validationConclusion.setTimeStampTokens(timeStampTokenValidationDataList);
        return validationConclusion;
    }

}
