/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.statistics;

import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class ContainerTypeResolver {

    private static final String PADES_SIGNATURE_FORMAT_PREFIX = "PAdES_";
    private static final String ASIC_E_SIGNATURE_FORM = "ASiC-E";
    private static final String ASIC_S_SIGNATURE_FORM = "ASiC-S";
    private static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";

    private static final String PADES_CONTAINER_TYPE = "PAdES";
    private static final String ASIC_E_CONTAINER_TYPE = "ASiC-E";
    private static final String ASIC_S_CONTAINER_TYPE = "ASiC-S";
    private static final String DDOC_CONTAINER_TYPE = "DIGIDOC_XML";

    private static final String NA = "N/A";

    static String resolveContainerType(ValidationConclusion validationConclusion) {
        return isPades(validationConclusion.getSignatures())
                ? PADES_CONTAINER_TYPE
                : resolveContainerTypeFromSignatureForm(validationConclusion.getSignatureForm());
    }

    private static boolean isPades(List<SignatureValidationData> signatures) {
        return CollectionUtils.isNotEmpty(signatures)
                && StringUtils.startsWith(signatures.get(0).getSignatureFormat(), PADES_SIGNATURE_FORMAT_PREFIX);
    }

    private static String resolveContainerTypeFromSignatureForm(String signatureForm) {
        if (isAsicE(signatureForm)) {
            return ASIC_E_CONTAINER_TYPE;
        }
        if (isAsicS(signatureForm)) {
            return ASIC_S_CONTAINER_TYPE;
        }
        if (isDdoc(signatureForm)) {
            return DDOC_CONTAINER_TYPE;
        }
        return NA;
    }

    private static boolean isAsicE(String signatureForm) {
        return ASIC_E_SIGNATURE_FORM.equals(signatureForm);
    }

    private static boolean isAsicS(String signatureForm) {
        return ASIC_S_SIGNATURE_FORM.equals(signatureForm);
    }

    private static boolean isDdoc(String signatureForm) {
        return StringUtils.startsWith(signatureForm, DDOC_SIGNATURE_FORM_PREFIX);
    }
}
