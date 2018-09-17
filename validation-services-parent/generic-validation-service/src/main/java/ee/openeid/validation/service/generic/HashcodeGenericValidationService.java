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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedSignatureFileException;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.DigestDocument;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HashcodeGenericValidationService extends GenericValidationService {

    @Override
    protected SignedDocumentValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
        SignedDocumentValidator validator = super.createValidatorFromDocument(validationDocument);
        List<DSSDocument> detachedContents = createDetachedContents(validationDocument.getDatafiles());
        validator.setDetachedContents(detachedContents);
        return validator;
    }

    @Override
    protected void validateBestSignatureTime(final Reports reports) {
        //Do nothing
    }

    @Override
    protected RuntimeException constructMalformedDocumentException(RuntimeException cause) {
        return new MalformedSignatureFileException(cause, "Signature file malformed");
    }

    private List<DSSDocument> createDetachedContents(final List<Datafile> datafiles) {
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

}
