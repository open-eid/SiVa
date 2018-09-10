package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
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

    private List<DSSDocument> createDetachedContents(final List<Datafile> datafiles) {
        return datafiles.stream()
                .map(this::createDigestDocument)
                .collect(Collectors.toList());
    }

    private DigestDocument createDigestDocument(final Datafile datafile) {
        DigestDocument digestDocument = new DigestDocument();
        digestDocument.setName(datafile.getFilename());

        DigestAlgorithm digestAlgorithm = DigestAlgorithm.valueOf(datafile.getHashAlgo());
        digestDocument.addDigest(digestAlgorithm, datafile.getHash());

        return digestDocument;
    }

}
