package ee.openeid.siva.statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ee.openeid.siva.statistics.googleanalytics.GoogleAnalyticsMeasurementProtocolClient;
import ee.openeid.siva.statistics.model.SimpleSignatureReport;
import ee.openeid.siva.statistics.model.SimpleValidationReport;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class StatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

    private GoogleAnalyticsMeasurementProtocolClient googleAnalyticsMeasurementProtocolClient;

    public void publishValidationStatistic(long validationDurationInNanos, QualifiedReport report) {
        SimpleValidationReport simpleValidationReport = createValidationResult(validationDurationInNanos, report);
        try {
            LOGGER.info(toJson(simpleValidationReport));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error generating json: {}", e.getMessage(), e);
        }
        googleAnalyticsMeasurementProtocolClient.sendStatisticalData(simpleValidationReport);
    }

    private SimpleValidationReport createValidationResult(long validationDurationInNanos, QualifiedReport report) {
        SimpleValidationReport simpleValidationReport = new SimpleValidationReport();
        simpleValidationReport.setDuration(TimeUnit.NANOSECONDS.toMillis(validationDurationInNanos));
        simpleValidationReport.setSignatureCount(report.getSignaturesCount());
        simpleValidationReport.setValidSignatureCount(report.getValidSignaturesCount());
        simpleValidationReport.setSimpleSignatureReports(createSimpleSignatureReports(report));
        simpleValidationReport.setContainerType(report.getSignatureForm());

        return  simpleValidationReport;
    }

    private List<SimpleSignatureReport> createSimpleSignatureReports(QualifiedReport report) {
        return report.getSignatures().stream().map(sig -> createSimpleSignatureReport(sig)).collect(Collectors.toList());
    }

    private SimpleSignatureReport createSimpleSignatureReport(SignatureValidationData signatureValidationData) {
        SimpleSignatureReport simpleSignatureReport = new SimpleSignatureReport();
        simpleSignatureReport.setIndication(signatureValidationData.getIndication());
        simpleSignatureReport.setSubIndication(signatureValidationData.getSubIndication());

        String countryCode = StringUtils.isEmpty(signatureValidationData.getCountryCode()) ? "XX" : signatureValidationData.getCountryCode();
        simpleSignatureReport.setCountryCode(countryCode);

        return simpleSignatureReport;
    }

    private String toJson(SimpleValidationReport simpleValidationReport) throws JsonProcessingException {
        Map<String, SimpleValidationReport> stats = new HashMap<>();
        stats.put("stats", simpleValidationReport);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(stats);
    }

    @Autowired
    public void setGoogleAnalyticsMeasurementClient(GoogleAnalyticsMeasurementProtocolClient googleAnalyticsMeasurementProtocolClient) {
        this.googleAnalyticsMeasurementProtocolClient = googleAnalyticsMeasurementProtocolClient;
    }

}
