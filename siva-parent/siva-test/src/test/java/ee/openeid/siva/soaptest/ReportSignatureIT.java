/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.soaptest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@Category(IntegrationTest.class)
public class ReportSignatureIT extends SiVaSoapTests {

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(TEST_FILES_DIRECTORY);
    }

    @Test
    public void whenRequestingSimpleReport_thenValidationReportSignatureShouldNotBeInResponse() {
        Document report = extractValidateDocumentResponseDom(post(validationRequestForDocument("hellopades-pades-lt-sha256-sign.pdf")).andReturn().body().asString());
        Assert.assertThat(getValidateDocumentResponseFromDom(report).getValidationReportSignature(), emptyOrNullString());
    }

    @Test
    @Ignore("SIVA-196")
    public void whenRequestingDetailedReport_thenValidationReportSignatureShouldBeInResponse() {
        Document report = extractValidateDocumentResponseDom(post(validationRequestForDocumentReportType("hellopades-pades-lt-sha256-sign.pdf", "Detailed")).andReturn().body().asString());
        Assert.assertThat(getValidateDocumentResponseFromDom(report).getValidationReportSignature(), not(emptyOrNullString()));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
