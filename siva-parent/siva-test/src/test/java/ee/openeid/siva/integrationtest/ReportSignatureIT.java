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

package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

@Category(IntegrationTest.class)
public class ReportSignatureIT extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }

    @Test
    public void whenRequestingSimpleReport_thenValidationReportSignatureShouldNotBeInResponse() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", null, "Simple"))
                .then()
                .body("validationReportSignature", isEmptyOrNullString());
    }

    @Test
    public void whenRequestingDetailedReport_thenValidationReportSignatureShouldBeInResponse() {
        post(validationRequestFor("hellopades-pades-lt-sha256-sign.pdf", null, "Detailed"))
                .then()
                .body("validationReportSignature", not(isEmptyOrNullString()));
    }

} 
