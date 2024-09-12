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
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.validation.util.CertUtil;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.Indication;
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

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.*;

public class TimeStampTokenValidationReportBuilder {

    private static final String ASICS_SIGNATURE_FORMAT = "ASiC-S";

    private final eu.europa.esig.dss.validation.reports.Reports dssReports;
    private final List<TimestampToken> timestamps;
    private final ValidationDocument validationDocument;
    private final ValidationPolicy validationPolicy;
    private final TrustedListsCertificateSource trustedListsCertificateSource;
    private final boolean isReportSignatureEnabled;

    public TimeStampTokenValidationReportBuilder(
        eu.europa.esig.dss.validation.reports.Reports dssReports,
        List<TimestampToken> timestamps,
        ValidationDocument validationDocument,
        ValidationPolicy validationPolicy,
        TrustedListsCertificateSource trustedListsCertificateSource,
        boolean isReportSignatureEnabled) {
        this.dssReports = dssReports;
        this.timestamps = timestamps;
        this.validationDocument = validationDocument;
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
            Indication dssIndication = dssReports.getSimpleReport().getIndication(timestampId);
            List<Error> errors = parseTimestampErrors(timestampId);
            if (errors.isEmpty() && Objects.equals(dssIndication, Indication.PASSED)) {
                timeStampTokenValidationData.setIndication(TimeStampTokenValidationData.Indication.TOTAL_PASSED);
            } else {
                timeStampTokenValidationData.setError(errors);
                timeStampTokenValidationData.setIndication(TimeStampTokenValidationData.Indication.TOTAL_FAILED);
            }

            timeStampTokenValidationDataList.add(timeStampTokenValidationData);
        }

        return timeStampTokenValidationDataList;
    }

    private List<String> getTimestampIds() {
        return dssReports.getSimpleReport()
            .getTimestampIdList();
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
