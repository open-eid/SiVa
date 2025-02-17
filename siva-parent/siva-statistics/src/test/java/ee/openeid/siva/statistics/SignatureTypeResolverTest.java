/*
 * Copyright 2020 - 2025 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import org.junit.jupiter.api.Test;

import static ee.openeid.siva.statistics.SignatureTypeResolver.resolveSignatureType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SignatureTypeResolverTest {

    private static final String SK_XML = "SK_XML";
    private static final String DIGIDOC_XML = "DIGIDOC_XML";
    private static final String ASIC_S = "ASiC-S";
    private static final String XADES = "XAdES";
    private static final String CADES = "CAdES";
    private static final String PADES = "PAdES";
    private static final String NA = "N/A";

    @Test
    void reportWithEmptySignaturesReturnsNa() {
        assertEquals(NA, resolveSignatureType(new ValidationConclusion()));
    }

    @Test
    void reportWithTstContainerTypeReturnsNa() {
        ValidationConclusion validationConclusion = new ValidationConclusionBuilder()
                .withSignatureForm(ASIC_S)
                .addSignatureWithFormat(XADES +  "_some_prefix")
                .addTimeStampToken(new TimeStampTokenValidationData())
                .build();
        assertEquals(NA, resolveSignatureType(validationConclusion));
    }

    @Test
    void skXmlSignatureFormatReturnsXades() {
        assertEquals(XADES, resolveSignatureType(reportWithSignatureFormat(SK_XML + "_some_prefix")));
    }

    @Test
    void digidocXmlSignatureFormatReturnsXades() {
        assertEquals(XADES, resolveSignatureType(reportWithSignatureFormat(DIGIDOC_XML + "_some_prefix")));
    }

    @Test
    void xadesSignatureFormatReturnsCades() {
        assertEquals(XADES, resolveSignatureType(reportWithSignatureFormat(XADES + "_some_prefix")));
    }

    @Test
    void cadesSignatureFormatReturnsCades() {
        assertEquals(CADES, resolveSignatureType(reportWithSignatureFormat(CADES + "_some_prefix")));
    }

    @Test
    void padesSignatureFormatReturnsCades() {
        assertEquals(PADES, resolveSignatureType(reportWithSignatureFormat(PADES + "_some_prefix")));
    }

    @Test
    void unknownSignatureFormatReturnsNa() {
        assertEquals(NA, resolveSignatureType(reportWithSignatureFormat("unknown")));
    }

    @Test
    void usesOnlyPrefixesOfSignatureFormats() {
        assertEquals(NA, resolveSignatureType(reportWithSignatureFormat("b" + SK_XML + "_some_prefix")));
        assertEquals(NA, resolveSignatureType(reportWithSignatureFormat("b" + DIGIDOC_XML + "_some_prefix")));
        assertEquals(NA, resolveSignatureType(reportWithSignatureFormat("b" + XADES + "_some_prefix")));
        assertEquals(NA, resolveSignatureType(reportWithSignatureFormat("b" + CADES + "_some_prefix")));
        assertEquals(NA, resolveSignatureType(reportWithSignatureFormat("b" + PADES + "_some_prefix")));
    }

    @Test
    void usesFirstSignatureOfReport() {
        ValidationConclusion validationConclusion = new ValidationConclusionBuilder()
                .addSignatureWithFormat("unknown")
                .addSignatureWithFormat(XADES + "_some_prefix")
                .build();
        assertEquals(NA, resolveSignatureType(validationConclusion));
    }

    private ValidationConclusion reportWithSignatureFormat(String signatureFormat) {
        return new ValidationConclusionBuilder()
                .addSignatureWithFormat(signatureFormat)
                .build();
    }
}
