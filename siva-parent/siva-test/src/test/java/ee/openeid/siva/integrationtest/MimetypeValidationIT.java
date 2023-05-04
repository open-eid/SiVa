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
import static ee.openeid.siva.common.Constants.MIMETYPE_EXTRA_FIELDS_WARNING;
import static ee.openeid.siva.common.Constants.MIMETYPE_INVALID_TYPE;
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
     * TestCaseID: Asice-mimetype-validation-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICe container mimetype filename with capital letter (Mimetype).
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsiceContainerMimetypeWithCapitalLetter.asice
     */
    @Test
    public void asiceMimetypeFileNameWithCapitalLetter() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("AsiceContainerMimetypeWithCapitalLetter.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asice-mimetype-validation-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICe container, where mimetype filename is with extra space in the end ("mimetype ").
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsiceContainerMimetypeFilenameWithExtraSpace.asice
     */
    @Test
    public void asiceMimetypeFilenameWithExtraSpace() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("AsiceContainerMimetypeFilenameWithExtraSpace.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asice-mimetype-validation-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICe container with extra byte in the beginning of the container.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsiceContainerMimetypeWithCapitalLetter.asice
     */
    @Test
    public void asiceContainerWithExtraByteInBeginning() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("AsiceContainerMimetypeWithCapitalLetter.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asice-mimetype-validation-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: ASICe container with invalid mimetype as "text/plain".
     *
     * Expected Result: Validation report is returned with mimetype validation warning "Container should have one of the expected mimetypes: "application/vnd.etsi.asic-e+zip", "application/vnd.etsi.asic-s+zip"".
     *
     * File: AsiceInvalidMimetypeAsText.asice
     */
    @Test
    public void asiceInvalidMimetypeAsText() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("AsiceInvalidMimetypeAsText.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_INVALID_TYPE));
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
    public void bdocWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("BdocContainerNoMimetype.bdoc"))
                .then().rootPath("requestErrors[0]")
                .body("message", Matchers.is("Document malformed or not matching documentType"));
    }

    /**
     * TestCaseID: Bdoc-mimetype-validation-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid BDOC container, where mimetype filename is with extra space in the end ("mimetype ").
     *
     * Expected Result: HTTP 400 is returned with error message "Document malformed or not matching documentType".
     *
     * File: BdocContainerMimetypeFilenameWithExtraSpace.bdoc
     */
    @Test
    public void bdocMimetypeFilenameWithExtraSpace() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("BdocContainerMimetypeFilenameWithExtraSpace.bdoc"))
                .then().rootPath("requestErrors[0]")
                .body("message", Matchers.is("Document malformed or not matching documentType"));
    }

    /**
     * TestCaseID: Bdoc-mimetype-validation-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: BDOC container with invalid mimetype as "application/zip".
     *
     * Expected Result: HTTP 400 is returned with error message "Document malformed or not matching documentType".
     *
     * File: BdocInvalidMimetypeAsZip.bdoc
     */
    @Test
    public void bdocInvalidMimetypeAsZip() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("BdocInvalidMimetypeAsZip.bdoc"))
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
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
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

    /**
     * TestCaseID: Asics-mimetype-validation-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid ASICs container, where mimetype filename is with extra space in the end ("mimetype ").
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: AsicsContainerMimetypeFilenameWithExtraSpace.asics
     */
    @Test
    public void asicsMimetypeFilenameWithExtraSpace() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("AsicsContainerMimetypeFilenameWithExtraSpace.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Asics-mimetype-validation-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: ASICs container with invalid mimetype as "application/xml".
     *
     * Expected Result: Validation report is returned with mimetype validation warning "Container should have one of the expected mimetypes: "application/vnd.etsi.asic-e+zip", "application/vnd.etsi.asic-s+zip"".
     *
     * File: AsicsInvalidMimetypeAsXml.asics
     */
    @Test
    public void asicsInvalidMimetypeAsXml() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithInvalidMimetype/");
        post(validationRequestFor("AsicsInvalidMimetypeAsXml.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_INVALID_TYPE));
    }

    /**
     * TestCaseID: Edoc-mimetype-validation-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Valid EDOC container with valid.
     *
     * Expected Result: Validation report is returned without mimetype validation warnings.
     *
     * File: EdocContainerValidMimetype.edoc
     */
    @Test
    public void edocValidMimetype() {
        post(validationRequestFor("EdocContainerValidMimetype.edoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationWarnings", Matchers.hasSize(1))
                .body("validationWarnings.content", Matchers.hasItem(TEST_ENV_VALIDATION_WARNING));
    }

    /**
     * TestCaseID: Edoc-mimetype-validation-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid EDOC container with mimetype as last in cointainer.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: EdocContainerValidMimetypeAsLast.edoc
     */
    @Test
    public void edocInvalidMimetypeLocationAsLast() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("EdocContainerValidMimetypeAsLast.edoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Edoc-mimetype-validation-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Invalid EDOC container without mimetype.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "mimetype should be the first file in the container".
     *
     * File: EdocContainerNoMimetype.edoc
     */
    @Test
    public void edocWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("EdocContainerNoMimetype.edoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_NOT_FIRST_WARNING));
    }

    /**
     * TestCaseID: Adoc-mimetype-validation-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/overview/#main-features-of-siva-validation-service
     *
     * Title: Valid ADOC container with mimetype.
     *
     * Expected Result: Validation report is returned with mimetype validation warning "Container "mimetype" file must not contain "Extra fields" in its ZIP header".
     *
     * File: AdocContainerMimetypeWithExtraFields.adoc
     */
    @Test
    public void adocMimetypeWithExtraFields() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/");
        post(validationRequestFor("AdocContainerMimetypeWithExtraFields.adoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationWarnings", Matchers.hasSize(2))
                .body("validationWarnings.content", Matchers.hasItem(MIMETYPE_EXTRA_FIELDS_WARNING));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
