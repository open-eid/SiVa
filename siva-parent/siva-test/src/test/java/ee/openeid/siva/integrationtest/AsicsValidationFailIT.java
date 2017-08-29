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
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class AsicsValidationFailIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "asics/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Asics-ValidationPass-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title:
     * <p>
     * Expected Result:
     * <p>
     * File: DDOCinsideAsics.asics
     */
    @Test
    public void ValidDdocInsideValidAsicsZipExtension() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TwoDataFilesAsics.asics"));
        post(validationRequestWithValidKeys(encodedString, "TwoDataFilesAsics.asics", null, VALID_SIGNATURE_POLICY_5))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is("Document does not meet the requirements"));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
