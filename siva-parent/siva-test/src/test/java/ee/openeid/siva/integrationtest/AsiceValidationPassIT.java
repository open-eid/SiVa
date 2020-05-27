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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.ALL_FILES_NOT_SIGNED;
import static ee.openeid.siva.integrationtest.TestData.CERTIFICATE_DO_NOT_MATCH_TRUST_SERVICE;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_FORMAT_XADES_LT;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_FORMAT_XADES_LTA;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_FORM_ASICE;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_LEVEL_QESIG;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_SCOPE_FULL;
import static ee.openeid.siva.integrationtest.TestData.TOTAL_PASSED;
import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;
import static ee.openeid.siva.integrationtest.TestData.VALIDATION_LEVEL_ARCHIVAL_DATA;
import static ee.openeid.siva.integrationtest.TestData.VALID_SIGNATURE_SCOPE_CONTENT_FULL;

@Category(IntegrationTest.class)
public class AsiceValidationPassIT extends SiVaRestTests {
    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timestamp/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Asice-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice with single valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: ValidLiveSignature.asice
     */
    @Test
    public void validAsiceSingleSignature() {
        post(validationRequestFor("ValidLiveSignature.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-10-11T09:36:10Z"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice TM with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: BDOC-TS.asice
     */
    @Test
    public void validAsiceMultipleSignatures() {
        post(validationRequestFor("BDOC-TS.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[1].indication", Matchers.is(TOTAL_PASSED))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Asice-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice One LT signature with certificates from different countries
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-30.asice
     */
    @Test
    public void asiceDifferentCertificateCountries() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-30.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));
    }


    /**
     * TestCaseID: Asice-ValidationPass-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice Baseline-LT file
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-49.asice
     */
    @Test
    public void asiceBaselineLtProfileValidSignature() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-49.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-05-23T10:06:23Z"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice Baseline-LTA file
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LTA-V-24.asice
     */
    @Test
    public void asiceBaselineLtaProfileValidSignature() {
        post(validationRequestFor("EE_SER-AEX-B-LTA-V-24.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LTA))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2014-10-30T18:50:35Z"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice file signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-2.asice
     */
    @Test
    public void asiceWithEccSha256ValidSignature() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-2.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice file with 	ESTEID-SK 2015 certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice
     */
    @Test
    public void asiceSk2015CertificateChainValidSignature() {
        post(validationRequestFor("IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Asice file with KLASS3-SK 2010 (EECCRCA) certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-28.asice
     */
    @Test
    public void asiceKlass3Sk2010CertificateChainValidSignature() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-28.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Wilson OÜ digital stamp"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("12508548"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: *.sce file with TimeStamp
     *
     * Expected Result: The document should pass the validation
     *
     * File: ASICE_TS_LTA_content_as_sce.sce
     */
    @Test
    public void asiceWithSceFileExtensionShouldPass() {
        post(validationRequestFor("ASICE_TS_LTA_content_as_sce.sce"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LTA))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2014-10-30T18:50:35Z"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.notNullValue())
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.notNullValue())
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     *
     * Title: Asice-TS with special characters in data file
     *
     * Expected Result: The document should pass the validation with correct signature scope
     *
     * File: Nonconventionalcharacters.asice
     */
    @Test
    public void asiceWithSpecialCharactersInDataFileShouldPass() {
        post(validationRequestFor("Nonconventionalcharacters.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("!~#¤%%&()=+-_.txt"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is(SIGNATURE_SCOPE_FULL))
                .body("signatures[0].signatureScopes[0].content", Matchers.is(VALID_SIGNATURE_SCOPE_CONTENT_FULL))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-12
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Asice unsigned data files in the container
     * <p>
     * Expected Result: The document should pass the validation with warning
     * <p>
     * File: EE_SER-AEX-B-LT-V-34.asice
     */
    @Test
    public void asiceUnsignedDataFiles() {
        post(validationRequestFor("EE_SER-AEX-B-LT-V-34.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings[0].content", Matchers.is(CERTIFICATE_DO_NOT_MATCH_TRUST_SERVICE))
                .body("signatures[0].warnings[1].content", Matchers.is(ALL_FILES_NOT_SIGNED))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: Asice-ValidationPass-13
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: New Estonian ECC signature
     * <p>
     * Expected Result: The document should pass the validation
     * <p>
     * File: Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice
     */
    @Test
    public void asiceEccSignatureShouldPass() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice"))
                .then().root(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47101010033"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));

    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
