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

import org.junit.Test;

import static ee.openeid.siva.statistics.SignatureFormToContainerTypeTransormer.transformToContainerTypeOrEmpty;
import static org.junit.Assert.*;

public class SignatureFormToContainerTypeTransormerTest {
    private static final String ASIC_E_SIGNATURE_FORM = "ASiC-E";
    private static final String ASIC_S_SIGNATURE_FORM = "ASiC-S";
    private static final String XROAD_SIGNATURE_FORM = "ASiC-E_batchsignature";
    private static final String PDF_SIGNATURE_FORM = "PAdES";
    private static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";

    private static final String ASIC_E_CONTAINER_TYPE = "ASiC-E";
    private static final String ASIC_S_CONTAINER_TYPE = "ASiC-S";
    private static final String XROAD_CONTAINER_TYPE = "ASiC-E (BatchSignature)";
    private static final String DDOC_CONTAINER_TYPE = "XAdES";

    @Test
    public void transformingNullShouldReturnEmptyString() {
        assertEquals("", transformToContainerTypeOrEmpty(null));
    }

    @Test
    public void transformingNonSupportedSignatureFormReturnsEmptyString() {
        assertEquals("", transformToContainerTypeOrEmpty("SOMETHING"));
    }

    @Test
    public void transformingEmptySignatureFormReturnsEmptyString() {
        assertEquals("", transformToContainerTypeOrEmpty(""));
    }

    @Test
    public void transformingAsiceSignatureFormReturnsAsiceContainerType() {
        assertEquals(ASIC_E_CONTAINER_TYPE, transformToContainerTypeOrEmpty(ASIC_E_SIGNATURE_FORM));
    }
    @Test
    public void transformingAsicsSignatureFormReturnsAsicsContainerType(){
        assertEquals(ASIC_S_CONTAINER_TYPE, transformToContainerTypeOrEmpty(ASIC_S_SIGNATURE_FORM));
    }
    @Test
    public void transformingXRoadSignatureFormReturnsXRoadContainerType() {
        assertEquals(XROAD_CONTAINER_TYPE, transformToContainerTypeOrEmpty(XROAD_SIGNATURE_FORM));
    }

    @Test
    public void transformingSignatureFormWithDDOCPrefixReturnsDDOCContainerType() {
        assertEquals(DDOC_CONTAINER_TYPE, transformToContainerTypeOrEmpty(DDOC_SIGNATURE_FORM_PREFIX));
        assertEquals(DDOC_CONTAINER_TYPE, transformToContainerTypeOrEmpty(DDOC_SIGNATURE_FORM_PREFIX + "some suffix"));
    }

    @Test
    public void transformingSignatureFormWithDDOCPrefixNotInTheBeginningReturnsEmptyString() {
        assertEquals("", transformToContainerTypeOrEmpty("b" + DDOC_SIGNATURE_FORM_PREFIX));
        assertEquals("", transformToContainerTypeOrEmpty("b" + DDOC_SIGNATURE_FORM_PREFIX + "s"));
    }

    @Test
    public void transformingSignatureFormThatContainsButNotEqualsAValidSignatureFormReturnsEmptyString() {
        assertEquals("", transformToContainerTypeOrEmpty("b" + ASIC_E_SIGNATURE_FORM));
        assertEquals("", transformToContainerTypeOrEmpty("b" + XROAD_SIGNATURE_FORM));
        assertEquals("", transformToContainerTypeOrEmpty("b" + PDF_SIGNATURE_FORM));
    }
}
