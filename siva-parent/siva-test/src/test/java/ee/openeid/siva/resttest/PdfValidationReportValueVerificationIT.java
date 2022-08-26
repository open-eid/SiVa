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
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.openeid.siva.integrationtest.TestData.VALIDATION_CONCLUSION_PREFIX;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Category(IntegrationTest.class)

public class PdfValidationReportValueVerificationIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/baseline_profile_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);}

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
                .body("signatures[0].id", Matchers.is("S-1CA4D655909860192F80E6EA6D3FCC18C25A81E8902819C5E05B5C12D5BD6784"))
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
    public void pdfAllElementsArePresentValidMultipleSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("pades_lt_two_valid_sig.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[1].id", Matchers.is("S-87966FE26A3FB0B27130B11EBF254A196E9C3319A56D25D479FFF2780C00494D"))
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
                .body("signatures[1].id", Matchers.is("S-8E37E9F25D08ECE70EA1D135CEFCFE8A713CB2AD39183D1591A4561A4809EB90"))
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
                .body("signatures[0].id", Matchers.is("S-12BD46636D1B6AE7156E209D4AC465A61B13D0BBB9668E07690091D1E4BB8F3E"))
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
     * TestCaseID: Pdf-ValidationReportVerification-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title:  PDF with PAdES-LT and B signatures with same signer certificate - ocspResponseCreationTimes in mixed container are reported correctly
     *
     * Expected Result: ocspResponseCreationTime for LT is present and for B profile is not.
     *
     * File: hellopades-lt-b.pdf
     */
    @Ignore("SIVA-365")
    @Test
    public void pdfMixedSameCertificateSignaturesCorrectOcspResponseCreationTime() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("hellopades-lt-b.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].info", Matchers.hasKey("ocspResponseCreationTime"))
                .body("signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("signatures[1].signatureLevel", Matchers.is("NOT_ADES"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].info", Matchers.not(Matchers.hasKey("ocspResponseCreationTime")))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Pdf-ValidationReportVerification-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-response-interface
     *
     * Title:  PDF with PAdES-T and B profile signatures with different signer certificates - ocspResponseCreationTimes in mixed container are reported correctly
     *
     * Expected Result: ocspResponseCreationTime for T profile is present and for B profile is not
     *
     * File: hellopades-lt-b.pdf
     */
    @Test
    public void pdfMixedDifferentCertificateSignaturesCorrectOcspResponseCreationTime() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        post(validationRequestFor("hellopades-pades-b-t-sha256-auth.pdf"))
                .then().rootPath(VALIDATION_CONCLUSION_PREFIX)
                .body(matchesJsonSchemaInClasspath("SimpleReportSchema.json"))
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_B"))
                .body("signatures[0].signatureLevel", Matchers.is("NOT_ADES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].info", Matchers.not(Matchers.hasKey("ocspResponseCreationTime")))
                .body("signatures[1].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("signatures[1].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("signatures[1].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[1].info.ocspResponseCreationTime", Matchers.is("2022-08-23T14:59:17Z"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(2));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


}
