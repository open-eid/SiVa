package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.digidoc4j.*;
import org.digidoc4j.impl.asic.asice.AsicESignature;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.digidoc4j.X509Cert.SubjectName.CN;

public class AsicContainerValidationReportBuilder extends TimemarkContainerValidationReportBuilder {
    public AsicContainerValidationReportBuilder(Container container, ValidationDocument validationDocument, ValidationPolicy validationPolicy, ValidationResult validationResult, boolean isReportSignatureEnabled) {
        super(container, validationDocument, validationPolicy, validationResult, isReportSignatureEnabled);
    }

    @Override
    List<Warning> getExtraWarnings(Signature signature) {
        List<String> dataFilenames = container.getDataFiles().stream().map(DataFile::getName).collect(Collectors.toList());
        Warning warning = createValidationWarning(signature, getUnsignedFiles((AsicESignature) signature, dataFilenames));
        if (warning == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(warning);
    }

    @Override
    List<ValidationWarning> getExtraValidationWarnings() {
        return Collections.emptyList();
    }

    @Override
    List<SignatureScope> getSignatureScopes(Signature signature, List<String> dataFilenames) {
        AsicESignature bDocSignature = (AsicESignature) signature;
        return bDocSignature.getOrigin().getReferences()
                .stream()
                .map(r -> decodeUriIfPossible(r.getURI()))
                .filter(dataFilenames::contains) //filters out Signed Properties
                .map(AsicContainerValidationReportBuilder::createFullSignatureScopeForDataFile)
                .collect(Collectors.toList());
    }

    @Override
    String getSignatureForm() {
        return BDOC_SIGNATURE_FORM;
    }

    @Override
    String getSignatureFormat(SignatureProfile profile) {
        return XADES_FORMAT_PREFIX + profile.toString();
    }

    private static SignatureScope createFullSignatureScopeForDataFile(String filename) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(filename);
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        signatureScope.setContent(FULL_DOCUMENT);
        return signatureScope;
    }

    private String decodeUriIfPossible(String uri) {
        try {
            return URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("datafile " + uri + " has unsupported encoding", e);
            return uri;
        }
    }

    private Warning createValidationWarning(Signature signature, List<String> unsignedFiles) {
        if (unsignedFiles.isEmpty()) {
            return null;
        }
        String signedBy = removeQuotes(signature.getSigningCertificate().getSubjectName(CN));
        String commaSeparated = String.join(", ", unsignedFiles);
        String content = String.format("Signature %s has unsigned files: %s", signedBy, commaSeparated);
        return createWarning(content);
    }

    private List<String> getUnsignedFiles(AsicESignature bDocSignature, List<String> dataFilenames) {
        List<String> uris = bDocSignature.getOrigin().getReferences()
                .stream()
                .map(reference -> decodeUriIfPossible(reference.getURI()))
                .filter(dataFilenames::contains)
                .collect(Collectors.toList());
        return dataFilenames.stream()
                .filter(df -> !uris.contains(df))
                .collect(Collectors.toList());
    }

}
