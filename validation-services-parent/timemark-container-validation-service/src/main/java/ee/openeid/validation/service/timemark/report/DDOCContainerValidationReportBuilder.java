/*
 * Copyright 2020 - 2024 Riigi Infosüsteemi Amet
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

    public static final String DDOC_TIMESTAMP_WARNING = "The algorithm SHA1 used in DDOC is no longer considered reliable for signature creation!";

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
