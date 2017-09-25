package ee.openeid.validation.service.timestamptoken.validator.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getDateFormatterWithGMTZone;

public class TimeStampTokenValidationReportBuilder {

    private static final String ASICS_SIGNATURE_FORMAT = "ASiC-S";

    private ValidationDocument validationDocument;
    private Date validationTime;
    private ValidationPolicy validationPolicy;
    private TimeStampTokenValidationData timeStampTokenValidationData;

    public TimeStampTokenValidationReportBuilder(ValidationDocument validationDocument, Date validationTime, ValidationPolicy validationPolicy, TimeStampTokenValidationData timeStampTokenValidationData) {
        this.validationDocument = validationDocument;
        this.validationTime = validationTime;
        this.validationPolicy = validationPolicy;
        this.timeStampTokenValidationData = timeStampTokenValidationData;
    }

    public Reports build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        DetailedReport detailedReport = new DetailedReport(validationConclusion, null);
        return new Reports(simpleReport, detailedReport);
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getDateFormatterWithGMTZone().format(validationTime));
        validationConclusion.setSignatureForm(ASICS_SIGNATURE_FORMAT);
        List<TimeStampTokenValidationData> timeStampTokenValidationDataList = new ArrayList<>();
        timeStampTokenValidationDataList.add(timeStampTokenValidationData);
        validationConclusion.setTimeStampTokens(timeStampTokenValidationDataList);
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(validationDocument.getName(), validationDocument.getBytes()));
        return validationConclusion;
    }

}
