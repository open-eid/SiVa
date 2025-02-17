/*
 * Copyright 2019 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.request.validation;

import ee.openeid.siva.webapp.request.validation.annotations.ValidReportType;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

class ValidReportTypeTest extends AnnotationValidatorTestBase {

    private static final String INVALID_REPORT_TYPE = "Invalid report type";

    @Test
    void validReportTypes() {
        validReportType("simple");
        validReportType("simPLE");
        validReportType("SIMPLE");

        validReportType("detailed");
        validReportType("detaILED");
        validReportType("DETAILED");

        validReportType("diagnostic");
        validReportType("diagnOSTIC");
        validReportType("DIAGNOSTIC");

        validReportType(null);
    }

    @Test
    void invalidReportTypes() {
        invalidReportType("simple1", INVALID_REPORT_TYPE);
        invalidReportType("", INVALID_REPORT_TYPE);
        invalidReportType(" ", INVALID_REPORT_TYPE);
    }

    private void validReportType(String signatureFilename) {
        assertNoViolations(new MockTestTarget(signatureFilename), signatureFilename);
    }

    private void invalidReportType(String signatureFilename, String... errorMessages) {
        assertViolations(new MockTestTarget(signatureFilename), signatureFilename, errorMessages);
    }

    @AllArgsConstructor
    class MockTestTarget implements TestClassWithAnnotatedFields {

        @ValidReportType
        String reportType;
    }
}
