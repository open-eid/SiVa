/*
 * Copyright 2020 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.document.report.ValidationConclusion;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class SignatureTypeResolver {

    private static final String SK_XML = "SK_XML";
    private static final String DIGIDOC_XML = "DIGIDOC_XML";
    private static final String ASIC_S = "ASiC-S";

    private static final String XADES = "XAdES";
    private static final String CADES = "CAdES";
    private static final String PADES = "PAdES";

    static final String NA = "N/A";

    static String resolveSignatureType(ValidationConclusion validationConclusion) {
        if (CollectionUtils.isEmpty(validationConclusion.getSignatures()) || isTstTypeContainer(validationConclusion)) {
            return NA;
        }

        String signatureFormat = validationConclusion.getSignatures().get(0).getSignatureFormat();
        if (isDdoc(signatureFormat) || isXades(signatureFormat)) {
            return XADES;
        }
        if (isCades(signatureFormat)) {
            return CADES;
        }
        if (isPades(signatureFormat)) {
            return PADES;
        }
        return NA;
    }

    static boolean isTstTypeContainer(ValidationConclusion validationConclusion) {
        return ASIC_S.equals(validationConclusion.getSignatureForm())
                && CollectionUtils.isNotEmpty(validationConclusion.getTimeStampTokens());
    }

    private static boolean isDdoc(String signatureFormat) {
        return StringUtils.startsWith(signatureFormat, SK_XML) || StringUtils.startsWith(signatureFormat, DIGIDOC_XML);
    }

    private static boolean isXades(String signatureFormat) {
        return StringUtils.startsWith(signatureFormat, XADES);
    }

    private static boolean isCades(String signatureFormat) {
        return StringUtils.startsWith(signatureFormat, CADES);
    }

    private static boolean isPades(String signatureFormat) {
        return StringUtils.startsWith(signatureFormat, PADES);
    }

}
