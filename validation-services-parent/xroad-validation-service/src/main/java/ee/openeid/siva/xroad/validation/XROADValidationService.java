/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.siva.xroad.configuration.XROADValidationServiceProperties;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.SystemProperties;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.conf.globalconf.GlobalConf;
import eu.europa.esig.dss.asic.common.ZipContainerHandler;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.function.Supplier;

@Service
public class XROADValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XROADValidationService.class);
    private XROADValidationServiceProperties properties;
    private SignaturePolicyService<ValidationPolicy> signaturePolicyService;
    private ReportConfigurationProperties reportConfigurationProperties;
    private Supplier<ZipContainerHandler> zipContainerHandlerFactory;

    @Override
    public Reports validateDocument(ValidationDocument validationDocument) {
        final ValidationPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());
        final AsicContainer container = validateAndExtractAsicContainer(validationDocument);
        final AsicContainerVerifier verifier = new AsicContainerVerifier(container);
        try {
            verifier.verify();
            return new XROADValidationReportBuilder(verifier, validationDocument, policy, reportConfigurationProperties.isReportSignatureEnabled()).build();
        } catch (CodedException codedException) {
            return new XROADValidationReportBuilder(verifier, validationDocument, policy, reportConfigurationProperties.isReportSignatureEnabled(), codedException).build();
        } catch (Exception e) {
            LOGGER.warn("There was an error validating the document", e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    private AsicContainer validateAndExtractAsicContainer(ValidationDocument validationDocument) {
        DSSDocument containerDocument = new InMemoryDocument(Base64.decodeBase64(validationDocument.getDataBase64Encoded()));
        try {
            // Use DSS' ZIP container extraction utilities to prevent ZIP bombing
            zipContainerHandlerFactory.get().extractContainerContent(containerDocument);

            try (InputStream inputStream = containerDocument.openStream()) {
                return AsicContainer.read(inputStream);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to create AsicContainer from validation document", e);
            throw new MalformedDocumentException(e);
        }
    }


    @PostConstruct
    void loadXroadConfigurationDirectory() {
        String configurationDirectoryPath = properties.getConfigurationDirectoryPath();
        System.setProperty(SystemProperties.CONFIGURATION_PATH, configurationDirectoryPath);
        LOGGER.info("Loading configuration from path: {}", configurationDirectoryPath);
        try {
            GlobalConf.reload();
        } catch (CodedException e) {
            LOGGER.error("Unable to load configuration: ", e);
        }
    }

    @Autowired
    public void setProperties(XROADValidationServiceProperties properties) {
        this.properties = properties;
    }

    @Autowired
    @Qualifier(value = "XROADPolicyService")
    public void setSignaturePolicyService(SignaturePolicyService<ValidationPolicy> signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }

    @Autowired
    public void setZipContainerHandlerFactory(Supplier<ZipContainerHandler> zipContainerHandlerFactory) {
        this.zipContainerHandlerFactory = zipContainerHandlerFactory;
    }

}
