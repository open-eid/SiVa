/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Test;

import static org.junit.Assert.*;

public class PDFWithNoSignaturesTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_NO_SIGNATURES = "no-signatures.pdf";

    @Test
    public void validatingPdfWithNoSignaturesReturnsReport() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_NO_SIGNATURES));
        assertNotNull(report);
        assertEquals(PDF_WITH_NO_SIGNATURES, report.getDocumentName());
        assertTrue(report.getValidSignaturesCount() == 0);
        assertTrue(report.getSignaturesCount() == 0);
        assertTrue(report.getSignatures().isEmpty());
        assertNotNull(report.getValidationTime());
    }
}
