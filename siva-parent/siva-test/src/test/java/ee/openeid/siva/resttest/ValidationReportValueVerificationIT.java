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

import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_FORMAT_XADES_LT_TM;
import static ee.openeid.siva.integrationtest.TestData.SIGNATURE_LEVEL_QESIG;
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("id-2d1a98a8173d01473aa7e88bc74b361a"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("test.txt"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2020-05-29T08:19:25Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2020-05-29T08:19:27Z"))
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[0].claimedRole", Matchers.is("Signing as king of signers"))
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[1].claimedRole", Matchers.is("Second role"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Elbonia"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("Harju"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.city", Matchers.is("Tallinn"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.postalCode", Matchers.is("32323"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("TwoValidTmSignaturesWithRolesAndProductionPlace.bdoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("id-7022c18f415891f9cb9124927ab14cfb"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("test.txt"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Full document"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2020-05-29T09:34:56Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The trusted certificate doesn't match the trust service"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2020-05-29T09:34:58Z"))
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[0].claimedRole", Matchers.is("First role"))
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[1].claimedRole", Matchers.is("Second role"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Some country"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("ÕÄLnül23#&()"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.city", Matchers.is("City with spaces"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.postalCode", Matchers.is("123456789"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("validTsSignatureWithRolesAndProductionPlace.asice"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is(SIGNATURE_FORMAT_XADES_LT_TM))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("build.xml"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2014-07-11T14:10:07Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2011-10-15T14:59:35Z"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S1510667783001"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("NURM,AARE,PNOEE-38211015222"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("HASH_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("test.pdf"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Full document"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2017-11-14T13:56:23Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The trusted certificate doesn't match the trust service"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The private key is not on a QSCD at issuance time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[2].content", Matchers.is("The private key is not on a QSCD at (best) signing time!"))
                .body("validationReport.validationConclusion.signatures[0].warnings[3].content", Matchers.is("The signature/seal is not a valid AdES!"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2017-11-14T13:56:34Z"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("testAdesQCInvalid.asice"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("MICHAL,KRISTEN,37507120348"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("Baltic MoU digital signing_04112015.docx"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2015-11-04T10:24:11Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2015-11-04T10:24:20Z"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("Baltic MoU digital signing_EST_LT_LV.bdoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(3))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(3));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_T"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is(SIGNATURE_LEVEL_QESIG))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("signer1"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The certificate path is not trusted!"))
                .body("validationReport.validationConclusion.signatures[0].errors[1].content", Matchers.is("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("test1.txt"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Full document"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2013-10-11T08:15:47Z"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("SS-4_teadmataCA.4.asice"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("BdocContainerNoSignature.bdoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(0));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S-24C246774A122D4077DF298CF6F7DC9475AC0E272E0A7FFC2C1285DCC83F312C"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("11404176865"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("Partial PDF"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("PARTIAL"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("The document byte range: [0, 2226, 21172, 314]"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2020-05-27T09:59:07Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2020-05-27T09:59:09Z"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Narva"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("reason_and_location_Test.pdf"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[1].id", Matchers.is("S-4E325FC1EC28358B6A2292007728F5B100657069464B81A5A3E719DA852B5B61"))
                .body("validationReport.validationConclusion.signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[1].signatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"))
                .body("validationReport.validationConclusion.signatures[1].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[1].signedBy", Matchers.is("VOLL,ANDRES,39004170346"))
                .body("validationReport.validationConclusion.signatures[1].subjectDistinguishedName.commonName", Matchers.is("VOLL,ANDRES,39004170346"))
                .body("validationReport.validationConclusion.signatures[1].subjectDistinguishedName.serialNumber", Matchers.is("39004170346"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[1].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[1].signatureScopes[0].name", Matchers.is("Partial PDF"))
                .body("validationReport.validationConclusion.signatures[1].signatureScopes[0].scope", Matchers.is("PARTIAL"))
                .body("validationReport.validationConclusion.signatures[1].signatureScopes[0].content", Matchers.is("The document byte range: [0, 134940, 153886, 24208]"))
                .body("validationReport.validationConclusion.signatures[1].claimedSigningTime", Matchers.is("2016-06-27T09:59:37Z"))
                .body("validationReport.validationConclusion.signatures[1].warnings[0].content", Matchers.is("The trusted certificate doesn't match the trust service"))
                .body("validationReport.validationConclusion.signatures[1].info.bestSignatureTime", Matchers.is("2016-06-27T09:59:48Z"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("pades_lt_two_valid_sig.pdf"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[1].id", Matchers.is("S-664A6BD08A60EF4F3DBEE46F0B0FD880997772A13F00FED58698DD329EF25F54"))
                .body("validationReport.validationConclusion.signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("validationReport.validationConclusion.signatures[1].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[1].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("validationReport.validationConclusion.signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[1].subIndication", Matchers.is("FORMAT_FAILURE"))
                .body("validationReport.validationConclusion.signatures[1].errors[0].content", Matchers.is("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[1].signatureScopes[0].name", Matchers.is("Full PDF"))
                .body("validationReport.validationConclusion.signatures[1].signatureScopes[0].scope", Matchers.is("FULL"))
                .body("validationReport.validationConclusion.signatures[1].signatureScopes[0].content", Matchers.is("Full document"))
                .body("validationReport.validationConclusion.signatures[1].claimedSigningTime", Matchers.is("2015-08-23T05:10:15Z"))
                .body("validationReport.validationConclusion.signatures[1].warnings[0].content", Matchers.is("The trusted certificate doesn't match the trust service"))
                .body("validationReport.validationConclusion.signatures[1].warnings[1].content", Matchers.is("The signature/seal is not a valid AdES!"))

                .body("validationReport.validationConclusion.signatures[1].info.bestSignatureTime", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("hellopades-lt-b.pdf"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S-9C94C6D367AA447A45CE12C6B4BA78EF6714642D1E8D8D01B69E2A1888D2454B"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("The result of the LTV validation process is not acceptable to continue the process!"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("PDF previous version #1"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("PARTIAL"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("The document byte range: [0, 14153, 52047, 491]"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-01-24T11:08:15Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2015-08-24T10:08:25Z"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("hellopades-lt-rsa1024-sha1-expired.pdf"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("validationReport.validationConclusion.signatures", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("PdfNoSignature.pdf"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(0));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.is("LUKIN,LIISA,47710110274"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.is("47710110274"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("Glitter-rock-4_gallery.jpg"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-10-03T07:46:31Z"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2012-10-03T07:46:51Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("DIGIDOC-XML1.3.ddoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.1"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.hasSize(2))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("DigiDocService_spec_1_110_est.pdf"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:42:19Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("harju"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.city", Matchers.is("tallinn"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.1"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("multipleInvalidSignatures.ddoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(3));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("readme"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2012-09-21T11:56:53Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("Bad digest for DataFile: D0 alternate digest matches!"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2012-09-21T11:56:55Z"))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("18912.ddoc"))
                .body("validationReport.validationConclusion.validationWarnings[0].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
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
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("DdocContainerNoSignature.ddoc"))
                .body("validationReport.validationConclusion.validationWarnings[0].content", Matchers.is("Please add Time-Stamp to the file for long term DDOC validation. This can be done with Time-Stamping application TeRa"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(0));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("SK_XML_1.0"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("ANSIP,ANDRUS,35610012722"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("Tartu ja Tallinna koostooleping.doc"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2002-10-07T12:10:19Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: SK-XML version: 1.0"))
                .body("validationReport.validationConclusion.signatures[0].info.bestSignatureTime", Matchers.is("2002-10-07T11:10:47Z"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("Eesti"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.city", Matchers.is("Tallinn"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.postalCode", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.0"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("SK-XML1.0.ddoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(2));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.1"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].name", Matchers.is("Testilood20070320.doc"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:42:19Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("harju"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.city", Matchers.is("tallinn"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.1"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("igasugust1.1.ddoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(3))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(3));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.2"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].name", Matchers.is("Testilood20070320.doc"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:45:44Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.2"))
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("harju"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.city", Matchers.is("otepää"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.2"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("igasugust1.2.ddoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(3))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(3));
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
                .then()
                .body(matchesJsonSchemaInClasspath("SimpleReportSchemaDdoc.json"))
                .body("validationReport.validationConclusion.signatures[0].id", Matchers.is("S0"))
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.signatures[0].signatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].errors", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].name", Matchers.is("Testilood20070320.doc"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].scope", Matchers.is("FullSignatureScope"))
                .body("validationReport.validationConclusion.signatures[0].signatureScopes[1].content", Matchers.is("Digest of the document content"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2009-06-01T10:46:37Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].info.signerRole[0].claimedRole", Matchers.is("Test"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.countryName", Matchers.is("eesti"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.stateOrProvince", Matchers.is("ei tea"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.city", Matchers.is("tõrva"))
                .body("validationReport.validationConclusion.signatures[0].info.signatureProductionPlace.postalCode", Matchers.is(" "))
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("igasugust1.3.ddoc"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(3))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(3));
    }


    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
