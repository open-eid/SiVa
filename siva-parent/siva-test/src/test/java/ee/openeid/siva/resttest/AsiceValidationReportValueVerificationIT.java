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

package ee.openeid.siva.resttest;

import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Category(IntegrationTest.class)

public class AsiceValidationReportValueVerificationIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface#validation-request-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: TwoValidTmSignaturesWithRolesAndProductionPlace.bdoc
     */
    @Test
    public void bdocCorrectValuesArePresentValidLtTmSignature() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("TwoValidTmSignaturesWithRolesAndProductionPlace.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("id-2d1a98a8173d01473aa7e88bc74b361a"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].signedBy", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("test.txt"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2020-05-29T08:19:25Z"))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIMebaUX4S1RLE7lcDJ0LxdLQQBgFsGxID+wzbAPvz8Ti"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2020-05-29T08:19:27Z"))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("Signing as king of signers"))
                .body("signatures[0].info.signerRole[1].claimedRole", Matchers.is("Second role"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Elbonia"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("Harju"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("Tallinn"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is("32323"))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2020-05-29T08:19:27Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIGHjCCBAagAwIBAgIQNcO4eO0xcsNbIk36aVrDqjANBgkqhk"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("TwoValidTmSignaturesWithRolesAndProductionPlace.bdoc"))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: validTsSignatureWithRolesAndProductionPlace.asice
     */
    @Test
    public void bdocCorrectValuesArePresentValidLtSignature() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("validTsSignatureWithRolesAndProductionPlace.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("id-7022c18f415891f9cb9124927ab14cfb"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].signedBy", Matchers.is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("test.txt"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Full document"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2020-05-29T09:34:56Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("The trusted certificate does not match the trust service!"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIB00XgQZ74rCQz13RlPDKtFVtGiUX01R5rTbhkZZKv0M"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2020-05-29T09:34:58Z"))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("First role"))
                .body("signatures[0].info.signerRole[1].claimedRole", Matchers.is("Second role"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Some country"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("ÕÄLnül23#&()"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("City with spaces"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is("123456789"))
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2020-05-29T09:35:00Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.is("2020-05-29T09:34:58Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID6jCCA02gAwIBAgIQR+qcVFxYF1pcSy/QGEnMVjAKBggqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName", Matchers.is("TEST of ESTEID2018"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content", Matchers.startsWith("MIIFfDCCBN2gAwIBAgIQNhjzSfd2UEpbkO14EY4ORTAKBggqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName", Matchers.is("DEMO of SK TSA 2014"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content", Matchers.startsWith("MIIEFTCCAv2gAwIBAgIQTqz7bCP8W45UBZa7tztTTDANBgkqhk"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("validTsSignatureWithRolesAndProductionPlace.asice"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     */
    @Test
    public void bdocCorrectValuesArePresentValidLtSignatureAdesWarning() {
        setTestFilesDirectory("bdoc/test/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc", VALID_SIGNATURE_POLICY_3))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.emptyOrNullString())
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("build.xml"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2014-07-11T14:10:07Z"))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEINHGGgGzXqzGfN2J6olA6VaXSeCG1PRBGrmG4wxQYf7A"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2011-10-15T14:59:35Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2011-10-15T14:59:35Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID3DCCAsSgAwIBAgIER/idhzANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT-TM, AdESqc
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: testAdesQCInvalid.asice
     */
    @Test
    public void bdocCorrectValuesArePresentInvalidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("testAdesQCInvalid.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("S1510667783001"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC"))
                .body("signatures[0].signedBy", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("signatures[0].errors[0].content", Matchers.is("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("test.pdf"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Full document"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2017-11-14T13:56:23Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("The private key does not reside in a QSCD at issuance time!"))
                .body("signatures[0].warnings[1].content", Matchers.is("The private key does not reside in a QSCD at (best) signing time!"))
                .body("signatures[0].warnings[2].content", Matchers.is("The signature/seal is not a valid AdES digital signature!"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.emptyOrNullString())
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2017-11-14T13:56:34Z"))
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2017-11-14T13:56:35Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.is("2017-11-14T13:56:34Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("NURM,AARE,PNOEE-38211015222"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIHkDCCBXigAwIBAgIQE2MaQOlx//NYkuLVlIRaIzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName", Matchers.is("EID-SK 2016"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content", Matchers.startsWith("MIIG4jCCBcqgAwIBAgIQO4A6a2nBKoxXxVAFMRvE2jANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content", Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("testAdesQCInvalid.asice"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (Bdoc valid multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: Baltic MoU digital signing_EST_LT_LV.bdoc
     */
    @Test
    public void bdocAllElementsArePresentValidMultipleSignatures() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Baltic MoU digital signing_EST_LT_LV.bdoc"));
        post(validationRequestWithValidKeys(encodedString, "Baltic MoU digital signing_EST_LT_LV.bdoc", VALID_SIGNATURE_POLICY_3))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].signedBy", Matchers.is("MICHAL,KRISTEN,37507120348"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("Baltic MoU digital signing_04112015.docx"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2015-11-04T10:24:11Z"))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEINiaR8aBDIPiXK/fiPb7fe3pWaBaEKzILvjnZVppopPy"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2015-11-04T10:24:20Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2015-11-04T10:24:20Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("MICHAL,KRISTEN,37507120348"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIEoTCCA4mgAwIBAgIQXESH+ckjJK1SC2r9DcQrDzANBgkqhk"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("Baltic MoU digital signing_EST_LT_LV.bdoc"))
                .body("validSignaturesCount", Matchers.is(3))
                .body("signaturesCount", Matchers.is(3));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (Bdoc indeterminate status)
     *
     * Expected Result: All required elements are present according to BdocDocSimpleReportSchema.json
     *
     * File: SS-4_teadmataCA.4.asice
     */
    @Ignore //TODO: needs investigation why the signature is determined as XAdES_BASELINE_T not as XAdES_BASELINE_LT_TM
    @Test
    public void bdocAllElementsArePresentIndeterminateSignature() {
        post(validationRequestFor("SS-4_teadmataCA.4.asice", VALID_SIGNATURE_POLICY_3, "simple"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_T"))
                .body("signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("signatures[0].signedBy", Matchers.is("signer1"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].errors[0].content", Matchers.is("The certificate path is not trusted!"))
                .body("signatures[0].errors[1].content", Matchers.is("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("test1.txt"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Full document"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2013-10-11T08:15:47Z"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.emptyOrNullString())
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("signer1"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIICHDCCAYWgAwIBAgIBAjANBgkqhkiG9w0BAQUFADAqMQswCQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName", Matchers.is("libdigidocpp Inter"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content", Matchers.startsWith("MIICCTCCAXKgAwIBAgIBAzANBgkqhkiG9w0BAQUFADAnMQswCQ"))
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("SS-4_teadmataCA.4.asice"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title:  Bdoc report with no signatures
     *
     * Expected Result: Report is returned with required elements
     *
     * File:BdocContainerNoSignature.bdoc
     */
    @Test
    public void bdocNoSignature() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("BdocContainerNoSignature.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures", Matchers.emptyOrNullString())
                .body("signatureForm", Matchers.is("ASiC-E"))
                .body("validatedDocument.filename", Matchers.is("BdocContainerNoSignature.bdoc"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Bdoc with LT_TM, LT & LTA signature - timeAssertionMessageImprints in mixed container are reported correctly
     *
     * Expected Result: timeAssertionMessageImprint values are present and meet the expected values.
     *
     * File: 3_signatures_TM_LT_LTA.bdoc
     */
    @Test
    public void bdocMixedSignaturesContainerCorrectTimeAssertionMessageImprint() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("3_signatures_TM_LT_LTA.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIGzgagluBCVuUgrnT6C5BmSAXBxuuxvlAN7epdGqHP0/"))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[1].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIBcwYgTTCv5dabbTMJENwex0W1UHxP2OnhiwIcDE89RE"))
                .body("signatures[2].signatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("signatures[2].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIOcfB5FibacEVizcnKhNisrfXU1QyXFzrVGjCQQdntiB"));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Asice with LT_TM, LT & LTA signature - timeAssertionMessageImprints in mixed container are reported correctly
     *
     * Expected Result: timeAssertionMessageImprint values are present and meet the expected values.
     *
     * File: 3_signatures_TM_LT_LTA.asice
     */

    @Test
    public void asiceMixedSignaturesContainerCorrectTimeAssertionMessageImprint() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("3_signatures_TM_LT_LTA.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIGzgagluBCVuUgrnT6C5BmSAXBxuuxvlAN7epdGqHP0/"))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[1].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIBcwYgTTCv5dabbTMJENwex0W1UHxP2OnhiwIcDE89RE"))
                .body("signatures[2].signatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("signatures[2].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIOcfB5FibacEVizcnKhNisrfXU1QyXFzrVGjCQQdntiB"));

    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Bdoc with B & LT_TM mixed signatures - ocspResponseCreationTimes in mixed container are reported correctly
     *
     * Expected Result: ocspResponseCreationTime values are present and meet the expected values.
     *
     * File: 3_signatures_TM_LT_LTA.asice
     */
    @Ignore("SIVA-365")
    @Test
    public void asiceMixedSignaturesSameCertificateContainerCorrectOcspResponseCreationTime() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("3_signatures_TM_LT_LTA.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2021-01-29T14:15:43Z"))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[1].info.ocspResponseCreationTime", Matchers.is("2021-01-29T14:31:37Z"))
                .body("signatures[2].signatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("signatures[2].info.ocspResponseCreationTime", Matchers.is("2021-01-29T14:38:11Z"));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Bdoc with LT_TM, LT & LTA signature, LT & LTA with same certificate - ocspResponseCreationTimes in mixed container are reported correctly
     *
     * Expected Result: ocspResponseCreationTime values are present and meet the expected values.
     *
     * File: 3_signatures_TM_LT_LTA.bdoc
     */
    @Test
    public void bdocMixedSignaturesSameCertificateContainerCorrectOcspResponseCreationTime() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("3_signatures_TM_LT_LTA.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2021-01-29T14:15:43Z"))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[1].info.ocspResponseCreationTime", Matchers.is("2021-01-29T14:31:37Z"))
                .body("signatures[2].signatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("signatures[2].info.ocspResponseCreationTime", Matchers.is("2021-01-29T14:38:11Z"));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Asice with LT & T mixed signatures - ocspResponseCreationTimes in mixed container are reported correctly
     *
     * Expected Result: ocspResponseCreationTime values are present and meet the expected values.
     *
     * File: 2_signatures_T_LT.asice
     */
    @Test
    public void asiceMixedSignaturesContainerCorrectOcspResponseCreationTime() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestFor("2_signatures_T_LT.asice"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_T"))
                .body("signatures[0].info", Matchers.not(Matchers.hasKey("ocspResponseCreationTime")))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[1].info.ocspResponseCreationTime", Matchers.is("2022-08-25T09:05:10Z"));
    }

    /**
     * TestCaseID: Bdoc-ValidationReportVerification-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Bdoc with LT-TM & B mixed signatures - ocspResponseCreationTimes in mixed container are reported correctly
     *
     * Expected Result: ocspResponseCreationTime values are present and meet the expected values.
     *
     * File: 2_signatures_B_TM.bdoc
     */
    @Test
    public void bdocMixedSignaturesContainerCorrectOcspResponseCreationTime() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestFor("2_signatures_B_TM.bdoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_EPES"))
                .body("signatures[0].info", Matchers.not(Matchers.hasKey("ocspResponseCreationTime")))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[1].info.ocspResponseCreationTime", Matchers.is("2022-08-25T12:22:37Z"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
