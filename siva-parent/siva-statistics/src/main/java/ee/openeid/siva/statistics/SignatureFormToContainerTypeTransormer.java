/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class SignatureFormToContainerTypeTransormer {

    private static final String ASIC_E_SIGNATURE_FORM = "ASiC_E";
    private static final String XROAD_SIGNATURE_FORM = "ASiC_E_batchsignature";
    private static final String PDF_SIGNATURE_FORM = "PAdES";
    private static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";

    private static final String ASIC_E_CONTAINER_TYPE = "ASiC-E";
    private static final String XROAD_CONTAINER_TYPE = "ASiC-E (BatchSignature)";
    private static final String PDF_CONTAINER_TYPE= "PAdES";
    private static final String DDOC_CONTAINER_TYPE = "XAdES";

    static String transformToContainerTypeOrEmpty(String signatureForm) {
        if (signatureForm == null) {
            return valueNotPresent();
        }
        if (isAsic(signatureForm)) {
            return ASIC_E_CONTAINER_TYPE;
        }
        if (isXRoad(signatureForm)) {
            return XROAD_CONTAINER_TYPE;
        }
        if (isPdf(signatureForm)) {
            return PDF_CONTAINER_TYPE;
        }
        if (isDdoc(signatureForm)) {
            return DDOC_CONTAINER_TYPE;
        }
        return valueNotPresent();
    }

    private static boolean isAsic(String signatureForm) {
        return signatureForm.equals(ASIC_E_SIGNATURE_FORM);
    }

    private static boolean isXRoad(String signatureForm) {
        return signatureForm.equals(XROAD_SIGNATURE_FORM);
    }

    private static boolean isDdoc(String signatureForm) {
        return signatureForm.startsWith(DDOC_SIGNATURE_FORM_PREFIX);
    }

    private static boolean isPdf(String signatureForm) {
        return signatureForm.equals(PDF_SIGNATURE_FORM);
    }

    private static String valueNotPresent() {
        return StringUtils.EMPTY;
    }
}
