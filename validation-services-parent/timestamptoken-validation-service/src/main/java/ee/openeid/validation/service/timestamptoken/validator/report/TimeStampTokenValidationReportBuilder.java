/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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
import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.DetailedReport;
import ee.openeid.siva.validation.document.report.DiagnosticReport;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.Scope;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.validation.service.timestamptoken.util.TimestampNotGrantedValidationUtils;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.SignerDataWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.enumerations.TimestampQualification;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlTimestamp;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.WARNING_MSG_DATAFILE_NOT_COVERED_BY_TS;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.createReportPolicy;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getDateFormatterWithGMTZone;
import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.getValidationTime;

public class TimeStampTokenValidationReportBuilder {

    private static final String ASICS_SIGNATURE_FORMAT = "ASiC-S";

    private final eu.europa.esig.dss.validation.reports.Reports dssReports;
    private final List<TimestampToken> timestamps;
    private final String dataFileName;
    private final ValidationDocument validationDocument;
    private final ValidationPolicy validationPolicy;
    private final TrustedListsCertificateSource trustedListsCertificateSource;
    private final boolean isReportSignatureEnabled;

    public TimeStampTokenValidationReportBuilder(
        eu.europa.esig.dss.validation.reports.Reports dssReports,
        List<TimestampToken> timestamps,
        ValidationDocument validationDocument,
        String dataFileName,
        ValidationPolicy validationPolicy,
        TrustedListsCertificateSource trustedListsCertificateSource,
        boolean isReportSignatureEnabled) {
        this.dssReports = dssReports;
        this.timestamps = timestamps;
        this.validationDocument = validationDocument;
        this.dataFileName = dataFileName;
        this.validationPolicy = validationPolicy;
        this.trustedListsCertificateSource = trustedListsCertificateSource;
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
        validationConclusion.setTimeStampTokens(generateTimeStampTokenData());
        validationConclusion.setValidatedDocument(ReportBuilderUtils.createValidatedDocument(isReportSignatureEnabled, validationDocument.getName(), validationDocument.getBytes()));
        validationConclusion.setValidationWarnings(addValidationWarningsIfNotGrantedTimestampExists(validationConclusion.getTimeStampTokens()));
        return validationConclusion;
    }

    private List<TimeStampTokenValidationData> generateTimeStampTokenData() {
        List<TimeStampTokenValidationData> timeStampTokenValidationDataList = new ArrayList<>();

        for (String timestampId : getTimestampIds()) {
            TimeStampTokenValidationData timeStampTokenValidationData = new TimeStampTokenValidationData();

            TimestampWrapper ts = dssReports.getDiagnosticData().getTimestampById(timestampId);
            timeStampTokenValidationData.setSignedBy(ts.getSigningCertificate().getCommonName());
            timeStampTokenValidationData.setSignedTime(getDateFormatterWithGMTZone().format(ts.getProductionTime()));
            timeStampTokenValidationData.setCertificates(getCertificateList(ts));
            timeStampTokenValidationData.setTimestampScopes(getTimestampScopes(ts));
            Optional.ofNullable(dssReports.getSimpleReport().getTimestampQualification(timestampId))
                .map(TimestampQualification::name)
                .ifPresent(timeStampTokenValidationData::setTimestampLevel);
            Indication dssIndication = dssReports.getSimpleReport().getIndication(timestampId);
            List<Error> errors = parseTimestampErrors(timestampId);
            if (errors.isEmpty() && Objects.equals(dssIndication, Indication.PASSED)) {
                timeStampTokenValidationData.setIndication(TimeStampTokenValidationData.Indication.TOTAL_PASSED);
            } else {
                timeStampTokenValidationData.setError(errors);
                timeStampTokenValidationData.setIndication(TimeStampTokenValidationData.Indication.TOTAL_FAILED);
                Optional.ofNullable(dssReports.getSimpleReport().getSubIndication(timestampId))
                    .map(SubIndication::name)
                    .ifPresent(timeStampTokenValidationData::setSubIndication);
            }
            timeStampTokenValidationData.setWarning(parseTimestampWarnings(timestampId));

            if (TimeStampTokenValidationData.Indication.TOTAL_PASSED.equals(timeStampTokenValidationData.getIndication())) {
                if (!isDataFileCovered(ts)) {
                    addWarningTo(timeStampTokenValidationData, WARNING_MSG_DATAFILE_NOT_COVERED_BY_TS);
                }
            }

            timeStampTokenValidationDataList.add(timeStampTokenValidationData);
        }

        return timeStampTokenValidationDataList;
    }

    private List<ValidationWarning> addValidationWarningsIfNotGrantedTimestampExists(List<TimeStampTokenValidationData> validationDataList) {
        return validationDataList.stream()
                .map(TimeStampTokenValidationData::getWarning)
                .map(TimestampNotGrantedValidationUtils::getValidationWarningIfNotGrantedTimestampExists)
                .filter(Objects::nonNull)
                .findFirst()
                .map(warning -> new ArrayList<>(List.of(warning)))
                .orElse(null);
    }

    private List<Scope> getTimestampScopes(TimestampWrapper ts) {
        return dssReports.getDiagnosticData().getTimestampById(ts.getId()).getTimestampScopes()
            .stream()
            .map(s -> ReportBuilderUtils.parseScope(s, validationDocument.getDatafiles()))
            .collect(Collectors.toList());
    }

    private static void addWarningTo(TimeStampTokenValidationData timeStampTokenValidationData, String warningMessage) {
        List<Warning> warnings = timeStampTokenValidationData.getWarning();
        if (warnings == null) {
            timeStampTokenValidationData.setWarning(warnings = new ArrayList<>());
        }
        warnings.add(new Warning(warningMessage));
    }

    private boolean isDataFileCovered(TimestampWrapper ts) {
        return ts.getTimestampedSignedData().stream()
            .map(SignerDataWrapper::getReferencedName)
            .anyMatch(n -> StringUtils.equals(dataFileName, n));
    }

    private List<String> getTimestampIds() {
        return dssReports.getSimpleReport()
            .getTimestampIdList();
    }

    private List<Warning> parseTimestampWarnings(String timestampId) {
        return parseTimestampMessages(timestampId, XmlDetails::getWarning)
            .map(TimeStampTokenValidationReportBuilder::mapDssXmlMessage)
            .map(TimeStampTokenValidationReportBuilder::mapDssWarning)
            .collect(Collectors.toList());
    }

    private List<Error> parseTimestampErrors(String timestampId) {
        return parseTimestampMessages(timestampId, XmlDetails::getError)
            .map(TimeStampTokenValidationReportBuilder::mapDssXmlMessage)
            .map(TimeStampTokenValidationReportBuilder::mapDssError)
            .collect(Collectors.toList());
    }

    private Stream<XmlMessage> parseTimestampMessages(String timestampId, Function<XmlDetails, List<XmlMessage>> detailMessagesExtractor) {
        return Optional.ofNullable(getXmlTimestamp(timestampId))
            .stream()
            .flatMap(token -> extractTokenMessages(token, detailMessagesExtractor));
    }

    public XmlTimestamp getXmlTimestamp(String timestampId) {
        final List<XmlToken> xmlTokens = dssReports.getSimpleReport()
            .getJaxbModel()
            .getSignatureOrTimestampOrEvidenceRecord();

        if (CollectionUtils.isNotEmpty(xmlTokens)) {
            for (XmlToken xmlToken : xmlTokens) {
                if (StringUtils.equals(timestampId, xmlToken.getId()) && xmlToken instanceof XmlTimestamp) {
                    return (XmlTimestamp) xmlToken;
                }
            }
        }

        return null;
    }

    private static Stream<XmlMessage> extractTokenMessages(XmlToken token, Function<XmlDetails, List<XmlMessage>> detailMessagesExtractor) {
        return Stream.concat(
                Optional.ofNullable(token).map(XmlToken::getAdESValidationDetails).stream(),
                Optional.ofNullable(token).map(XmlToken::getQualificationDetails).stream()
            )
            .map(detailMessagesExtractor)
            .flatMap(List::stream);
    }

    private static Warning mapDssWarning(String dssWarning) {
        Warning warning = new Warning();
        warning.setContent(emptyWhenNull(dssWarning));
        return warning;
    }

    private static String mapDssXmlMessage(XmlMessage dssXmlMessage) {
        return Optional.ofNullable(dssXmlMessage)
            .map(XmlMessage::getValue)
            .orElse(null);
    }

    private static Error mapDssError(String dssError) {
        Error error = new Error();
        error.setContent(emptyWhenNull(dssError));
        return error;
    }

    private List<Certificate> getCertificateList(TimestampWrapper ts) {
        return ts.getCertificateChain().stream()
            .map(this::mapToCertificate)
            .collect(Collectors.toList());
    }

    private Certificate mapToCertificate(CertificateWrapper cw) {
        Certificate cert = new Certificate();
        cert.setCommonName(cw.getCommonName());
        cert.setContent(getCertificateContent(cw.getId()));
        cert.setType(CertificateType.CONTENT_TIMESTAMP);

        return cert;
    }

    private String getCertificateContent(String certificateId) {
        CertificateToken ct = getCertFromTsl(certificateId)
            .orElseGet(() -> getCertFromTimestamps(certificateId).orElseThrow());
        return Optional.of(ct)
            .map(CertificateToken::getCertificate)
            .map(CertUtil::encodeCertificateToBase64)
            .orElseThrow();
    }

    private Optional<CertificateToken> getCertFromTsl(String certificateId) {
        return trustedListsCertificateSource.getCertificates()
            .stream()
            .filter(t -> StringUtils.equals(t.getDSSIdAsString(), certificateId))
            .findFirst();
    }

    private Optional<CertificateToken> getCertFromTimestamps(String certificateId) {
        return timestamps.stream()
            .flatMap(t -> t.getCertificates().stream())
            .filter(t -> StringUtils.equals(t.getDSSIdAsString(), certificateId))
            .findFirst();
    }
}
