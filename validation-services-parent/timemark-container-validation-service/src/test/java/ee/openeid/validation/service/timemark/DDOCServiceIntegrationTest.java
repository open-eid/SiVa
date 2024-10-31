/*
 * Copyright 2020 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.document.report.DataFileData;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.Scope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.SubjectDistinguishedName;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.siva.validation.util.CertUtil;
import ee.openeid.tsl.TSLRefresher;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.tsl.configuration.TSLLoaderDefinitionRegisterer;
import ee.openeid.validation.service.timemark.configuration.TimemarkContainerValidationServiceConfiguration;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.BDOCSignaturePolicyService;
import ee.openeid.validation.service.timemark.tsl.TimemarkTrustedListsCertificateSource;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        TSLLoaderConfiguration.class,
        TSLLoaderDefinitionRegisterer.class,
        TSLRefresher.class,
        TSLValidationJobFactory.class,
        TimemarkTrustedListsCertificateSource.class,
        TimemarkContainerValidationServiceConfiguration.class,
        TimemarkContainerValidationService.class,
        DDOCDataFilesService.class,
        BDOCSignaturePolicyService.class,
        ConstraintLoadingSignaturePolicyService.class,
        BDOCConfigurationService.class,
        ReportConfigurationProperties.class,
        ProxyConfig.class
})
@ActiveProfiles("test")
class DDOCServiceIntegrationTest {
    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String VALID_DDOC_2_SIGNATURES = "ddoc_valid_2_signatures.ddoc";
    private static final String DATAFILE_XMLNS_MISSING = "datafile_xmlns_missing.ddoc";
    private static final String DDOC_1_3_HASHCODE = "DigiDoc 1.3 hashcode.ddoc";
    private static final String DDOC_1_0_HASHCODE = "DigiDoc 1.0 hashcode.ddoc";
    private static final String DDOC_1_2_HASHCODE = "DigiDoc 1.2 hashcode.ddoc";
    static final String VALID_DDOC = "ddoc_valid_2_signatures.ddoc";

    @Autowired
    private TimemarkContainerValidationService timemarkContainerValidationService;
    @Autowired
    private DDOCDataFilesService ddocDataFilesService;
    @Autowired
    private BDOCConfigurationService configurationService;
    @Autowired
    private ReportConfigurationProperties reportConfigurationProperties;

    @Test
    void getDataFilesDDOCWithMalformedBytesResultsInMalformedDocumentException() {
        DataFilesDocument dataFilesDocument = buildDataFilesDocument();
        dataFilesDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));
        assertThrows(
                MalformedDocumentException.class, () -> ddocDataFilesService.getDataFiles(dataFilesDocument)
        );
    }

    @Test
    void ddocGetDataFilesResultShouldIncludeDataFilesReportPOJO() {
        DataFilesReport dataFilesReport = ddocDataFilesService.getDataFiles(buildDataFilesDocument());
        assertNotNull(dataFilesReport);
    }

    @Test
    void ddocGetDataFilesShouldReturnDataFilesReportWithDataFileIncluded() {
        DataFilesReport report = ddocDataFilesService.getDataFiles(buildDataFilesDocument());

        List<DataFileData> dataFiles = report.getDataFiles();

        assertFalse(dataFiles.isEmpty());

        DataFileData dataFileData = dataFiles.get(0);

        assertEquals("VGVzdDENClRlc3QyDQpUZfB0Mw0KS2H+bWFhciAuLi4uDQo=", dataFileData.getBase64());
        assertEquals("Šužlikud sõid ühe õuna ära.txt", dataFileData.getFilename());
        assertEquals("text/plain", dataFileData.getMimeType());
        assertEquals(35, dataFileData.getSize());
    }

    @Test
    void validatingADDOCWithMalformedBytesResultsInMalformedDocumentException() {
        ValidationDocument validationDocument = buildValidationDocument(VALID_DDOC_2_SIGNATURES);
        validationDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));
        assertThrows(
                MalformedDocumentException.class, () -> timemarkContainerValidationService.validateDocument(validationDocument)
        );
    }

    @Test
    void ddocValidationResultShouldIncludeValidationReportPOJO() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        assertNotNull(validationResult2Signatures);
    }

    @Test
    void validationReportShouldIncludeSignatureFormWithCorrectPrefixAndVersion() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.3", validationResult2Signatures.getValidationConclusion().getSignatureForm());
    }

    @Test
    void validationReportShouldIncludeRequiredFields() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        ValidationConclusion validationConclusion = validationResult2Signatures.getValidationConclusion();
        assertNotNull(validationConclusion.getPolicy());
        assertNotNull(validationConclusion.getValidationTime());
        assertEquals(VALID_DDOC_2_SIGNATURES, validationConclusion.getValidatedDocument().getFilename());
        assertThat(validationConclusion.getSignatures(), hasSize(2));
        assertEquals(2, validationConclusion.getValidSignaturesCount());
        assertEquals(2, validationConclusion.getSignaturesCount());
    }

    @Test
    void validationReportShouldHaveCorrectSignatureValidationDataForSignature1() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        SignatureValidationData sig1 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S0"))
                .findFirst()
                .orElseThrow();

        assertEquals("DIGIDOC_XML_1.3", sig1.getSignatureFormat());
        assertEquals("http://www.w3.org/2000/09/xmldsig#rsa-sha1", sig1.getSignatureMethod());
        assertThat(sig1.getSignatureLevel(), emptyOrNullString());
        assertEquals("KESKEL,URMO,38002240232", sig1.getSignedBy());
        assertEquals("KESKEL,URMO,38002240232", sig1.getSubjectDistinguishedName().getCommonName());
        assertEquals("38002240232", sig1.getSubjectDistinguishedName().getSerialNumber());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig1.getIndication());
        assertThat(sig1.getErrors(), empty());
        assertThat(sig1.getWarnings(), empty());
        assertThat(sig1.getSignatureScopes(), hasSize(1));
        Scope scope = sig1.getSignatureScopes().get(0);
        assertEquals("Šužlikud sõid ühe õuna ära.txt", scope.getName());
        assertEquals("2005-02-11T16:23:43Z", sig1.getInfo().getBestSignatureTime());
        assertEquals("7BWOmJnhm9HUbcnnnb/9SkYe1ok=", sig1.getInfo().getTimeAssertionMessageImprint());
        assertEquals(1, sig1.getInfo().getSignerRole().size());
        assertEquals("Sušlik", sig1.getInfo().getSignerRole().get(0).getClaimedRole());
        assertEquals("Kurežžaare", sig1.getInfo().getSignatureProductionPlace().getCity());
        assertEquals("Hõrjumaa", sig1.getInfo().getSignatureProductionPlace().getStateOrProvince());
        assertEquals("123", sig1.getInfo().getSignatureProductionPlace().getPostalCode());
        assertEquals("Šveits", sig1.getInfo().getSignatureProductionPlace().getCountryName());
        assertEquals("Digest of the document content", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2005-02-11T16:23:21Z", sig1.getClaimedSigningTime());
    }

    @Test
    void validationReportShouldHaveCorrectSignatureValidationDataForSignature2() {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        SignatureValidationData sig2 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S1"))
                .findFirst()
                .orElseThrow();

        assertEquals("DIGIDOC_XML_1.3", sig2.getSignatureFormat());
        assertEquals("http://www.w3.org/2000/09/xmldsig#rsa-sha1", sig2.getSignatureMethod());
        assertTrue(StringUtils.isEmpty(sig2.getSignatureLevel()));
        assertEquals("JALUKSE,KRISTJAN,38003080336", sig2.getSignedBy());
        assertEquals("JALUKSE,KRISTJAN,38003080336", sig2.getSubjectDistinguishedName().getCommonName());
        assertEquals("38003080336", sig2.getSubjectDistinguishedName().getSerialNumber());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig2.getIndication());
        assertThat(sig2.getErrors(), empty());
        assertThat(sig2.getWarnings(), empty());
        assertThat(sig2.getSignatureScopes(), hasSize(1));
        Scope scope = sig2.getSignatureScopes().get(0);
        assertEquals("Šužlikud sõid ühe õuna ära.txt", scope.getName());
        assertEquals("Digest of the document content", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2009-02-13T09:22:58Z", sig2.getInfo().getBestSignatureTime());
        assertEquals("UDM+W5rBO6kwBcbHzHvN5/M0r/k=", sig2.getInfo().getTimeAssertionMessageImprint());
        assertEquals("2009-02-13T09:22:49Z", sig2.getClaimedSigningTime());
        assertTrue(sig2.getInfo().getSignerRole().isEmpty());
        assertEquals(" ", sig2.getInfo().getSignatureProductionPlace().getCity());
        assertEquals(" ", sig2.getInfo().getSignatureProductionPlace().getStateOrProvince());
        assertEquals(" ", sig2.getInfo().getSignatureProductionPlace().getPostalCode());
        assertEquals(" ", sig2.getInfo().getSignatureProductionPlace().getCountryName());
    }

    @Test
    void dDocValidationError173ForMissingDataFileXmlnsShouldBeShownAsWarningInReport() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DATAFILE_XMLNS_MISSING)).getSimpleReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(validationConclusion.getSignaturesCount(), validationConclusion.getValidSignaturesCount());
        SignatureValidationData signature = validationConclusion.getSignatures().get(0);
        assertThat(signature.getErrors(), empty());
        assertThat(signature.getWarnings(), hasSize(1));
        assertEquals("Bad digest for DataFile: D0 alternate digest matches!", signature.getWarnings().get(0).getContent());
    }

    @Test
    @Disabled("SIVA-159")
    void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode13Format() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DDOC_1_3_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.3_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    @Disabled("SIVARIA2-126")
    void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode10Format() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DDOC_1_0_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.0_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    @Disabled("SIVA-159")
    void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode12Format() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DDOC_1_2_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.2_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    void validationReportShouldContainDefaultPolicyWhenPolicyIsNotExplicitlyGiven() throws Exception {
        Policy policy = validateWithPolicy("").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    void validationReportShouldContainAdesPolicyWhenAdesPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv3").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    void validationReportShouldContainQESPolicyWhenQESPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv4").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() {
        assertThrows(
                InvalidPolicyException.class, () -> validateWithPolicy("non-existing-policy")
        );
    }

    @Test
    void multiSignatureDDOCSubjectDNValuesPresent() {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        SignatureValidationData signatureValidationData = report.getValidationConclusion().getSignatures().get(0);
        SignatureValidationData signatureValidationData2 = report.getValidationConclusion().getSignatures().get(1);

        assertSubjectDNPresent(signatureValidationData, "KESKEL,URMO,38002240232", "38002240232");
        assertSubjectDNPresent(signatureValidationData2, "JALUKSE,KRISTJAN,38003080336", "38003080336");
    }

    @Test
    void timeAssertionMessageImprintIsEmptyForCorruptedOcspData() {
        SimpleReport report = timemarkContainerValidationService
                .validateDocument(buildValidationDocument("ddoc_corrupted_ocsp_2_signatures.ddoc"))
                .getSimpleReport();

        SignatureValidationData signatureValidationData = report.getValidationConclusion().getSignatures().get(0);
        SignatureValidationData signatureValidationData2 = report.getValidationConclusion().getSignatures().get(1);

        assertEquals("", signatureValidationData.getInfo().getTimeAssertionMessageImprint());
        assertEquals("", signatureValidationData2.getInfo().getTimeAssertionMessageImprint());
    }

    @Test
    void timeAssertionMessageImprintIsEmptyForMissingOcspData() {
        SimpleReport report = timemarkContainerValidationService
                .validateDocument(buildValidationDocument("ddoc_missing_ocsp_2_signatures.ddoc"))
                .getSimpleReport();

        SignatureValidationData signatureValidationData = report.getValidationConclusion().getSignatures().get(0);
        SignatureValidationData signatureValidationData2 = report.getValidationConclusion().getSignatures().get(1);

        assertEquals("", signatureValidationData.getInfo().getTimeAssertionMessageImprint());
        assertEquals("", signatureValidationData2.getInfo().getTimeAssertionMessageImprint());
    }

    @Test
    void certificateValuesPresent() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        SignatureValidationData signatureValidationData1 = report.getValidationConclusion().getSignatures().get(0);
        SignatureValidationData signatureValidationData2 = report.getValidationConclusion().getSignatures().get(1);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        assertEquals(2, signatureValidationData1.getCertificates().size());
        assertEquals(2, signatureValidationData2.getCertificates().size());

        ee.openeid.siva.validation.document.report.Certificate signerCertificate1 = signatureValidationData1.getCertificatesByType(CertificateType.SIGNING).get(0);
        Certificate signerX509Certificate1 = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signerCertificate1.getContent().getBytes())));
        assertEquals("KESKEL,URMO,38002240232", CertUtil.getCommonName((X509Certificate) signerX509Certificate1));
        assertEquals("KESKEL,URMO,38002240232", signerCertificate1.getCommonName());

        ee.openeid.siva.validation.document.report.Certificate revocationCertificate1 = signatureValidationData1.getCertificatesByType(CertificateType.REVOCATION).get(0);
        Certificate revocationX509Certificate1 = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(revocationCertificate1.getContent().getBytes())));
        assertEquals("ESTEID-SK OCSP RESPONDER", CertUtil.getCommonName((X509Certificate) revocationX509Certificate1));
        assertEquals("ESTEID-SK OCSP RESPONDER", revocationCertificate1.getCommonName());

        ee.openeid.siva.validation.document.report.Certificate signerCertificate2 = signatureValidationData2.getCertificatesByType(CertificateType.SIGNING).get(0);
        Certificate signerX509Certificate2 = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(signerCertificate2.getContent().getBytes())));

        assertEquals("JALUKSE,KRISTJAN,38003080336", CertUtil.getCommonName((X509Certificate) signerX509Certificate2));
        assertEquals("JALUKSE,KRISTJAN,38003080336", signerCertificate2.getCommonName());

        ee.openeid.siva.validation.document.report.Certificate revocationCertificate2 = signatureValidationData2.getCertificatesByType(CertificateType.REVOCATION).get(0);
        Certificate revocationX509Certificate2 = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(revocationCertificate2.getContent().getBytes())));
        assertEquals("ESTEID-SK 2007 OCSP RESPONDER", CertUtil.getCommonName((X509Certificate) revocationX509Certificate2));
        assertEquals("ESTEID-SK 2007 OCSP RESPONDER", revocationCertificate2.getCommonName());
    }

    private void assertSubjectDNPresent(SignatureValidationData signature, String commonName, String serialNumber) {
        SubjectDistinguishedName subjectDistinguishedName = signature.getSubjectDistinguishedName();
        assertNotNull(subjectDistinguishedName);
        assertEquals(serialNumber, subjectDistinguishedName.getSerialNumber());
        assertEquals(commonName, subjectDistinguishedName.getCommonName());
    }

    private static DataFilesDocument buildDataFilesDocument() {
        return DummyDataFilesDocumentBuilder
                .aDataFilesDocument()
                .withDocument(TEST_FILES_LOCATION + VALID_DDOC)
                .build();
    }

    private static ValidationDocument buildValidationDocument(String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

    static class DummyDataFilesDocumentBuilder {

        private final DataFilesDocument dataFilesDocument;

        private DummyDataFilesDocumentBuilder() {
            dataFilesDocument = new DataFilesDocument();
        }

        static DummyDataFilesDocumentBuilder aDataFilesDocument() {
            return new DummyDataFilesDocumentBuilder();
        }

        DummyDataFilesDocumentBuilder withDocument(String filePath) {
            try (InputStream in = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath))) {
                dataFilesDocument.setBytes(in.readAllBytes());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load dummy data files document", e);
            }

            return this;
        }

        public DataFilesDocument build() {
            return dataFilesDocument;
        }
    }

    private SimpleReport validateWithPolicy(String policyName) {
        ValidationDocument validationDocument = buildValidationDocument(VALID_DDOC_2_SIGNATURES);
        validationDocument.setSignaturePolicy(policyName);
        return timemarkContainerValidationService.validateDocument(validationDocument).getSimpleReport();
    }
}
