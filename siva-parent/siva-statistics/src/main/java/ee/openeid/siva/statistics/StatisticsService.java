/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.statistics.logging.ContainerStatistics;
import ee.openeid.siva.statistics.logging.SignatureStatistics;
import ee.openeid.siva.statistics.model.SimpleSignatureReport;
import ee.openeid.siva.statistics.model.SimpleValidationReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ee.openeid.siva.statistics.ContainerTypeResolver.resolveContainerType;

@Slf4j
@Service
public class StatisticsService {

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
        } catch (JacksonException e) {
            log.error("Error generating json: {}", e.getMessage(), e);
        }
    }

    private void logContainerStats(SimpleValidationReport simpleValidationReport) {
        ContainerStatistics.log(toJson(simpleValidationReport));
    }

    private String toJson(SimpleValidationReport simpleValidationReport)  {
        Map<String, SimpleValidationReport> stats = new HashMap<>();
        stats.put("stats", simpleValidationReport);
        return new JsonMapper().writeValueAsString(stats);
    }


    private void logSignatureStats(List<SimpleSignatureReport> simpleSignatureReports) {
        for (SimpleSignatureReport simpleSignatureReport : simpleSignatureReports) {
            SignatureStatistics.log(toJson(simpleSignatureReport));
        }
    }

    private String toJson(SimpleSignatureReport simpleSignatureReport) {
        Map<String, SimpleSignatureReport> stats = new HashMap<>();
        stats.put("signatureStats", simpleSignatureReport);
        return new JsonMapper().writeValueAsString(stats);
    }

    @Autowired
    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

}
