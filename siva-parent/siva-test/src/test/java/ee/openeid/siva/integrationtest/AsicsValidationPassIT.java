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

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;

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
     * File: ddocWithRoleAndSigProductionPlace.asics
     */
    @Test
    public void validDdocInsideValidAsics() {
        post(validationRequestFor("ddocWithRoleAndSigProductionPlace.asics"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2009-06-01T10:46:42Z"))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("ei tea"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("tõrva"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("ESTEID-SK 2007 OCSP RESPONDER"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIDnDCCAoSgAwIBAgIERZ0acjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIID3zCCAsegAwIBAgIER4JChjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY 2020"))
                .body("timeStampTokens[0].signedTime", Matchers.is("2020-06-02T11:18:22Z"))
                .body("timeStampTokens[0].certificates[0].commonName",  Matchers.is("SK TIMESTAMPING AUTHORITY 2020"))
                .body("timeStampTokens[0].certificates[0].type",  Matchers.is("CONTENT_TIMESTAMP"))
                .body("timeStampTokens[0].certificates[0].content",  Matchers.startsWith("MIIEFjCCAv6gAwIBAgIQYjZ9dFrZQ6tdpFC5Xj/6bjANBgkqhk"))
                .body("validatedDocument.filename", Matchers.is("ddocWithRoleAndSigProductionPlace.asics"))
                .body("signaturesCount", Matchers.is(3))
                .body("validSignaturesCount", Matchers.is(3));
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("validatedDocument.filename", Matchers.is("ValidDDOCinsideAsics.scs"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("signatures[0].signedBy", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("validatedDocument.filename", Matchers.is("ValidBDOCinsideAsics.asics"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-05-11T10:18:06Z"))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("Signer / Proper signature"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Estonia"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("Harju"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("Tallinn"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is("22333"))
                .body("signatures[0].signedBy", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("38211015222"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[1].info.bestSignatureTime", Matchers.is("2016-05-11T10:19:38Z"))
                .body("timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(2));
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("timeStampTokens[0].signedTime", Matchers.is("2017-08-25T09:56:33Z"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(0))
                .body("validatedDocument.filename", Matchers.is("TXTinsideAsics.asics"));
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("timeStampTokens[0].signedTime", Matchers.is("2017-08-25T11:24:01Z"))
                .body("validatedDocument.filename", Matchers.is("ValidASICSinsideAsics.asics"));
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1))
                .body("validatedDocument.filename", Matchers.is("ValidDDOCinsideAsics.zip"));
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("ASiC-S"))
                .body("validatedDocument.filename", Matchers.is("ValidDDOCinsideAsicsWrongMime.asics"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("timeStampTokens[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("timeStampTokens[0].signedBy", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("timeStampTokens[0].signedTime", Matchers.is("2017-08-10T12:40:40Z"))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
