/*
 * Copyright 2019 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.service.ValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.ArrayList;
import java.util.List;

public abstract class ValidationProxy {
    static final Logger LOGGER = LoggerFactory.getLogger(ValidationProxy.class);

    protected static final String SERVICE_BEAN_NAME_POSTFIX = "ValidationService";
    private static final Profiles TEST_PROFILE = Profiles.of("test");

    private final StatisticsService statisticsService;
    private final ApplicationContext applicationContext;
    private final Environment environment;

    @Autowired
    protected ValidationProxy(StatisticsService statisticsService, ApplicationContext applicationContext, Environment environment) {
        this.statisticsService = statisticsService;
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    public SimpleReport validate(ProxyRequest proxyRequest) {
        long validationStartTime = System.nanoTime();
        SimpleReport report = validateRequest(proxyRequest);
        long validationDuration = System.nanoTime() - validationStartTime;

        publishStatistics(report, validationDuration);
        addWarningForTestEnvironment(report);
        return report;
    }

    private void publishStatistics(SimpleReport report, long validationDuration) {
        statisticsService.publishValidationStatistic(validationDuration, report.getValidationConclusion());
    }

    private void addWarningForTestEnvironment(SimpleReport simpleReport) {
        if (!environment.acceptsProfiles(TEST_PROFILE)) {
            return;
        }

        ValidationWarning validationWarning = new ValidationWarning();
        validationWarning.setContent("This is validation service demo. Use it for testing purposes only");

        List<ValidationWarning> warnings = new ArrayList<>();
        warnings.add(validationWarning);

        if (CollectionUtils.isNotEmpty(simpleReport.getValidationConclusion().getValidationWarnings())) {
            warnings.addAll(simpleReport.getValidationConclusion().getValidationWarnings());
        }

        simpleReport.getValidationConclusion().setValidationWarnings(warnings);
    }

    SimpleReport chooseReport(Reports reports, ReportType reportType) {
        if (reportType == null) {
            return reports.getSimpleReport();
        }

        switch (reportType) {
            case SIMPLE:
                return reports.getSimpleReport();
            case DETAILED:
                return reports.getDetailedReport();
            case DIAGNOSTIC:
                return reports.getDiagnosticReport();
            default:
                throw new IllegalArgumentException("Failed to determine report type - report of type '" + reportType.name() + "' is unhandled");
        }
    }

    ValidationService getServiceForType(ProxyRequest proxyRequest) {
        String validatorName = constructValidatorName(proxyRequest);
        LOGGER.info("Validation service: {}", validatorName);
        try {
            return (ValidationService) applicationContext.getBean(validatorName);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.error("{} not found", validatorName, e);
            throw new ValidatonServiceNotFoundException(validatorName + " not found");
        }
    }

    abstract String constructValidatorName(ProxyRequest proxyRequest);
    abstract SimpleReport validateRequest(ProxyRequest proxyRequest);

}
