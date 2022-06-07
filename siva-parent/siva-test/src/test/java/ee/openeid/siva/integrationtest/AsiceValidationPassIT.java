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

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.*;

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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-10-11T09:36:10Z"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("signatures[0].signedBy", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].certificates.size()", Matchers.is(3))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIE3DCCAsSgAwIBAgIQSsqdjzAQgvpX80krgJy83DANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("ESTEID-SK 2015"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIGcDCCBVigAwIBAgIQRUgJC4ec7yFWcqzT3mwbWzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
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
     * Title: Asice with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: BDOC-TS.asice
     */
    @Test
    public void validAsiceMultipleSignatures() {
        post(validationRequestFor("BDOC-TS.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].signedBy", Matchers.is("PELANIS,MINDAUGAS,37412260478"))
                .body("signatures[0].subjectDistinguishedName.commonName",  Matchers.is("MINDAUGAS PELANIS"))
                .body("signatures[0].subjectDistinguishedName.serialNumber",  Matchers.is("37412260478"))
                .body("signatures[0].certificates.size()", Matchers.is(3))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("MINDAUGAS PELANIS"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIGJzCCBQ+gAwIBAgIObV8h37aTlaYAAQAEAckwDQYJKoZIhv"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("VI Registru Centras RCSC (IssuingCA-A)"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIF7jCCBNagAwIBAgIOEvrAfT5Zs1YAAwAAABkwDQYJKoZIhv"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2016-05-23T10:06:23Z"))
                .body("signatures[0].signedBy", Matchers.is("UUKKIVI,KRISTI,48505280278"))
                .body("signatures[0].certificates.size()", Matchers.is(3))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("UUKKIVI,KRISTI,48505280278"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEojCCA4qgAwIBAgIQPKphkF8jscxRrFRhBsxlhjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
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
    @Ignore("DD4J-615")
    public void asiceBaselineLtaProfileValidSignature() {
        post(validationRequestFor("EE_SER-AEX-B-LTA-V-24.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LTA))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2014-10-30T18:50:35Z"))
                .body("signatures[0].signedBy", Matchers.is("METSMA,RAUL,38207162766"))
                .body("signatures[0].certificates.size()", Matchers.is(4))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("METSMA,RAUL,38207162766"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEmzCCA4OgAwIBAgIQFQe7NKtE06tRSY1vHfPijjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("BalTstamp QTSA TSU2"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEtzCCA5+gAwIBAgIKFg5NNQAAAAADhzANBgkqhkiG9w0BAQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].issuer.commonName",  Matchers.startsWith("SSC Qualified Class 3 CA"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].issuer.content",  Matchers.startsWith("MIIFvTCCA6WgAwIBAgIQWJFmnMAIyiVAcLMn/5wGnjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].commonName",  Matchers.is("BalTstamp QTSA TSU2"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEtzCCA5+gAwIBAgIKFg5NNQAAAAADhzANBgkqhkiG9w0BAQ"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].issuer.commonName",  Matchers.startsWith("SSC Qualified Class 3 CA"))
                .body("signatures[0].certificates.findAll{it.type == 'ARCHIVE_TIMESTAMP'}[0].issuer.content",  Matchers.startsWith("MIIFvTCCA6WgAwIBAgIQWJFmnMAIyiVAcLMn/5wGnjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].signedBy", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("signatures[0].certificates.size()", Matchers.is(3))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("LUKIN,LIISA,47710110274"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIFfzCCA2egAwIBAgIQL+hzDhb7R0xWi+03fxcZKDANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("ESTEID-SK 2015"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIGcDCCBVigAwIBAgIQRUgJC4ec7yFWcqzT3mwbWzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].signedBy", Matchers.is("Wilson OÜ digital stamp"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("Wilson OÜ digital stamp"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("12508548"))
                .body("signatures[0].certificates.size()", Matchers.is(3))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("Wilson OÜ digital stamp"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIEcDCCA1igAwIBAgIQBCCW1H7A4/xUfYW+dTWZgzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName",  Matchers.startsWith("KLASS3-SK 2010"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content",  Matchers.startsWith("MIIErDCCA5SgAwIBAgIQAznVp1LayatNgy6bN8f9QjANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("KLASS3-SK 2010 OCSP RESPONDER"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIELzCCAxegAwIBAgICAMswDQYJKoZIhvcNAQEFBQAwbTELMA"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].issuer.commonName",  Matchers.startsWith("KLASS3-SK 2010"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].issuer.content",  Matchers.startsWith("MIIErDCCA5SgAwIBAgIQAznVp1LayatNgy6bN8f9QjANBgkqhk"))
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
    @Ignore("DD4J-615")
    public void asiceWithSceFileExtensionShouldPass() {
        post(validationRequestFor("ASICE_TS_LTA_content_as_sce.sce"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
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
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].signedBy", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47101010033"))
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
     * Title: Asice pss signature
     * <p>
     * Expected Result: The document should pass the validation
     * <p>
     * File: PSS-signature.asice
     */
    @Test
    public void asicePssSignatureShouldPass() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("PSS-signature.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1"))
                .body("signatures[0].signedBy", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("11404176865"))
                .body("validationLevel", Matchers.is(VALIDATION_LEVEL_ARCHIVAL_DATA))
                .body("validSignaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: Asice-ValidationPass-14
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: Asice with empty datafiles
     * <p>
     * Expected Result: The document should pass the validation but with warnings about empty datafiles
     * <p>
     * File: signed-container-with-empty-datafiles.asice
     */
    @Test
    public void asiceWithEmptyDataFilesShouldPass() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("signed-container-with-empty-datafiles.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].signatureScopes.size()", Matchers.is(5))
                .body("signatures[0].signatureScopes.name", Matchers.containsInRelativeOrder(
                        "data-file-1.txt", "empty-file-2.txt", "data-file-3.txt", "empty-file-4.txt", "data-file-5.txt"
                ))
                .body("signatures[0].warnings.size()", Matchers.is(3))
                .body("signatures[0].warnings.content", Matchers.containsInAnyOrder(
                        "The trusted certificate does not match the trust service!",
                        "Data file 'empty-file-2.txt' is empty",
                        "Data file 'empty-file-4.txt' is empty"
                ))
                .body("signaturesCount", Matchers.is(1))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Asice-ValidationPass-15
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#common_POLv3_POLv4
     * <p>
     * Title: ASICE with new Smart-ID certificate profile without personal number in CommonName
     * <p>
     * Expected Result: The document should pass
     * <p>
     * File: validSidSignatureWithCertWithoutPnoInCn.asice
     */

    @Test
    public void validSignatureSignerCertDoNotHavePersonalNumberInCnShouldPass() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("validSidSignatureWithCertWithoutPnoInCn.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is(SIGNATURE_FORM_ASICE))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[0].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[0].signedBy", Matchers.is("TESTNUMBER,QUALIFIED OK1,30303039914"))
                .body("signatures[0].subjectDistinguishedName.commonName",  Matchers.is("TESTNUMBER,QUALIFIED OK1"))
                .body("signatures[0].subjectDistinguishedName.serialNumber",  Matchers.is("PNOEE-30303039914"))
                .body("signatures[1].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT))
                .body("signatures[1].indication", Matchers.is(TOTAL_PASSED))
                .body("signatures[1].signedBy", Matchers.is("TESTNUMBER,BOD,39912319997"))
                .body("signatures[1].subjectDistinguishedName.commonName",  Matchers.is("TESTNUMBER,BOD"))
                .body("signatures[1].subjectDistinguishedName.serialNumber",  Matchers.is("PNOEE-39912319997"))
                .body("signatures[1].certificates.size()", Matchers.is(3))
                .body("signatures[1].certificates.findAll{it.type == 'SIGNING'}[0].commonName",  Matchers.is("TESTNUMBER,BOD"))
                .body("signatures[1].certificates.findAll{it.type == 'SIGNING'}[0].content",  Matchers.startsWith("MIIIojCCBoqgAwIBAgIQJ5zu8nauSO5hSFPXGPNAtzANBgkqhk"))
                .body("signatures[1].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName",  Matchers.is("DEMO SK TIMESTAMPING AUTHORITY 2020"))
                .body("signatures[1].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content",  Matchers.startsWith("MIIEgzCCA2ugAwIBAgIQcGzJsYR4QLlft+S73s/WfTANBgkqhk"))
                .body("signatures[1].certificates.findAll{it.type == 'REVOCATION'}[0].commonName",  Matchers.is("DEMO of EID-SK 2016 AIA OCSP RESPONDER 2018"))
                .body("signatures[1].certificates.findAll{it.type == 'REVOCATION'}[0].content",  Matchers.startsWith("MIIFQDCCAyigAwIBAgIQSKlAnTgs72Ra5xCvMScb/jANBgkqhk"))
                .body("signaturesCount", Matchers.is(2))
                .body("validSignaturesCount", Matchers.is(2));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
