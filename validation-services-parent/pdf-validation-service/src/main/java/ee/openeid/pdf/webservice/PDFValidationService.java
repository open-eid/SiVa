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
package ee.openeid.pdf.webservice;

import ee.openeid.pdf.webservice.document.transformer.PDFValidationResult;
import ee.openeid.pdf.webservice.document.transformer.ValidationDocumentToDSSDocumentTransformer;
import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.report.Reports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.soap.SOAPException;
import java.io.InputStream;

/**
 * Implementation of the Interface for the Contract of the Validation Web Service.
 */
@Service
public class PDFValidationService implements ValidationService {
    private static final Logger logger = LoggerFactory.getLogger(PDFValidationService.class);

    private CertificateVerifier certificateVerifier;

    @Override
    public QualifiedValidationResult validateDocument(ValidationDocument validationDocument) throws DSSException {

        String exceptionMessage;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("WsValidateDocument: begin");
            }

            if (validationDocument == null) {
                throw new SOAPException("No request document found");
            }

            final DSSDocument dssDocument = ValidationDocumentToDSSDocumentTransformer.createDssDocument(validationDocument);
            final SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(dssDocument);
            validator.setCertificateVerifier(certificateVerifier);

            final InputStream inputStream = null;
            final Reports reports = validator.validateDocument(inputStream);

            if (logger.isInfoEnabled()) {
                logger.info(
                        "Validation completed. Total signature count: {} and valid signature count: {}",
                        reports.getSimpleReport().getElement("//SignaturesCount").getText(),
                        reports.getSimpleReport().getElement("//ValidSignaturesCount").getText()
                );

                logger.info("WsValidateDocument: end");
            }

            return new PDFValidationResult(reports);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            exceptionMessage = e.getMessage();
        }
        logger.info("WsValidateDocument: end with exception");
        throw new DSSException(exceptionMessage);
    }

    @Autowired
    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }


}