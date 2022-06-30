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

package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.PolicyConfigurationWrapper;
import eu.europa.esig.dss.model.DSSException;
import org.apache.commons.io.FilenameUtils;
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
public class TimemarkContainerValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimemarkContainerValidationService.class);

    private ReportConfigurationProperties reportConfigurationProperties;
    private BDOCConfigurationService bdocConfigurationService;
    private static final String DDOC_FORMAT = "DDOC";

    @Override
    public Reports validateDocument(ValidationDocument validationDocument) {
        if (DDOC_FORMAT.equalsIgnoreCase(FilenameUtils.getExtension(validationDocument.getName()))) {
            XMLEntityAttackValidator.validateAgainstXMLEntityAttacks(validationDocument.getBytes());
        }
        PolicyConfigurationWrapper policyConfiguration = bdocConfigurationService.loadPolicyConfiguration(validationDocument.getSignaturePolicy());
        Container container;
        try {
            container = createContainer(validationDocument, policyConfiguration.getConfiguration());
        } catch (DigiDoc4JException | DSSException e) {
            LOGGER.error("Unable to create container from validation document", e);
            throw new MalformedDocumentException(e);
        }

        try {
            ValidationResult validationResult = container.validate();
            TimemarkContainerValidationReportBuilderFactory reportBuilderFactory = new TimemarkContainerValidationReportBuilderFactory();
            return reportBuilderFactory.getReportBuilder(container, validationDocument, policyConfiguration.getPolicy(),
                    validationResult, reportConfigurationProperties.isReportSignatureEnabled()).build();
        } catch (DigiDoc4JException e) {
            throw new MalformedDocumentException(e);
        } catch (Exception e) {
            LOGGER.error("An error occurred when validating document " + validationDocument.getName(), e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    private Container createContainer(ValidationDocument validationDocument, Configuration configuration) {
        InputStream containerInputStream = new ByteArrayInputStream(validationDocument.getBytes());
        return ContainerBuilder.aContainer()
                .fromStream(containerInputStream)
                .withConfiguration(configuration)
                .build();
    }

    @Autowired
    public void setBdocConfigurationService(BDOCConfigurationService bdocConfigurationService) {
        this.bdocConfigurationService = bdocConfigurationService;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }

}
