/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.timestamptoken.validator.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.DiagnosticReport;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;

import java.util.ArrayList;
import java.util.List;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getValidationTime;

public class TimeStampTokenValidationReportBuilder {

    private static final String ASICS_SIGNATURE_FORMAT = "ASiC-S";

    private ValidationDocument validationDocument;
    private ValidationPolicy validationPolicy;
    private TimeStampTokenValidationData timeStampTokenValidationData;
    private boolean isReportSignatureEnabled;

    public TimeStampTokenValidationReportBuilder(ValidationDocument validationDocument, ValidationPolicy validationPolicy, TimeStampTokenValidationData timeStampTokenValidationData, boolean isReportSignatureEnabled) {
        this.validationDocument = validationDocument;
        this.validationPolicy = validationPolicy;
        this.timeStampTokenValidationData = timeStampTokenValidationData;
        this.isReportSignatureEnabled = isReportSignatureEnabled;
    }

    public Reports build() {
        ValidationConclusion validationConclusion = getValidationConclusion();
        SimpleReport simpleReport = new SimpleReport(validationConclusion);
        DetailedReport detailedReport = new DetailedReport(validationConclusion, null);
        DiagnosticReport diagnosticReport = new DiagnosticReport(validationConclusion, null);
        return new Reports(simpleReport, detailedReport, diagnosticReport);
    }

    private ValidationConclusion getValidationConclusion() {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        validationConclusion.setPolicy(createReportPolicy(validationPolicy));
        validationConclusion.setValidationTime(getValidationTime());
        validationConclusion.setSignatureForm(ASICS_SIGNATURE_FORMAT);
        List<TimeStampTokenValidationData> timeStampTokenValidationDataList = new ArrayList<>();
        timeStampTokenValidationDataList.add(timeStampTokenValidationData);
        validationConclusion.setTimeStampTokens(timeStampTokenValidationDataList);
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
        return validationConclusion;
    }

}
