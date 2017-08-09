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

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class PDFWithInvalidSignaturesTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE = "hellopades-pades-b-sha256-auth.pdf";

    @Test
    @Ignore("java.lang.OutOfMemoryError: Java heap space may occur")
    public void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithNoValidSignatures() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE));
        assertNotNull(report);
        assertTrue(report.getSignaturesCount() == 1);
        assertTrue(report.getValidSignaturesCount() == 0);
    }

    @Ignore(/*TODO:*/"SignatureFormatConstraint outputs error node in wrong format, so error is not parsed correctly to report (VAL-197)")
    @Test
    public void validatingPdfWithOneBaselineProfileBSignatureReturnsReportWithOneCorrectlyFormattedError() throws Exception {
        QualifiedReport report = validationService.validateDocument(
                buildValidationDocument(PDF_WITH_ONE_BASELINE_PROFILE_B_SIGNATURE));
        System.out.println(report);
        SignatureValidationData signature = report.getSignatures().get(0);
        assertEquals("TOTAL-FAILED", signature.getIndication());
        assertTrue(signature.getErrors().size() == 1);
        Error error = signature.getErrors().get(0);

        //check error object integrity
        assertTrue(StringUtils.isNotBlank(error.getContent()));

    }

}
