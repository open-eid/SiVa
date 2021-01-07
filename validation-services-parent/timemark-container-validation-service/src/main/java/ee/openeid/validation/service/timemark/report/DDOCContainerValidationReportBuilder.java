package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.document.report.ValidationWarning;
import ee.openeid.siva.validation.document.report.Warning;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Container;
import org.digidoc4j.DigestDataFile;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureProfile;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.impl.ddoc.DDocContainer;
import org.digidoc4j.impl.ddoc.DDocFacade;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DDOCContainerValidationReportBuilder extends TimemarkContainerValidationReportBuilder {

    public static final String DDOC_TIMESTAMP_WARNING = "Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa";

    public DDOCContainerValidationReportBuilder(Container container, ValidationDocument validationDocument, ValidationPolicy validationPolicy, ValidationResult validationResult, boolean isReportSignatureEnabled) {
        super(container, validationDocument, validationPolicy, validationResult, isReportSignatureEnabled);
    }

    @Override
    void processSignatureIndications(ValidationConclusion validationConclusion, String policyName) {
        //Do nothing
    }

    @Override
    SignatureValidationData.Indication getIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults) {
        ValidationResult signatureValidationResult = signatureValidationResults.get(signature.getUniqueId());
        if (signatureValidationResult.isValid() && validationResult.getErrors().isEmpty()) {
            return SignatureValidationData.Indication.TOTAL_PASSED;
        } else {
            return SignatureValidationData.Indication.TOTAL_FAILED;
        }
    }

    @Override
    String getSubIndication(Signature signature, Map<String, ValidationResult> signatureValidationResults) {
        return "";
    }

    @Override
    protected String getSignatureLevel(Signature signature) {
        return null;
    }

    @Override
    List<ValidationWarning> getExtraValidationWarnings() {
        ValidationWarning timestampValidationWarning = new ValidationWarning();
        timestampValidationWarning.setContent(DDOC_TIMESTAMP_WARNING);
        return Collections.singletonList(timestampValidationWarning);
    }

    @Override
    List<SignatureScope> getSignatureScopes(Signature signature, List<String> dataFilenames) {
        return dataFilenames
                .stream()
                .map(this::mapDataFile)
                .collect(Collectors.toList());
    }

    @Override
    String getSignatureForm() {
        return getDigidocXmlSignatureForm();
    }

    @Override
    String getSignatureFormat(SignatureProfile profile) {
        DDocFacade dDocFacade = ((DDocContainer) container).getDDoc4JFacade();
        return dDocFacade.getFormat().replaceAll("-", "_") + "_" + dDocFacade.getVersion();
    }

    private SignatureScope mapDataFile(String filename) {
        SignatureScope signatureScope = new SignatureScope();
        signatureScope.setName(filename);
        signatureScope.setContent(FULL_DOCUMENT);
        signatureScope.setScope(FULL_SIGNATURE_SCOPE);
        return signatureScope;
    }

    private String getDigidocXmlSignatureForm() {
        return DDOC_SIGNATURE_FORM_PREFIX + ((DDocContainer) container).getDDoc4JFacade().getVersion() + getSignatureFormSuffix();
    }

    private String getSignatureFormSuffix() {
        return isHashcodeForm() ? DDOC_HASHCODE_SIGNATURE_FORM_SUFFIX : StringUtils.EMPTY;
    }

    private boolean isHashcodeForm() {
        DDocFacade ddocFacade = ((DDocContainer) container).getDDoc4JFacade();
        return ddocFacade.getDataFiles().stream().anyMatch(dataFile -> {
                    if (dataFile instanceof DigestDataFile) {
                        return HASHCODE_CONTENT_TYPE.equals(((DigestDataFile) dataFile).getContentType());
                    }
                    return false;
                }
        );
    }
}
