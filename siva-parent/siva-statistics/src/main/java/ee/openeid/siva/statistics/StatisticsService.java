/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ee.openeid.siva.statistics.googleanalytics.GoogleAnalyticsMeasurementProtocolClient;
import ee.openeid.siva.statistics.model.SimpleSignatureReport;
import ee.openeid.siva.statistics.model.SimpleValidationReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ee.openeid.siva.statistics.SignatureFormToContainerTypeTransormer.transformToContainerTypeOrEmpty;

@Service
public class StatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

    private HttpServletRequest httpRequest;
    private GoogleAnalyticsMeasurementProtocolClient googleAnalyticsMeasurementProtocolClient;

    public void publishValidationStatistic(long validationDurationInNanos, ValidationConclusion validationConclusion) {
        SimpleValidationReport simpleValidationReport = createValidationResult(validationDurationInNanos, validationConclusion);
        try {
            LOGGER.info(toJson(simpleValidationReport));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error generating json: {}", e.getMessage(), e);
        }
        googleAnalyticsMeasurementProtocolClient.sendStatisticalData(simpleValidationReport);
    }

    private SimpleValidationReport createValidationResult(long validationDurationInNanos, ValidationConclusion report) {
        SimpleValidationReport simpleValidationReport = new SimpleValidationReport();
        simpleValidationReport.setDuration(TimeUnit.NANOSECONDS.toMillis(validationDurationInNanos));
        simpleValidationReport.setSignatureCount(report.getSignaturesCount());
        simpleValidationReport.setValidSignatureCount(report.getValidSignaturesCount());
        simpleValidationReport.setSimpleSignatureReports(createSimpleSignatureReports(report));
        simpleValidationReport.setContainerType(transformToContainerTypeOrEmpty(report.getSignatureForm()));
        simpleValidationReport.setUserIdentifier(getUserIdentifier());
        return simpleValidationReport;
    }

    private List<SimpleSignatureReport> createSimpleSignatureReports(ValidationConclusion report) {
        if (report.getSignatures() != null)
            return report.getSignatures().stream().map(this::createSimpleSignatureReport).collect(Collectors.toList());
        return Collections.emptyList();
    }

    private SimpleSignatureReport createSimpleSignatureReport(SignatureValidationData signatureValidationData) {
        SimpleSignatureReport simpleSignatureReport = new SimpleSignatureReport();
        simpleSignatureReport.setIndication(signatureValidationData.getIndication());
        simpleSignatureReport.setSubIndication(signatureValidationData.getSubIndication());
        simpleSignatureReport.setSignatureFormat(signatureValidationData.getSignatureFormat());

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

    private String getUserIdentifier() {
        String userIdentifier = httpRequest.getHeader("x-authenticated-user");
        return StringUtils.isEmpty(userIdentifier) ? "N/A" : userIdentifier;
    }

    @Autowired
    public void setGoogleAnalyticsMeasurementClient(GoogleAnalyticsMeasurementProtocolClient googleAnalyticsMeasurementProtocolClient) {
        this.googleAnalyticsMeasurementProtocolClient = googleAnalyticsMeasurementProtocolClient;
    }

    @Autowired
    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

}
