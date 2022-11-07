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

import ee.openeid.siva.common.Constants;
import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Category(IntegrationTest.class)

public class DdocValidationReportValueVerificationIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/live/timemark/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);}

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
                .body("signatures[0].subjectDistinguishedName.givenName", Matchers.is("LIISA"))
                .body("signatures[0].subjectDistinguishedName.surname", Matchers.is("LUKIN"))
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
