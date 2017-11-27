/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.bdoc;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.bdoc.report.BDOCValidationReportBuilder;
import ee.openeid.validation.service.bdoc.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.bdoc.signature.policy.PolicyConfigurationWrapper;
import eu.europa.esig.dss.DSSException;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Configuration;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class BDOCValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BDOCValidationService.class);

    private static final String CONTAINER_TYPE_DDOC = "DDOC";

    private ReportConfigurationProperties reportConfigurationProperties;
    private BDOCConfigurationService configurationService;

    @Override
    public Reports validateDocument(ValidationDocument validationDocument) {
        PolicyConfigurationWrapper policyConfiguration = configurationService.loadPolicyConfiguration(validationDocument.getSignaturePolicy());
        Container container;
        try {
            container = createContainer(validationDocument, policyConfiguration.getConfiguration());
        } catch (DigiDoc4JException | DSSException e) {
            LOGGER.error("Unable to create container from validation document", e);
            throw new MalformedDocumentException(e);
        }
        verifyContainerTypeNotDDOC(container.getType());
        try {
            ValidationResult validationResult = container.validate();
            return new BDOCValidationReportBuilder(container, validationDocument, policyConfiguration.getPolicy(), validationResult.getContainerErrors(), reportConfigurationProperties.isReportSignatureEnabled()).build();
        } catch (Exception e) {
            if (isXRoadContainer(container)) {
                LOGGER.error("XROAD container passed to BDOC validator", e);
                throw new MalformedDocumentException(e);
            }
            LOGGER.error("An error occurred when validating document " + validationDocument.getName(), e);
            throw e;
        }
    }

    private boolean isXRoadContainer(Container container) {
        return container
                .getDataFiles()
                .stream()
                .filter(dataFile -> StringUtils.equals(dataFile.getName(), "message.xml"))
                .count() == 1;
    }

    private Container createContainer(ValidationDocument validationDocument, Configuration configuration) {
        InputStream containerInputStream = new ByteArrayInputStream(validationDocument.getBytes());
        return ContainerBuilder.aContainer()
                .fromStream(containerInputStream)
                .withConfiguration(configuration)
                .build();
    }

    private void verifyContainerTypeNotDDOC(String containerType) {
        if (StringUtils.equalsIgnoreCase(containerType, CONTAINER_TYPE_DDOC)) {
            LOGGER.error("DDOC container passed to BDOC validator");
            throw new MalformedDocumentException();
        }
    }

    @Autowired
    public void setConfigurationService(BDOCConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }
}
