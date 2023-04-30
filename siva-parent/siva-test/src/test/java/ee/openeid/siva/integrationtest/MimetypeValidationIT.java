/*
 * Copyright 2017 - 2022 Riigi Infosüsteemide Amet
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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static ee.openeid.siva.common.Constants.MIMETYPE_COMPRESSED_WARNING;
import static ee.openeid.siva.common.Constants.MIMETYPE_NOT_FIRST_WARNING;
import static ee.openeid.siva.common.Constants.TEST_ENV_VALIDATION_WARNING;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_FORMAT_XADES_LT;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_FORMAT_XADES_LT_TM;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_FORM_ASICE;
import static ee.openeid.siva.integrationtest.TestData.TOTAL_FAILED;
import static ee.openeid.siva.integrationtest.TestData.TOTAL_PASSED;
import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;

@Tag("IntegrationTest")
public class MimetypeValidationIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "mimetype_validation_test_files/ValidContainers/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @BeforeEach
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Asice-mimetype-validation-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: ASICe container with valid mimetype.
     *
     * Expected Result: Validation report is returned without mimetype validation warnings.
     *
     * File: AsiceContainerValidMimetype.asice
     */
    @Test
    public void asiceValidMimetype() {
        post(validationRequestFor("AsiceContainerValidMimetype.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings.content", Matchers.hasItem(TEST_ENV_VALIDATION_WARNING));
    }

    /**
     * TestCaseID: Asice-mimetype-validation-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICe container with mimetype as last in cointainer.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsiceContainerMimetypeAsLast.asice
     */
    @Test
    public void asiceInvalidMimetypeLocationAsLast() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("AsiceContainerMimetypeAsLast.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asice-mimetype-validation-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICe container with deflated mimetype.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "Container "mimetype" file must not be compressed".
     *
     * File: AsiceContainerMimetypeIsDeflated.asice
     */
    @Test
    public void asiceInvalidMimetypeCompressionAsDeflated() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("AsiceContainerMimetypeIsDeflated.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_COMPRESSED_WARNING));
    }

    /**
     * TestCaseID: Asice-mimetype-validation-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICe container without mimetype.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsiceContainerNoMimetype.asice
     */
    @Test
    public void asiceWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("AsiceContainerNoMimetype.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Bdoc-mimetype-validation-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: BDOC container with valid mimetype.
     *
     * Expected Result: Validation report is returned without mimetype validation warnings.
     *
     * File: BdocContainerValidMimetype.bdoc
     */
    @Test
    public void bdocValidMimetype() {
        post(validationRequestFor("BdocContainerValidMimetype.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings.content", Matchers.hasItem(TEST_ENV_VALIDATION_WARNING));
    }

    /**
     * TestCaseID: Bdoc-mimetype-validation-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid BDOC container with mimetype as last.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: BdocContainerMimetypeAsLast.bdoc
     */
    @Test
    public void bdocInvalidMimetypeLocationAsLast() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("BdocContainerMimetypeAsLast.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Bdoc-mimetype-validation-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid BDOC container with deflated mimetype.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "Container "mimetype" file must not be compressed".
     *
     * File: BdocContainerMimetypeIsDeflated.bdoc
     */
    @Test
    public void bdocInvalidMimetypeCompressionAsDeflated() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("BdocContainerMimetypeIsDeflated.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_COMPRESSED_WARNING));
    }

    /**
     * TestCaseID: Bdoc-mimetype-validation-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid BDOC container without mimetype.
     *
     * Expected Result: HTTP 400 is returned with error message "Document malformed or not matching documentType".
     *
     * File: BdocContainerNoMimetype.bdoc
     */
    @Test
    public void invalidBdocContainerWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("BdocContainerNoMimetype.bdoc"))
                .then().rootPath("requestErrors[0]")
                .body("message", Matchers.is("Document malformed or not matching documentType"));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: ASICs container with valid mimetype and Tmp file inside.
     *
     * Expected Result: Validation report is returned without mimetype validation warnings.
     *
     * File: AsicsContainerValidMimetype.asics
     */
    @Test
    public void asicsValidMimetypeWithTmpFile() {
        post(validationRequestFor("AsicsContainerValidMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings.content", Matchers.hasItem(TEST_ENV_VALIDATION_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: ASICs container with valid mimetype and DDOC inside.
     *
     * Expected Result: Validation report is returned without mimetype validation warnings.
     *
     * File: Ddoc_as_AsicsContainerValidMimetype.asics
     */
    @Test
    public void asicsValidMimetypeWithDdocContainer() {
        post(validationRequestFor("Ddoc_as_AsicsContainerValidMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings.content", Matchers.hasItem(TEST_ENV_VALIDATION_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICs container with mimetype as last and Tmp file inside.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsicsContainerMimetypeAsLast.asics
     */
    @Test
    public void asicsInvalidMimetypeLocationAsLastWithTmpFile() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("AsicsContainerMimetypeAsLast.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICs container with mimetype as last and DDOC inside.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: Ddoc_as_AsicsContainerMimetypeAsLast.asics
     */
    @Test
    public void asicsInvalidMimetypeLocationAsLastWithDdoc() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("Ddoc_as_AsicsContainerMimetypeAsLast.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICs container with deflated mimetype and Tmp file inside.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "Container "mimetype" file must not be compressed".
     *
     * File: AsicsContainerMimetypeIsDeflated.asics
     */
    @Test
    public void asicsInvalidMimetypeCompressionAsDeflatedWithTmpFile() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("AsicsContainerMimetypeIsDeflated.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_COMPRESSED_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICs container with deflated mimetype and DDOC inside.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "Container "mimetype" file must not be compressed".
     *
     * File: Ddoc_as_AsicsContainerMimetypeIsDeflated.asics
     */
    @Test
    public void asicsInvalidMimetypeCompressionAsDeflatedWithDdoc() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("Ddoc_as_AsicsContainerMimetypeIsDeflated.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_COMPRESSED_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICs container without mimetype and Tmp file inside.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsicsContainerNoMimetype.asics
     */
    @Test
    public void asicsContainingTmpFileWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("AsicsContainerNoMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICs container without mimetype and DDOC inside.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: Ddoc_as_AsicsContainerNoMimetype.asics
     */
    @Test
    public void asicsContainingDdocContainerWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("Ddoc_as_AsicsContainerNoMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}