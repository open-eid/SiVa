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
     * Title:
     * <p>
     * Expected Result:
     * <p>
     * File: DDOCinsideAsics.asics
     */
    @Test
    public void validDdocInsideValidAsics() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ValidDDOCinsideAsics.asics"));
        post(validationRequestWithValidKeys(encodedString, "ValidDDOCinsideAsics.asics", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationConclusion.validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3.ddoc"))
                .body("validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asics-ValidationPass-2
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
    public void validDdocInsideValidAsicsScsExtension() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ValidDDOCinsideAsics.scs"));
        post(validationRequestWithValidKeys(encodedString, "ValidDDOCinsideAsics.scs", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationConclusion.validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3.ddoc"))
                .body("validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asics-ValidationPass-3
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
    public void validBdocInsideValidAsics() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ValidBDOCinsideAsics.asics"));
        post(validationRequestWithValidKeys(encodedString, "ValidBDOCinsideAsics.asics", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2016-05-11T10:18:06Z"))
                .body("validationConclusion.signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[1].info.bestSignatureTime", Matchers.is("2016-05-11T10:19:38Z"))
                .body("validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationConclusion.validatedDocument.filename", Matchers.is("Valid_IDCard_MobID_signatures.bdoc"))
                .body("validationConclusion.signaturesCount", Matchers.is(2))
                .body("validationConclusion.validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Asics-ValidationPass-4
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
    @Test //TODO: We should return 0 as signatureCount?
    public void textInsideValidAsics() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("TXTinsideAsics.asics"));
        post(validationRequestWithValidKeys(encodedString, "TXTinsideAsics.asics", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-25T09:56:33Z"))
                .body("validationConclusion.validatedDocument.filename", Matchers.is("TXTinsideAsics.asics"));
    }

    /**
     * TestCaseID: Asics-ValidationPass-5
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
    @Test //TODO: We should return 0 as signatureCount?
    public void asicsInsideValidAsics() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ValidASICSinsideAsics.asics"));
        post(validationRequestWithValidKeys(encodedString, "ValidASICSinsideAsics.asics", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-25T11:24:01Z"))
                .body("validationConclusion.validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3(1).asics"));
    }

    /**
     * TestCaseID: Asics-ValidationPass-6
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
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ValidDDOCinsideAsics.zip"));
        post(validationRequestWithValidKeys(encodedString, "ValidDDOCinsideAsics.zip", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationConclusion.validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3.ddoc"))
                .body("validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asics-ValidationPass-7
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
    public void ValidDdocInsideValidAsicsWrongMimeType() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ValidDDOCinsideAsicsWrongMime.asics"));
        post(validationRequestWithValidKeys(encodedString, "ValidDDOCinsideAsicsWrongMime.asics", VALID_SIGNATURE_POLICY_5))
                .then()
                .body("validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationConclusion.timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("validationConclusion.timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("validationConclusion.validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3.ddoc"))
                .body("validationConclusion.signaturesCount", Matchers.is(1))
                .body("validationConclusion.validSignaturesCount", Matchers.is(1));
    }



    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
