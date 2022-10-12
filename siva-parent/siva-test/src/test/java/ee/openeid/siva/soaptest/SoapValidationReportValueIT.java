/*
 * Copyright 2019 - 2022 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.soaptest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.SOAP_VALIDATION_CONCLUSION_PREFIX;

@Category(IntegrationTest.class)
public class SoapValidationReportValueIT extends SiVaSoapTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timestamp/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: TwoValidTmSignaturesWithRolesAndProductionPlace.bdoc
     *
     */
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtTmSignature() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestForDocument("TwoValidTmSignaturesWithRolesAndProductionPlace.bdoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("2"))
                .body("ValidSignaturesCount", Matchers.is("2"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.CommonName", Matchers.is("MÄNNIK,MARI-LIIS,47101010033"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.SerialNumber", Matchers.is("47101010033"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.GivenName", Matchers.is("MARI-LIIS"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.Surname", Matchers.is("MÄNNIK"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Name", Matchers.is("test.txt"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Content", Matchers.is("Digest of the document content"))
                .body("Signatures.Signature[0].ClaimedSigningTime", Matchers.is("2020-05-29T08:19:25Z"))
                .body("Signatures.Signature[0].Info.BestSignatureTime", Matchers.is("2020-05-29T08:19:27Z"))
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole[0]", Matchers.is("Signing as king of signers"))
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole[1]", Matchers.is("Second role"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("Elbonia"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("Harju"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.City", Matchers.is("Tallinn"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.PostalCode", Matchers.is("32323"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-2
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
     *
     */
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtSignature() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("validTsSignatureWithRolesAndProductionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.CommonName", Matchers.is("JÕEORG,JAAK-KRISTJAN,38001085718"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.SerialNumber", Matchers.is("PNOEE-38001085718"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Name", Matchers.is("test.txt"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FULL"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Content", Matchers.is("Full document"))
                .body("Signatures.Signature[0].ClaimedSigningTime", Matchers.is("2020-05-29T09:34:56Z"))
                .body("Signatures.Signature[0].Info.BestSignatureTime", Matchers.is("2020-05-29T09:34:58Z"))
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole[0]", Matchers.is("First role"))
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole[1]", Matchers.is("Second role"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("Some country"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("ÕÄLnül23#&()"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.City", Matchers.is("City with spaces"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.PostalCode", Matchers.is("123456789"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings.Warning[0].Content", Matchers.is("The trusted certificate does not match the trust service!"))
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT, AdES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: 23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc
     *
     */
    @Test
    @Ignore //TODO: New testfile needed
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdes() {
        setTestFilesDirectory("bdoc/test/timemark/");
        post(validationRequestForDocument("23154_test1-old-sig-sigat-NOK-prodat-OK-1.bdoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("ADES"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LT_TM, AdESqc, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: testAdesQC.bdoc
     *
     */
    @Ignore //TODO: Testfile needed
    @Test
    public void SoapBdocCorrectValuesArePresentValidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestForDocument("testAdesQC.bdoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("ADESIG_QC"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Bdoc-SoapValidationReportValue-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report XAdES_BASELINE_LTA, QES, FullSignatureScope
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: EE_SER-AEX-B-LTA-V-24.bdoc
     *
     */
    @Test
    @Ignore("DD4J-615")
    public void SoapBdocCorrectValuesArePresentInvalidLtSignatureAdesqc() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestForDocument("EE_SER-AEX-B-LTA-V-24.bdoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("ASiC-E"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-1
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
     *
     */
    @Test
    public void SoapDdocCorrectValuesArePresentV1_0() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("SK-XML1.0.ddoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[0].Id", Matchers.is("S0"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("SK_XML_1.0"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("ANSIP,ANDRUS,35610012722"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", Matchers.is("Tartu ja Tallinna koostooleping.doc"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", Matchers.is("Digest of the document content"))
                .body("Signatures.Signature[0].ClaimedSigningTime", Matchers.is("2002-10-07T12:10:19Z"))
                .body("Signatures.Signature[0].Warnings.Warning[0].Content", Matchers.is("Old and unsupported format: SK-XML version: 1.0"))
                .body("Signatures.Signature[0].Info.BestSignatureTime", Matchers.is("2002-10-07T11:10:47Z"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("Eesti"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.StateOrProvince", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.City", Matchers.is("Tallinn"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.PostalCode", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.0"))
                .body("ValidatedDocument.Filename", Matchers.is("SK-XML1.0.ddoc"))
                .body("ValidSignaturesCount", Matchers.is("2"))
                .body("SignaturesCount", Matchers.is("2"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-2
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
     *
     */
    @Test
    public void SoapDdocCorrectValuesArePresentV1_1() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("igasugust1.1.ddoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("3"))
                .body("ValidSignaturesCount", Matchers.is("3"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.CommonName", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.SerialNumber", Matchers.is("38508134916"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.GivenName", Matchers.is("SIMMO"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.Surname", Matchers.is("SOONSEIN"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", Matchers.is("DigiDocService_spec_1_110_est.pdf"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", Matchers.is("Digest of the document content"))
                .body("Signatures.Signature[0].ClaimedSigningTime", Matchers.is("2009-06-01T10:42:19Z"))
                .body("Signatures.Signature[0].Warnings.Warning[0].Content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.1"))
                .body("Signatures.Signature[0].Info.BestSignatureTime", Matchers.is("2009-06-01T10:42:25Z"))
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole[0]", Matchers.is("Test"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("eesti"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("harju"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.City", Matchers.is("tallinn"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.PostalCode", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.1"))
                .body("ValidatedDocument.Filename", Matchers.is("igasugust1.1.ddoc"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-3
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
     *
     */
    @Test
    public void SoapDdocCorrectValuesArePresentV1_2() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("igasugust1.2.ddoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("3"))
                .body("ValidSignaturesCount", Matchers.is("3"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.CommonName", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.SerialNumber", Matchers.is("38508134916"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", Matchers.is("DigiDocService_spec_1_110_est.pdf"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", Matchers.is("Digest of the document content"))
                .body("Signatures.Signature[0].ClaimedSigningTime", Matchers.is("2009-06-01T10:45:44Z"))
                .body("Signatures.Signature[0].Warnings.Warning[0].Content", Matchers.is("Old and unsupported format: DIGIDOC-XML version: 1.2"))
                .body("Signatures.Signature[0].Info.BestSignatureTime", Matchers.is("2009-06-01T10:45:49Z"))
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole[0]", Matchers.is("Test"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("eesti"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("harju"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.City", Matchers.is("otepää"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.PostalCode", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.2"))
                .body("ValidatedDocument.Filename", Matchers.is("igasugust1.2.ddoc"));
    }

    /**
     *
     * TestCaseID: Ddoc-SoapValidationReportValue-4
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
     *
     */
    @Test
    public void SoapDdocCorrectValuesArePresentV1_3() {
        setTestFilesDirectory("ddoc/live/timemark/");
        post(validationRequestForDocument("igasugust1.3.ddoc"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("3"))
                .body("ValidSignaturesCount", Matchers.is("3"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.CommonName", Matchers.is("SOONSEIN,SIMMO,38508134916"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.SerialNumber", Matchers.is("38508134916"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", Matchers.is("DigiDocService_spec_1_110_est.pdf"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("FullSignatureScope"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", Matchers.is("Digest of the document content"))
                .body("Signatures.Signature[0].ClaimedSigningTime", Matchers.is("2009-06-01T10:46:37Z"))
                .body("Signatures.Signature[0].Warnings", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].Info.BestSignatureTime", Matchers.is("2009-06-01T10:46:42Z"))
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole[0]", Matchers.is("Test"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("eesti"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("ei tea"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.City", Matchers.is("tõrva"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.PostalCode", Matchers.emptyOrNullString())
                .body("SignatureForm", Matchers.is("DIGIDOC_XML_1.3"))
                .body("ValidatedDocument.Filename", Matchers.is("igasugust1.3.ddoc"));
    }

    /**
     *
     * TestCaseID: Pdf-SoapValidationReportValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, PAdES_baseline_LT, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: pades_lt_two_valid_sig.pdf
     *
     */
    @Test
    public void SoapPdfCorrectValuesArePresentBaselineLtSignature() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestForDocument("reason_and_location_Test.pdf"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("1"))
                .body("Signatures.Signature[0].Id", Matchers.is("S-4D0D5A83688FC617AA83810ED74E26C5A79063D110B00AD207EAB3EFDC3F5619"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("Signatures.Signature[0].SignatureMethod", Matchers.is("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("QESIG"))
                .body("Signatures.Signature[0].SignedBy", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.CommonName", Matchers.is("ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.SerialNumber", Matchers.is("11404176865"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.GivenName", Matchers.is("MÄRÜ-LÖÖZ"))
                .body("Signatures.Signature[0].SubjectDistinguishedName.Surname", Matchers.is("ŽÕRINÜWŠKY"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-PASSED"))
                .body("Signatures.Signature[0].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Name", Matchers.is("Partial PDF"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Scope", Matchers.is("PARTIAL"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope[0].Content", Matchers.is("The document ByteRange : [0, 2226, 21172, 314]"))
                .body("Signatures.Signature[0].ClaimedSigningTime", Matchers.is("2020-05-27T09:59:07Z"))
                .body("Signatures.Signature[0].Info.BestSignatureTime", Matchers.is("2020-05-27T09:59:09Z"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("Narva"))
                .body("Signatures.Signature[0].Info.SigningReason", Matchers.is("Roll??"))
                .body("ValidatedDocument.Filename", Matchers.is("reason_and_location_Test.pdf"));
    }

    /**
     *
     * TestCaseID: Pdf-SoapValidationReportValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of values in Validation Report, PAdES_baseline_B, checks for missing info
     *
     * Expected Result: All required elements are present and meet the expected values and other values are empty as expected.
     *
     * File: hellopades-pades-b-sha256-auth.pdf
     *
     */
    @Test
    public void SoapPdfCorrectValuesArePresentInvalidBaselineBSignatureV2() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestForDocument("hellopades-pades-b-sha256-auth.pdf"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("SignaturesCount", Matchers.is("1"))
                .body("ValidSignaturesCount", Matchers.is("0"))
                .body("Signatures.Signature[0].SignatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("Signatures.Signature[0].Indication", Matchers.is("TOTAL-FAILED"))
                .body("Signatures.Signature[0].SubIndication", Matchers.is("FORMAT_FAILURE"))
                .body("Signatures.Signature[0].SignatureLevel", Matchers.is("NOT_ADES"))
                .body("Signatures.Signature[0].SignatureScopes.SignatureScope.Scope", Matchers.is("FULL"));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of filled SignerRole and SignatureProductionPlace values.
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceValidIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[0].Info.SignerRole.ClaimedRole", Matchers.is("Normal SignerRoleV2Type"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.CountryName", Matchers.is("Estonia"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("Harju County"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.City", Matchers.is("Tallinn"))
                .body("Signatures.Signature[0].Info.SignatureProductionPlace.PostalCode", Matchers.is("12345"));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of filled SignerRole and SignatureProductionPlace special character values.
     *
     * Expected Result: All required elements are present and meet the expected values.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceValidSpecialCharactersIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[1].Info.SignerRole.ClaimedRole[0]", Matchers.is("Special character SignerRoleV2Type - Õäöüžš"))
                .body("Signatures.Signature[1].Info.SignatureProductionPlace.CountryName", Matchers.is("Эсто́ния"))
                .body("Signatures.Signature[1].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("И́да-Ви́руский уезд"))
                .body("Signatures.Signature[1].Info.SignatureProductionPlace.City", Matchers.is("На́рва"))
                .body("Signatures.Signature[1].Info.SignatureProductionPlace.PostalCode", Matchers.is("#12345%"));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of filled SignerRole and SignatureProductionPlace values in XML and JSON syntax.
     *
     * Expected Result: All required elements are present and meet the expected values. Structure is not malformed.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceSoapAndJsonFormatIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[2].Info.SignerRole.ClaimedRole[0]", Matchers.is("{\"claimedRole\": \"XML/JSON SignerRoleV2Type\", </ns3:ClaimedRole>"))
                .body("Signatures.Signature[2].Info.SignatureProductionPlace.CountryName", Matchers.is("\"countryName\": \"Eesti\""))
                .body("Signatures.Signature[2].Info.SignatureProductionPlace.StateOrProvince", Matchers.is("Harjumaa</ns3:SignatureProductionPlace>"))
                .body("Signatures.Signature[2].Info.SignatureProductionPlace.City", Matchers.is("<ns3:ClaimedRole>Tallinn"))
                .body("Signatures.Signature[2].Info.SignatureProductionPlace.PostalCode", Matchers.is("[ {\"postalCode1\": 12345}, {postalCode2\": \"12345\"}],"));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of empty SignerRole and SignatureProductionPlace elements.
     *
     * Expected Result: Structure does not contain SignerRole and SignatureProductionPlace elements.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceEmptyIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[3].Info", Matchers.not(new XmlNodeContainsKeyMatcher("SignatureProductionPlace")))
                .body("Signatures.Signature[3].Info", Matchers.not(new XmlNodeContainsKeyMatcher("SignerRole")));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of filled SignerRole and empty SignatureProductionPlace elements.
     *
     * Expected Result: SignerRole element is present and meets the expected value. Structure does not contain SignatureProductionPlace elements.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceOnlyRoleIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[4].Info.SignerRole.ClaimedRole[0]", Matchers.is("Only role"))
                .body("Signatures.Signature[4].Info", Matchers.not(new XmlNodeContainsKeyMatcher("SignatureProductionPlace")));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of empty SignerRole and partially filled SignatureProductionPlace element.
     *
     * Expected Result: SignatureProductionPlace.City element is present and meets the expected value. Structure does not contain SignerRole element.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceOnlyCityIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[5].Info", Matchers.not(new XmlNodeContainsKeyMatcher("SignerRole")))
                .body("Signatures.Signature[5].Info.SignatureProductionPlace.City", Matchers.is("Only city"));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of short data type maximum range value for SignerRole and SignatureProductionPlace.
     *
     * Expected Result: All required elements are present and corresponding values meet the short data type maximum range of 32767 characters.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceShortDataTypeMaxValueLengthIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        String shortTypeValue = "32767" + StringUtils.repeat("a", 32762);
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[6].Info.SignerRole.ClaimedRole[0]", Matchers.is(shortTypeValue))
                .body("Signatures.Signature[6].Info.SignatureProductionPlace.CountryName", Matchers.is(shortTypeValue))
                .body("Signatures.Signature[6].Info.SignatureProductionPlace.StateOrProvince", Matchers.is(shortTypeValue))
                .body("Signatures.Signature[6].Info.SignatureProductionPlace.City", Matchers.is(shortTypeValue))
                .body("Signatures.Signature[6].Info.SignatureProductionPlace.PostalCode", Matchers.is(shortTypeValue));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title:  Verification of int data type value longer than short data type maximum value for SignatureProductionPlace.
     *
     * Expected Result: Element is present and meets expected value of 32768 characters.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceIntDataTypeValueLengthCityIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        String intValue = "32768" + StringUtils.repeat("a", 32763);
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[7].Info.SignatureProductionPlace.City", Matchers.is(intValue));
    }

    /**
     *
     * TestCaseID: Asice-SoapRoleAndProductionPlaceValue-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title: Verification of 3 concurrent claimed roles.
     *
     * Expected Result: All claimed roles are present and meet the expected values.
     *
     * File: role_productionPlace.asice
     *
     */
    @Test
    public void SoapAsiceRoleAndPlaceThreeRolesIT() {
        setTestFilesDirectory("bdoc/test/timestamp/");
        post(validationRequestForDocument("role_productionPlace.asice"))
                .then().rootPath(SOAP_VALIDATION_CONCLUSION_PREFIX)
                .body("Signatures.Signature[8].Info.SignerRole.ClaimedRole[0]", Matchers.is("First role"))
                .body("Signatures.Signature[8].Info.SignerRole.ClaimedRole[1]", Matchers.is("Second role"))
                .body("Signatures.Signature[8].Info.SignerRole.ClaimedRole[2]", Matchers.is("Third role"))
                .body("Signatures.Signature[8].Errors", Matchers.emptyOrNullString())
                .body("Signatures.Signature[8].Warnings.Warning[0].Content", Matchers.is("The trusted certificate does not match the trust service!"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}