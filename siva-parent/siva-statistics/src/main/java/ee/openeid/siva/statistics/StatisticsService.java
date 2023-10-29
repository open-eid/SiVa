/*
 * Copyright 2017 - 2023 Riigi Infosüsteemi Amet
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ee.openeid.siva.statistics.ContainerTypeResolver.resolveContainerType;
import static org.slf4j.MarkerFactory.getMarker;

@Service
public class StatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

    private static final String CONTAINER_LOG_MARKER = "STATISTICS_CONTAINER_LOG";
    private static final String SIGNATURE_LOG_MARKER = "STATISTICS_SIGNATURE_LOG";

    private static final String NA = "N/A";

    private HttpServletRequest httpRequest;

    public void publishValidationStatistic(long validationDurationInNanos, ValidationConclusion validationConclusion) {
        SimpleValidationReport simpleValidationReport = createValidationResult(
                validationDurationInNanos,
                validationConclusion,
                () -> SignatureTypeResolver.resolveSignatureType(validationConclusion));
        logStats(simpleValidationReport);
    }

    private SimpleValidationReport createValidationResult(long validationDurationInNanos,
                                                          ValidationConclusion report,
                                                          Supplier<String> signatureTypeSupplier) {
        SimpleValidationReport simpleValidationReport = new SimpleValidationReport();
        simpleValidationReport.setDuration(TimeUnit.NANOSECONDS.toMillis(validationDurationInNanos));
        simpleValidationReport.setContainerType(resolveContainerType(report));
        simpleValidationReport.setSignatureType(signatureTypeSupplier.get());
        simpleValidationReport.setUserIdentifier(getUserIdentifier());

        if (SignatureTypeResolver.isTstTypeContainer(report)) {
            simpleValidationReport.setSignatureCount(0);
            simpleValidationReport.setValidSignatureCount(0);
            simpleValidationReport.setSimpleSignatureReports(Collections.emptyList());
        } else {
            simpleValidationReport.setSignatureCount(report.getSignaturesCount());
            simpleValidationReport.setValidSignatureCount(report.getValidSignaturesCount());
            simpleValidationReport.setSimpleSignatureReports(createSimpleSignatureReports(report));
        }

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

    private String getUserIdentifier() {
        String userIdentifier = httpRequest.getHeader("x-authenticated-user");
        return StringUtils.isEmpty(userIdentifier) ? NA : userIdentifier;
    }

    private void logStats(SimpleValidationReport simpleValidationReport) {
        try {
            logContainerStats(simpleValidationReport);
            logSignatureStats(simpleValidationReport.getSimpleSignatureReports());
        } catch (JsonProcessingException e) {
            LOGGER.error("Error generating json: {}", e.getMessage(), e);
        }
    }

    private void logContainerStats(SimpleValidationReport simpleValidationReport) throws JsonProcessingException {
        LOGGER.info(getMarker(CONTAINER_LOG_MARKER), toJson(simpleValidationReport));
    }

    private String toJson(SimpleValidationReport simpleValidationReport) throws JsonProcessingException {
        Map<String, SimpleValidationReport> stats = new HashMap<>();
        stats.put("stats", simpleValidationReport);
        return new ObjectMapper().writer().writeValueAsString(stats);
    }


    private void logSignatureStats(List<SimpleSignatureReport> simpleSignatureReports) throws JsonProcessingException {
        for (SimpleSignatureReport simpleSignatureReport : simpleSignatureReports) {
            LOGGER.info(getMarker(SIGNATURE_LOG_MARKER), toJson(simpleSignatureReport));
        }
    }

    private String toJson(SimpleSignatureReport simpleSignatureReport) throws JsonProcessingException {
        Map<String, SimpleSignatureReport> stats = new HashMap<>();
        stats.put("signatureStats", simpleSignatureReport);
        return new ObjectMapper().writer().writeValueAsString(stats);
    }

    @Autowired
    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

}
