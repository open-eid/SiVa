/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.pdf.configuration.PDFPolicySettings;
import ee.openeid.validation.service.pdf.document.transformer.ValidationDocumentToDSSDocumentTransformerUtils;
import ee.openeid.validation.service.pdf.validator.EstonianPDFDocumentValidator;
import ee.openeid.validation.service.pdf.validator.report.PDFQualifiedReportBuilder;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.report.Reports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.soap.SOAPException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class PDFValidationService implements ValidationService {

    private static final Logger logger = LoggerFactory.getLogger(PDFValidationService.class);

    private CertificateVerifier certificateVerifier;

    private PDFPolicySettings policySettings;

    @Override
    public QualifiedReport validateDocument(ValidationDocument validationDocument) throws DSSException {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("WsValidateDocument: begin");
            }

            if (validationDocument == null) {
                throw new SOAPException("No request document found");
            }

            final DSSDocument dssDocument = ValidationDocumentToDSSDocumentTransformerUtils.createDssDocument(validationDocument);

            if (!new EstonianPDFDocumentValidator().isSupported(dssDocument)) {
                throw new MalformedDocumentException();
            }

            EstonianPDFDocumentValidator validator = new EstonianPDFDocumentValidator(dssDocument);
            validator.setCertificateVerifier(certificateVerifier);

            final Reports reports = validator.validateDocument(policySettings.getPolicyDataStream());

            ZonedDateTime validationTimeInGMT = ZonedDateTime.now(ZoneId.of("GMT"));

            if (logger.isInfoEnabled()) {
                logger.info(
                        "Validation completed. Total signature count: {} and valid signature count: {}",
                        reports.getSimpleReport().getElement("//SignaturesCount").getText(),
                        reports.getSimpleReport().getElement("//ValidSignaturesCount").getText()
                );

                logger.info("WsValidateDocument: end");
            }
            PDFQualifiedReportBuilder reportBuilder = new PDFQualifiedReportBuilder(reports, validationTimeInGMT, validationDocument.getName());
            return reportBuilder.build();

        } catch (MalformedDocumentException e) {
            endExceptionally(e);
            throw e;
        } catch (Exception e) {
            endExceptionally(e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    private void endExceptionally(Exception e) {
        logger.error(e.getMessage(), e);
        logger.info("WsValidateDocument: end with exception");
    }

    @Autowired
    public void setPolicySettings(PDFPolicySettings policySettings) {
        this.policySettings = policySettings;
    }

    @Autowired
    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }


}