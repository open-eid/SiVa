/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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
import org.junit.Test;

import static ee.openeid.siva.statistics.ContainerTypeResolver.resolveContainerType;
import static org.junit.Assert.*;

public class ContainerTypeResolverTest {

    private static final String PADES_SIGNATURE_FORMAT_PREFIX = "PAdES_";
    private static final String ASIC_E_SIGNATURE_FORM = "ASiC-E";
    private static final String ASIC_S_SIGNATURE_FORM = "ASiC-S";
    private static final String XROAD_SIGNATURE_FORM = "ASiC-E_batchsignature";
    private static final String DDOC_SIGNATURE_FORM_PREFIX = "DIGIDOC_XML_";

    private static final String PADES_CONTAINER_TYPE = "PAdES";
    private static final String ASIC_E_CONTAINER_TYPE = "ASiC-E";
    private static final String ASIC_S_CONTAINER_TYPE = "ASiC-S";
    private static final String DDOC_CONTAINER_TYPE = "DIGIDOC_XML";
    
    private static final String NA = "N/A";

    @Test
    public void noSignatureFormAndNoSignaturesReturnsNa() {
        assertEquals(NA, resolveContainerType(new ValidationConclusion()));
    }

    @Test
    public void noSignatureFormAndSignatureWithPadesPrefixReturnsPades() {
        assertEquals(PADES_CONTAINER_TYPE,
                resolveContainerType(reportWithSignatureFormat(PADES_SIGNATURE_FORMAT_PREFIX + "some suffix")));
    }

    @Test
    public void determiningPadesUsesFirstSignature() {
        ValidationConclusion report = new ValidationConclusionBuilder()
                .addSignatureWithFormat("RANDOM")
                .addSignatureWithFormat(PADES_SIGNATURE_FORMAT_PREFIX + "some suffix")
                .build();

        assertEquals(NA, resolveContainerType(report));
    }

    @Test
    public void signatureFormatWithPadesPrefixNotInTheBeginningReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureFormat("b" + PADES_SIGNATURE_FORMAT_PREFIX)));
        assertEquals(NA, resolveContainerType(reportWithSignatureFormat("b" + PADES_SIGNATURE_FORMAT_PREFIX + "s")));
    }

    @Test
    public void nonSupportedSignatureFormReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("SOMETHING")));
    }

    @Test
    public void emptySignatureFormReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("")));
    }

    @Test
    public void asiceSignatureFormReturnsAsiceContainerType() {
        assertEquals(ASIC_E_CONTAINER_TYPE, resolveContainerType(reportWithSignatureForm(ASIC_E_SIGNATURE_FORM)));
    }

    @Test
    public void xRoadSignatureFormReturnsAsiceContainerType() {
        assertEquals(ASIC_E_CONTAINER_TYPE, resolveContainerType(reportWithSignatureForm(XROAD_SIGNATURE_FORM)));
    }

    @Test
    public void asicsSignatureFormReturnsAsicsContainerType(){
        assertEquals(ASIC_S_CONTAINER_TYPE, resolveContainerType(reportWithSignatureForm(ASIC_S_SIGNATURE_FORM)));
    }

    @Test
    public void signatureFormWithDDOCPrefixReturnsDDOCContainerType() {
        assertEquals(DDOC_CONTAINER_TYPE,
                resolveContainerType(reportWithSignatureForm(DDOC_SIGNATURE_FORM_PREFIX + "some suffix")));
    }

    @Test
    public void signatureFormWithDDOCPrefixNotInTheBeginningReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + DDOC_SIGNATURE_FORM_PREFIX)));
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + DDOC_SIGNATURE_FORM_PREFIX + "s")));
    }

    @Test
    public void signatureFormThatContainsButNotEqualsAValidSignatureFormReturnsNa() {
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + ASIC_E_SIGNATURE_FORM)));
        assertEquals(NA, resolveContainerType(reportWithSignatureForm("b" + XROAD_SIGNATURE_FORM)));
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
