/*
 * Copyright 2018 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.exception.MalformedSignatureFileException;
import ee.openeid.siva.validation.security.SecureSAXParsers;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DigestDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.xml.parsers.SAXParser;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HashcodeGenericValidationService extends GenericValidationService {

    public Reports validate(List<ValidationDocument> validationDocuments) {
        List<Reports> reports = validationDocuments.stream().map(validationDocument -> validateDocument(validationDocument)).collect(Collectors.toList());
        return mergeReportsToOne(reports);
    }

    @Override
    protected SignedDocumentValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
        List<Datafile> datafiles = getDataFileInfoIfNeeded(validationDocument);
        if(!CollectionUtils.isEmpty(datafiles)){
            validationDocument.setDatafiles(datafiles);
        }
        SignedDocumentValidator validator = super.createValidatorFromDocument(validationDocument);
        List<DSSDocument> detachedContents = createDetachedContents(validationDocument.getDatafiles());
        validator.setDetachedContents(detachedContents);
        return validator;
    }

    private List<Datafile> getDataFileInfoIfNeeded(ValidationDocument validationDocument) {
        if (!CollectionUtils.isEmpty(validationDocument.getDatafiles())) {
            return null;
        } else {
            try {
                SAXParser saxParser = SecureSAXParsers.createParser();
                SignatureXmlHandler handler = new SignatureXmlHandler();
                saxParser.parse(new ByteArrayInputStream(validationDocument.getBytes()), handler);
                return handler.getDatafiles();
            } catch (Exception e) {
                throw constructMalformedDocumentException(new RuntimeException(e));
            }
        }
    }

    @Override
    protected RuntimeException constructMalformedDocumentException(Exception cause) {
        return new MalformedSignatureFileException(cause, "Signature file malformed");
    }

    @Override
    protected DSSDocument createDssDocument(final ValidationDocument validationDocument) {
        if (validationDocument == null) {
            return null;
        }
        return new InMemoryDocument(validationDocument.getBytes());
    }

    private List<DSSDocument> createDetachedContents(final List<Datafile> datafiles) {
        if(CollectionUtils.isEmpty(datafiles)){
            throw constructMalformedDocumentException(new RuntimeException("Data files are missing"));
        }
        return datafiles.stream()
                .map(this::createDigestDocument)
                .collect(Collectors.toList());
    }

    private DigestDocument createDigestDocument(final Datafile datafile) {
        DigestDocument digestDocument = new DigestDocument();
        digestDocument.setName(datafile.getFilename());

        DigestAlgorithm digestAlgorithm = DigestAlgorithm.valueOf(datafile.getHashAlgo().toUpperCase());
        digestDocument.addDigest(digestAlgorithm, datafile.getHash());

        return digestDocument;
    }

    private Reports mergeReportsToOne(List<Reports> reportsList) {
        int signaturesCount = 0;
        int validSignaturesCount = 0;
        Reports response = null;
        for (Reports reports : reportsList) {
            ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
            if (signaturesCount == 0) {
                response = reports;
                validSignaturesCount = validationConclusion.getValidSignaturesCount();
            } else {
                response.getSimpleReport().getValidationConclusion().getSignatures().addAll(validationConclusion.getSignatures());
                validSignaturesCount = validSignaturesCount + validationConclusion.getValidSignaturesCount();
            }
            signaturesCount = signaturesCount + validationConclusion.getSignaturesCount();
        }
        if (response != null) {
            ValidationConclusion validationConclusion = response.getSimpleReport().getValidationConclusion();
            validationConclusion.setSignaturesCount(signaturesCount);
            validationConclusion.setValidSignaturesCount(validSignaturesCount);
        }
        return response;
    }

}
