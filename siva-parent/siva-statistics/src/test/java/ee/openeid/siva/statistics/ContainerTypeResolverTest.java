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

import ee.openeid.siva.validation.document.report.ValidationConclusion;
import org.junit.jupiter.api.Test;

import static ee.openeid.siva.statistics.ContainerTypeResolver.resolveContainerType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContainerTypeResolverTest {

    private static final String PADES_SIGNATURE_FORMAT_PREFIX = "PAdES_";
    private static final String ASIC_E_SIGNATURE_FORM = "ASiC-E";
    private static final String ASIC_S_SIGNATURE_FORM = "ASiC-S";
    private static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";

    private static final String PADES_CONTAINER_TYPE = "PAdES";
    private static final String ASIC_E_CONTAINER_TYPE = "ASiC-E";
    private static final String ASIC_S_CONTAINER_TYPE = "ASiC-S";
    private static final String DDOC_CONTAINER_TYPE = "DIGIDOC_XML";
    
    private static final String NA = "N/A";

    @Test
    void noSignatureFormAndNoSignaturesReturnsNa() {
        assertEquals(NA, resolveContainerType(new ValidationConclusion()));
    }

    @Test
    void noSignatureFormAndSignatureWithPadesPrefixReturnsPades() {
        assertEquals(PADES_CONTAINER_TYPE,
                resolveContainerType(reportWithSignatureFormat(PADES_SIGNATURE_FORMAT_PREFIX + "some suffix")));
    }

    @Test
    void determiningPadesUsesFirstSignature() {
        ValidationConclusion report = new ValidationConclusionBuilder()
                .addSignatureWithFormat("RANDOM")
                .addSignatureWithFormat(PADES_SIGNATURE_FORMAT_PREFIX + "some suffix")
                .build();

        assertEquals(NA, resolveContainerType(report));
    }

    @Test
    void signatureFormatWithPadesPrefixNotInTheBeginningReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureFormat("b" + PADES_SIGNATURE_FORMAT_PREFIX)));
        assertEquals(NA, resolveContainerType(reportWithSignatureFormat("b" + PADES_SIGNATURE_FORMAT_PREFIX + "s")));
    }

    @Test
    void nonSupportedSignatureFormReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("SOMETHING")));
    }

    @Test
    void emptySignatureFormReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("")));
    }

    @Test
    void asiceSignatureFormReturnsAsiceContainerType() {
        assertEquals(ASIC_E_CONTAINER_TYPE, resolveContainerType(reportWithSignatureForm(ASIC_E_SIGNATURE_FORM)));
    }

    @Test
    void asicsSignatureFormReturnsAsicsContainerType(){
        assertEquals(ASIC_S_CONTAINER_TYPE, resolveContainerType(reportWithSignatureForm(ASIC_S_SIGNATURE_FORM)));
    }

    @Test
    void signatureFormWithDDOCPrefixReturnsDDOCContainerType() {
        assertEquals(DDOC_CONTAINER_TYPE,
                resolveContainerType(reportWithSignatureForm(DDOC_SIGNATURE_FORM_PREFIX + "some suffix")));
    }

    @Test
    void signatureFormWithDDOCPrefixNotInTheBeginningReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + DDOC_SIGNATURE_FORM_PREFIX)));
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + DDOC_SIGNATURE_FORM_PREFIX + "s")));
    }

    @Test
    void signatureFormThatContainsButNotEqualsAValidSignatureFormReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + ASIC_E_SIGNATURE_FORM)));
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + ASIC_S_SIGNATURE_FORM)));
    }

    private ValidationConclusion reportWithSignatureFormat(String signatureFormat) {
        return new ValidationConclusionBuilder()
                .addSignatureWithFormat(signatureFormat)
                .build();
    }

    private ValidationConclusion reportWithSignatureForm(String signatureForm) {
        return new ValidationConclusionBuilder()
                .withSignatureForm(signatureForm)
                .build();
    }
}
