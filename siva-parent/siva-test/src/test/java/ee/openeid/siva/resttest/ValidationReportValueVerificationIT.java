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

package ee.openeid.siva.resttest;

import ee.openeid.siva.common.Constants;
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

public class ValidationReportValueVerificationIT extends SiVaRestTests {

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
     * TestCaseID: Pdf-ValidationReportVerification-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (Pdf valid single signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: reason_and_location_Test.pdf
     */
    @Test
    public void pdfAllElementsArePresentValidSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("reason_and_location_Test.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("S-4D0D5A83688FC617AA83810ED74E26C5A79063D110B00AD207EAB3EFDC3F5619"))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].signedBy", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("11404176865"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("Partial PDF"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("PARTIAL"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("The document ByteRange : [0, 2226, 21172, 314]"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2020-05-27T09:59:07Z"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIDqs93c5A/EZVW0YfLVkSS3NeO716K6Kb0Mcr/ewLCmA"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2020-05-27T09:59:09Z"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Narva"))
                .body("signatures[0].info.signingReason", Matchers.is("Roll??"))
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2020-05-27T09:59:10Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.is("2020-05-27T09:59:09Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIFrjCCA5agAwIBAgIQUwvkG7xZfERXDit8E7z6DDANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName", Matchers.is("TEST of ESTEID-SK 2015"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content", Matchers.startsWith("MIIGgzCCBWugAwIBAgIQEDb9gCZi4PdWc7IoNVIbsTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName", Matchers.is("DEMO of SK TSA 2014"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content", Matchers.startsWith("MIIEFTCCAv2gAwIBAgIQTqz7bCP8W45UBZa7tztTTDANBgkqhk"))
                .body("signatureForm", Matchers.emptyOrNullString())
                .body("validatedDocument.filename", Matchers.is("reason_and_location_Test.pdf"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-ValidationReportVerification-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (Pdf valid Multiple signatures)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: pades_lt_two_valid_sig.pdf
     */
    @Test
    public void pdfAllElementsArePresentValidmultipleSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("pades_lt_two_valid_sig.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[1].id", Matchers.is("S-E5D6D118C4B604343E1395213075D5C429CD68E9178E4E8252EDB027732EF3F6"))
                .body("signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[1].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"))
                .body("signatures[1].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[1].signedBy", Matchers.is("VOLL,ANDRES,39004170346"))
                .body("signatures[1].subjectDistinguishedName.commonName", Matchers.is("VOLL,ANDRES,39004170346"))
                .body("signatures[1].subjectDistinguishedName.serialNumber", Matchers.is("39004170346"))
                .body("signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[1].errors", Matchers.emptyOrNullString())
                .body("signatures[1].signatureScopes[0].name", Matchers.is("Partial PDF"))
                .body("signatures[1].signatureScopes[0].scope", Matchers.is("PARTIAL"))
                .body("signatures[1].signatureScopes[0].content", Matchers.is("The document ByteRange : [0, 134940, 153886, 24208]"))
                .body("signatures[1].claimedSigningTime", Matchers.is("2016-06-27T09:59:37Z"))
                .body("signatures[1].warnings", Matchers.emptyOrNullString())
                .body("signatures[1].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIOjVGatd9zXaIv/XQ9c81bTjZ4K14Ihcrhwv+sBhM26V"))
                .body("signatures[1].info.bestSignatureTime", Matchers.is("2016-06-27T09:59:48Z"))
                .body("signatures[1].info.ocspResponseCreationTime", Matchers.is("2016-06-27T09:59:49Z"))
                .body("signatures[1].info.timestampCreationTime", Matchers.is("2016-06-27T09:59:48Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("TEST of SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIEmDCCA4CgAwIBAgIQP0r+1SmYLpVSgfYqBWYcBzANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName", Matchers.is("ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content", Matchers.startsWith("MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content", Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatureForm", Matchers.emptyOrNullString())
                .body("validatedDocument.filename", Matchers.is("pades_lt_two_valid_sig.pdf"))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Pdf-ValidationReportVerification-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (Pdf invalid signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void pdfAllElementsArePresentInvalidSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("hellopades-lt-b.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[1].id", Matchers.is("S-9D2DD421E47AE2C851EBD4C467DA97042362BB48331511E272B64110CFF862EE"))
                .body("signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("signatures[1].signatureLevel", Matchers.is("NOT_ADES"))
                .body("signatures[1].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].subIndication", Matchers.is("FORMAT_FAILURE"))
                .body("signatures[1].errors[0].content", Matchers.is("The certificate is not related to a granted status!"))
                .body("signatures[1].signatureScopes[0].name", Matchers.is("Full PDF"))
                .body("signatures[1].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("signatures[1].signatureScopes[0].content", Matchers.is("Full document"))
                .body("signatures[1].claimedSigningTime", Matchers.is("2015-08-23T05:10:15Z"))
                .body("signatures[1].warnings[0].content", Matchers.is("The signature/seal is not a valid AdES digital signature!"))
                .body("signatures[1].info.timeAssertionMessageImprint", Matchers.emptyOrNullString())
                .body("signatures[1].info.bestSignatureTime", Matchers.emptyOrNullString())
                .body("signatures[1].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[1].info.ocspResponseCreationTime", Matchers.is("2015-08-23T05:08:41Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIEnTCCA4WgAwIBAgIQURtcmP07BjlUmR1RPIeGCTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName", Matchers.is("ESTEID-SK 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content", Matchers.startsWith("MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content", Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatureForm", Matchers.emptyOrNullString())
                .body("validatedDocument.filename", Matchers.is("hellopades-lt-b.pdf"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Pdf-ValidationReportVerification-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (Pdf indeterminate status)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: hellopades-lt-rsa1024-sha1-expired.pdf
     */
    @Test
    public void pdfAllElementsArePresentIndeterminateSignature() {
        setTestFilesDirectory("pdf/signing_certifacte_test_files/");
        post(validationRequestFor("hellopades-lt-rsa1024-sha1-expired.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].id", Matchers.is("S-B2DE2D1E57C3DD8F518A13F027988A4BDBE03DC7A1DF96301351694DCDB88213"))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].errors.content", Matchers.hasItem("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("Partial PDF"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("PARTIAL"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("The document ByteRange : [0, 14153, 52047, 491]"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-01-24T11:08:15Z"))
                .body("signatures[0].warnings", Matchers.hasSize(1))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("MDEwDQYJYIZIAWUDBAIBBQAEIFx5F/YSDew7evstDVhsdXKaN1B3k/wDBgLOOs1YFdJr"))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2015-08-24T10:08:25Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.is("2015-08-24T10:08:25Z"))
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID3DCCAsSgAwIBAgIER/idhzANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.commonName", Matchers.is("ESTEID-SK 2007"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].issuer.content", Matchers.startsWith("MIID0zCCArugAwIBAgIERZugDTANBgkqhkiG9w0BAQUFADBdMR"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].commonName", Matchers.is("SK TIMESTAMPING AUTHORITY"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNATURE_TIMESTAMP'}[0].content", Matchers.startsWith("MIIEDTCCAvWgAwIBAgIQJK/s6xJo0AJUF/eG7W8BWTANBgkqhk"))
                .body("signatureForm", Matchers.emptyOrNullString())
                .body("validatedDocument.filename", Matchers.is("hellopades-lt-rsa1024-sha1-expired.pdf"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Pdf-ValidationReportVerification-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title:  Pdf report with no signatures
     *
     * Expected Result: Report is returned with required elements
     *
     * File: PdfNoSignature.pdf
     */
    @Test
    public void pdfNoSignature() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("PdfNoSignature.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures", Matchers.emptyOrNullString())
                .body("signatureForm", Matchers.emptyOrNullString())
                .body("validatedDocument.filename", Matchers.is("PdfNoSignature.pdf"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (ddoc valid single signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: DIGIDOC-XML1.3.ddoc
     */
    @Test
    public void ddocAllElementsArePresentValidSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("DIGIDOC-XML1.3.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].signatureLevel", Matchers.emptyOrNullString())
                .body("signatures[0].signedBy", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("signatures[0].subjectDistinguishedName.commonName", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47710110274"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("Glitter-rock-4_gallery.jpg"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("gUCY28PU17SPGDVisO/fc6BEO8E="))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIEOjCCAyKgAwIBAgIQemG0FEa+2axOwPpfWTLyszANBgkqhk"))
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3.ddoc"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: JSON structure has all elements (ddoc invalid signature)
     *
     * Expected Result: All required elements are present according to SimpleReportSchema.json
     *
     * File: multipleInvalidSignatures.ddoc
     */
    @Test
    public void ddocAllElementsArePresentInvalidSignature() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("multipleInvalidSignatures.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.1"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].signatureLevel", Matchers.emptyOrNullString())
                .body("signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].subIndication", Matchers.emptyOrNullString())
                .body("signatures[0].errors", Matchers.hasSize(2))
                .body("signatures[0].signatureScopes[0].name", Matchers.is("DigiDocService_spec_1_110_est.pdf"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:42:19Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("hOU1VZPsg2F65+E9z1gQ0PZ0Gvo="))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("harju"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("tallinn"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2009-06-01T10:42:25Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("ESTEID-SK 2007 OCSP RESPONDER"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIDnDCCAoSgAwIBAgIERZ0acjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID3zCCAsegAwIBAgIER4JChjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.1"))
                .body("validatedDocument.filename", Matchers.is("multipleInvalidSignatures.ddoc"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(3));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Check for optional warning element
     *
     * Expected Result: Warning element is present
     *
     * File: 18912.ddoc
     */
    @Test
    public void ddocOptionalWarningElementIsPresent() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("18912.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("readme"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2012-09-21T11:56:53Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Bad digest for DataFile: D0 alternate digest matches!"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("f4HUThRsYgGtO+CRH5cHA7mN/48="))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2012-09-21T11:56:55Z"))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2012-09-21T11:56:55Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("SK OCSP RESPONDER 2011"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIEvDCCA6SgAwIBAgIQcpyVmdruRVxNgzI3N/NZQTANBgkqhk"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIIEuTCCA6GgAwIBAgIQZ+e7WiJWyzFPH8axcYYMdzANBgkqhk"))
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validatedDocument.filename", Matchers.is("18912.ddoc"))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title:  Ddoc report with no signatures
     *
     * Expected Result: Report is returned with required elements
     *
     * File: DdocContainerNoSignature.ddoc
     */
    @Test
    public void ddocNoSignature() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("DdocContainerNoSignature.ddoc", VALID_SIGNATURE_POLICY_4, null))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validatedDocument.filename", Matchers.is("DdocContainerNoSignature.ddoc"))
                .body("validationWarnings[0].content", Matchers.is(Constants.TEST_ENV_VALIDATION_WARNING))
                .body("validationWarnings[1].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(0));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.0, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: SK-XML1.0.ddoc
     */
    @Test
    public void ddocCorrectValuesArePresentV1_0() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("SK-XML1.0.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("SK_XML_1.0"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].signedBy", Matchers.is("ANSIP,ANDRUS,35610012722"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[0].name", Matchers.is("Tartu ja Tallinna koostooleping.doc"))
                .body("signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2002-10-07T12:10:19Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: SK-XML version: 1.0"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("+zJk5eEWr1O5QozRwTBxOHtXGWE="))
                .body("signatures[0].info.bestSignatureTime", Matchers.is("2002-10-07T11:10:47Z"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Eesti"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.emptyOrNullString())
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("Tallinn"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.emptyOrNullString())
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2002-10-07T11:10:47Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("ESTEID-SK OCSP RESPONDER"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIDuDCCAqCgAwIBAgIEPJilyDANBgkqhkiG9w0BAQUFADB8MR"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("ANSIP,ANDRUS,35610012722"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID9zCCAt+gAwIBAgIEPZwyDDANBgkqhkiG9w0BAQUFADB8MR"))
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.0"))
                .body("validatedDocument.filename", Matchers.is("SK-XML1.0.ddoc"))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.1, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.1.ddoc
     */
    @Test
    public void ddocCorrectValuesArePresentV1_1() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.1.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.1"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[1].name", Matchers.is("Testilood20070320.doc"))
                .body("signatures[0].signatureScopes[1].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[1].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:42:19Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("hOU1VZPsg2F65+E9z1gQ0PZ0Gvo="))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("harju"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("tallinn"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2009-06-01T10:42:25Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("ESTEID-SK 2007 OCSP RESPONDER"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIDnDCCAoSgAwIBAgIERZ0acjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID3zCCAsegAwIBAgIER4JChjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.1"))
                .body("validatedDocument.filename", Matchers.is("igasugust1.1.ddoc"))
                .body("validSignaturesCount", Matchers.is(3))
                .body("signaturesCount", Matchers.is(3));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.2, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.2.ddoc
     */
    @Test
    public void ddocCorrectValuesArePresentV1_2() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.2.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.2"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[1].name", Matchers.is("Testilood20070320.doc"))
                .body("signatures[0].signatureScopes[1].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[1].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:45:44Z"))
                .body("signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.2"))
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("dnu5mnLqdxKw7QCRT96oshnmMSA="))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("harju"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("otepää"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2009-06-01T10:45:49Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("ESTEID-SK 2007 OCSP RESPONDER"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIDnDCCAoSgAwIBAgIERZ0acjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID3zCCAsegAwIBAgIER4JChjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.2"))
                .body("validatedDocument.filename", Matchers.is("igasugust1.2.ddoc"))
                .body("validSignaturesCount", Matchers.is(3))
                .body("signaturesCount", Matchers.is(3));
    }

    /**
     * TestCaseID: Ddoc-ValidationReportVerification-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, xml v1.3, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: Igasugust1.3.ddoc
     */
    @Test
    public void ddocCorrectValuesArePresentV1_3() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestFor("igasugust1.3.ddoc"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("signatures[0].id", Matchers.is("S0"))
                .body("signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].errors", Matchers.emptyOrNullString())
                .body("signatures[0].signatureScopes[1].name", Matchers.is("Testilood20070320.doc"))
                .body("signatures[0].signatureScopes[1].scope", Matchers.is("FullSignatureScope"))
                .body("signatures[0].signatureScopes[1].content", Matchers.is("Digest of the document content"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:46:37Z"))
                .body("signatures[0].warnings", Matchers.emptyOrNullString())
                .body("signatures[0].info.timeAssertionMessageImprint", Matchers.is("k5Q9iUHY8M0EJFjaH9h1eWyRgL8="))
                .body("signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("ei tea"))
                .body("signatures[0].info.signatureProductionPlace.city", Matchers.is("tõrva"))
                .body("signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("signatures[0].info.timestampCreationTime", Matchers.emptyOrNullString())
                .body("signatures[0].info.ocspResponseCreationTime", Matchers.is("2009-06-01T10:46:42Z"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].commonName", Matchers.is("ESTEID-SK 2007 OCSP RESPONDER"))
                .body("signatures[0].certificates.findAll{it.type == 'REVOCATION'}[0].content", Matchers.startsWith("MIIDnDCCAoSgAwIBAgIERZ0acjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].commonName", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("signatures[0].certificates.findAll{it.type == 'SIGNING'}[0].content", Matchers.startsWith("MIID3zCCAsegAwIBAgIER4JChjANBgkqhkiG9w0BAQUFADBbMQ"))
                .body("signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validatedDocument.filename", Matchers.is("igasugust1.3.ddoc"))
                .body("validSignaturesCount", Matchers.is(3))
                .body("signaturesCount", Matchers.is(3));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
