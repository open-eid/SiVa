/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.validation.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public abstract class ValidationProxy {
    static final Logger LOGGER = LoggerFactory.getLogger(ValidationProxy.class);
    protected static final String SERVICE_BEAN_NAME_POSTFIX = "ValidationService";

    private StatisticsService statisticsService;
    ApplicationContext applicationContext;


    public SimpleReport validate(ProxyRequest proxyRequest) {
        long validationStartTime = System.nanoTime();
        SimpleReport report = validateRequest(proxyRequest);
        statisticsService.publishValidationStatistic(System.nanoTime() - validationStartTime, report.getValidationConclusion());
        return report;
    }

    SimpleReport chooseReport(Reports reports, ReportType reportType) {
        if (reportType == ReportType.DETAILED) {
            return reports.getDetailedReport();
        }
        return reports.getSimpleReport();
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

    @Autowired
    public void setStatisticsService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
