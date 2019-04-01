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

package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class AsicsValidationPassIT extends SiVaRestTests {

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
     * Title: Validation of ASICs with DDOC inside
     * <p>
     * Expected Result: TST and inner DDOC are valid
     * <p>
     * File: ValidDDOCinsideAsics.asics
     */
    @Test
    public void validDdocInsideValidAsics() {
        post(validationRequestFor("ValidDDOCinsideAsics.asics"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("ValidDDOCinsideAsics.asics"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("Q9GFsDnNIddFXMgD05wa8ZWQfo0gihucxGgQjefYfjE="))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asics-ValidationPass-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Validation of ASICs with DDOC inside SCS extension
     * <p>
     * Expected Result: TST and inner DDOC are valid
     * <p>
     * File: ValidDDOCinsideAsics.scs
     */
    @Test
    public void validDdocInsideValidAsicsScsExtension() {
        post(validationRequestFor( "ValidDDOCinsideAsics.scs"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("ValidDDOCinsideAsics.scs"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("Q9GFsDnNIddFXMgD05wa8ZWQfo0gihucxGgQjefYfjE="))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asics-ValidationPass-3
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Validation of ASICs with BDOC inside
     * <p>
     * Expected Result: TST and inner BDOC are valid
     * <p>
     * File: ValidBDOCinsideAsics.asics
     */
    @Test
    public void validBdocInsideValidAsics() {
        post(validationRequestFor("ValidBDOCinsideAsics.asics"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("ValidBDOCinsideAsics.asics"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("cQrPKVeeWpikjjkn3StO6/1Eau+nt6OAPHV+QtZg9LI="))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-05-11T10:18:06Z"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("38211015222"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.is("NURM,AARE,38211015222"))
                .body("validationReport.validationConclusion.signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[1].info.bestSignatureTime", Matchers.is("2016-05-11T10:19:38Z"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Asics-ValidationPass-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Validation of ASICs with text document inside
     * <p>
     * Expected Result: TST is valid
     * <p>
     * File: TXTinsideAsics.asics
     */
    @Test
    public void textInsideValidAsics() {
        post(validationRequestFor("TXTinsideAsics.asics"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-25T09:56:33Z"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("TXTinsideAsics.asics"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("2SMbApVw0qFlMdSU8Jn4mwPzsf8Wo6ERUo2uJLNxEEo=")).log().all();
    }

    /**
     * TestCaseID: Asics-ValidationPass-5
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Validation of ASICs with ASICs inside
     * <p>
     * Expected Result: TST is valid, no inner looping of ASICs
     * <p>
     * File: ValidASICSinsideAsics.asics
     */
    @Test
    public void asicsInsideValidAsics() {
        post(validationRequestFor("ValidASICSinsideAsics.asics"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-25T11:24:01Z"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("ValidASICSinsideAsics.asics"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("PMpItHz71+zPU8Z7SIhiJD2qkNbAbfMa4ooCi2fmKTY="));
    }

    /**
     * TestCaseID: Asics-ValidationPass-6
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Validation of ASICs with DDOC inside ZIP extension
     * <p>
     * Expected Result: TST and inner DDOC are valid
     * <p>
     * File: ValidDDOCinsideAsics.zip
     */
    @Test
    public void ValidDdocInsideValidAsicsZipExtension() {
        post(validationRequestFor("ValidDDOCinsideAsics.zip"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("ValidDDOCinsideAsics.zip"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("Q9GFsDnNIddFXMgD05wa8ZWQfo0gihucxGgQjefYfjE="));
    }

    /**
     * TestCaseID: Asics-ValidationPass-7
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Validation of ASICs with wrong mimetype with DDOC inside
     * <p>
     * Expected Result: TST and inner DDOC are valid
     * <p>
     * File: ValidDDOCinsideAsicsWrongMime.asics
     */
    @Test
    public void ValidDdocInsideValidAsicsWrongMimeType() {
        post(validationRequestFor("ValidDDOCinsideAsicsWrongMime.asics"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("ValidDDOCinsideAsicsWrongMime.asics"))
                .body("validationReport.validationConclusion.validatedDocument.fileHash", Matchers.is("EKuM3zEBq2A4hGTmfi7IQW+rDaZ0PkD14rhu3UuKAis="))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationReport.validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
