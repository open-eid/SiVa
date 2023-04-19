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

    @Test
    public void validAsiceContainer() {
        post(validationRequestFor("AsiceContainerValidMimetype.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(1));
    }

    @Test
    public void validAsicsContainerContainingTmpFile() {
        post(validationRequestFor("AsicsContainerValidMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings.size()", Matchers.is(1));
    }

    @Test
    public void validAsicsContainerContainingDdocContainer() {
        post(validationRequestFor("Ddoc_as_AsicsContainerValidMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(1));
    }

    @Test
    public void validBdocContainer() {
        post(validationRequestFor("BdocContainerValidMimetype.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(1));
    }

    @Test
    public void invalidAsiceContainerWithMimetypeAsLast() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("AsiceContainerMimetypeAsLast.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("\"mimetype\" should be the first file in the container"));
    }

    @Test
    public void invalidAsicsContainerContainingTmpFileWithMimetypeAsLast() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("AsicsContainerMimetypeAsLast.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("\"mimetype\" should be the first file in the container"));
    }

    @Test
    public void invalidAsicsContainerContainingDdocContainerWithMimetypeAsLast() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("Ddoc_as_AsicsContainerMimetypeAsLast.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("\"mimetype\" should be the first file in the container"));
    }

    @Test
    public void invalidBdocContainerWithMimetypeAsLast() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithMimetypeAsLast/");
        post(validationRequestFor("BdocContainerMimetypeAsLast.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("\"mimetype\" should be the first file in the container"));
    }

    @Test
    public void invalidAsiceContainerWithDeflatedMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("AsiceContainerMimetypeIsDeflated.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("Container \"mimetype\" file must not be compressed"));
    }

    @Test
    public void invalidAsicsContainerContainingTmpFileWithMimetypeIsDeflated() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("AsicsContainerMimetypeIsDeflated.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("Container \"mimetype\" file must not be compressed"));
    }

    @Test
    public void invalidAsicsContainerContainingDdocContainerWithDeflatedMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("Ddoc_as_AsicsContainerMimetypeIsDeflated.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("Container \"mimetype\" file must not be compressed"));
    }

    @Test
    public void invalidBdocContainerWithMimetypeIsDeflated() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithDeflatedMimetype/");
        post(validationRequestFor("BdocContainerMimetypeIsDeflated.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("Container \"mimetype\" file must not be compressed"));
    }

    @Test
    public void invalidAsiceContainerWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("AsiceContainerNoMimetype.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("\"mimetype\" should be the first file in the container"));
    }

    @Test
    public void invalidAsicsContainerContainingTmpFileWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("AsicsContainerNoMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_FAILED))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(0))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("\"mimetype\" should be the first file in the container"));
    }

    @Test
    public void invalidAsicsContainerContainingDdocContainerWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("Ddoc_as_AsicsContainerNoMimetype.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validationWarnings.size()", Matchers.is(2))
                .body("validationWarnings[1].content", Matchers.is("\"mimetype\" should be the first file in the container"));
    }

    @Test
    public void invalidBdocContainerWithNoMimetype() {
        setTestFilesDirectory("mimetype_validation_test_files/InvalidContainers/ContainersWithNoMimetype/");
        post(validationRequestFor("BdocContainerNoMimetype.bdoc"))
                .then().rootPath("requestErrors[0]")
                .body("message", Matchers.is("Document malformed or not matching documentType"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
