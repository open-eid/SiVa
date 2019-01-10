package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.tsl.CustomCertificatesLoader;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.timemark.configuration.TimemarkContainerValidationServiceConfiguration;
import ee.openeid.validation.service.timemark.signature.policy.BDOCConfigurationService;
import ee.openeid.validation.service.timemark.signature.policy.BDOCSignaturePolicyService;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        TSLLoaderConfiguration.class,
        TSLLoader.class,
        CustomCertificatesLoader.class,
        TSLValidationJobFactory.class,
        TimemarkContainerValidationServiceConfiguration.class,
        TimemarkContainerValidationService.class,
        DDOCDataFilesService.class,
        BDOCSignaturePolicyService.class,
        ConstraintLoadingSignaturePolicyService.class,
        BDOCConfigurationService.class,
        ReportConfigurationProperties.class
})
@ActiveProfiles("test")
public class DDOCServiceIntegrationTest {
    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String VALID_DDOC_2_SIGNATURES = "ddoc_valid_2_signatures.ddoc";
    private static final String DATAFILE_XMLNS_MISSING = "datafile_xmlns_missing.ddoc";
    private static final String DDOC_1_3_HASHCODE = "DigiDoc 1.3 hashcode.ddoc";
    private static final String DDOC_1_0_HASHCODE = "DigiDoc 1.0 hashcode.ddoc";
    private static final String DDOC_1_2_HASHCODE = "DigiDoc 1.2 hashcode.ddoc";
    public static final String VALID_DDOC = "ddoc_valid_2_signatures.ddoc";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Autowired
    private TimemarkContainerValidationService timemarkContainerValidationService;
    @Autowired
    private DDOCDataFilesService ddocDataFilesService;
    @Autowired
    private BDOCConfigurationService configurationService;
    @Autowired
    private ReportConfigurationProperties reportConfigurationProperties;

    @Test
    public void getDataFilesDDOCWithMalformedBytesResultsInMalformedDocumentException() throws Exception {
        DataFilesDocument dataFilesDocument = buildDataFilesDocument();
        dataFilesDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));
        expectedException.expect(MalformedDocumentException.class);
        ddocDataFilesService.getDataFiles(dataFilesDocument);
    }

    @Test
    public void ddocGetDataFilesResultShouldIncludeDataFilesReportPOJO() throws Exception {
        DataFilesReport dataFilesReport = ddocDataFilesService.getDataFiles(buildDataFilesDocument());
        assertNotNull(dataFilesReport);
    }

    @Test
    public void ddocGetDataFilesShouldReturnDataFilesReportWithDataFileIncluded() throws Exception {
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
    public void validatingADDOCWithMalformedBytesResultsInMalformedDocumentException() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(VALID_DDOC_2_SIGNATURES);
        validationDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));
        expectedException.expect(MalformedDocumentException.class);
        timemarkContainerValidationService.validateDocument(validationDocument);
    }

    @Test
    public void ddocValidationResultShouldIncludeValidationReportPOJO() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        assertNotNull(validationResult2Signatures);
    }

    @Test
    public void vShouldIncludeSignatureFormWithCorrectPrefixAndVersion() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.3", validationResult2Signatures.getValidationConclusion().getSignatureForm());
    }

    @Test
    public void vShouldIncludeRequiredFields() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        ValidationConclusion validationConclusion = validationResult2Signatures.getValidationConclusion();
        assertNotNull(validationConclusion.getPolicy());
        assertNotNull(validationConclusion.getValidationTime());
        assertEquals(VALID_DDOC_2_SIGNATURES, validationConclusion.getValidatedDocument().getFilename());
        assertTrue(validationConclusion.getSignatures().size() == 2);
        assertTrue(validationConclusion.getValidSignaturesCount() == 2);
        assertTrue(validationConclusion.getSignaturesCount() == 2);
    }

    @Test
    public void vShouldHaveCorrectSignatureValidationDataForSignature1() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        SignatureValidationData sig1 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S0"))
                .findFirst()
                .get();

        assertEquals("DIGIDOC_XML_1.3", sig1.getSignatureFormat());
        assertTrue(StringUtils.isEmpty(sig1.getSignatureLevel()));
        assertEquals("KESKEL,URMO,38002240232", sig1.getSignedBy());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig1.getIndication());
        assertTrue(sig1.getErrors().size() == 0);
        assertTrue(sig1.getWarnings().isEmpty());
        assertTrue(sig1.getSignatureScopes().size() == 1);
        SignatureScope scope = sig1.getSignatureScopes().get(0);
        assertEquals("Šužlikud sõid ühe õuna ära.txt", scope.getName());
        assertEquals("2005-02-11T16:23:43Z", sig1.getInfo().getBestSignatureTime());
        assertEquals("Full document", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2005-02-11T16:23:21Z", sig1.getClaimedSigningTime());
    }

    @Test
    public void vShouldHaveCorrectSignatureValidationDataForSignature2() throws Exception {
        SimpleReport validationResult2Signatures = timemarkContainerValidationService.validateDocument(buildValidationDocument(VALID_DDOC_2_SIGNATURES)).getSimpleReport();
        SignatureValidationData sig2 = validationResult2Signatures.getValidationConclusion().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S1"))
                .findFirst()
                .get();

        assertEquals("DIGIDOC_XML_1.3", sig2.getSignatureFormat());
        assertTrue(StringUtils.isEmpty(sig2.getSignatureLevel()));
        assertEquals("JALUKSE,KRISTJAN,38003080336", sig2.getSignedBy());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig2.getIndication());
        assertTrue(sig2.getErrors().size() == 0);
        assertTrue(sig2.getWarnings().isEmpty());
        assertTrue(sig2.getSignatureScopes().size() == 1);
        SignatureScope scope = sig2.getSignatureScopes().get(0);
        assertEquals("Šužlikud sõid ühe õuna ära.txt", scope.getName());
        assertEquals("Full document", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2009-02-13T09:22:58Z", sig2.getInfo().getBestSignatureTime());
        assertEquals("2009-02-13T09:22:49Z", sig2.getClaimedSigningTime());
    }

    @Test
    public void dDocValidationError173ForMissingDataFileXmlnsShouldBeShownAsWarningInReport() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DATAFILE_XMLNS_MISSING)).getSimpleReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(validationConclusion.getSignaturesCount(), validationConclusion.getValidSignaturesCount());
        SignatureValidationData signature = validationConclusion.getSignatures().get(0);
        assertTrue(signature.getErrors().isEmpty());
        assertTrue(signature.getWarnings().size() == 1);
        assertEquals("Bad digest for DataFile: D0 alternate digest matches!", signature.getWarnings().get(0).getContent());
    }

    @Test
    public void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode13Format() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DDOC_1_3_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.3_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    @Ignore("SIVARIA2-126")
    public void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode10Format() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DDOC_1_0_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.0_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    public void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode12Format() throws Exception {
        SimpleReport report = timemarkContainerValidationService.validateDocument(buildValidationDocument(DDOC_1_2_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.2_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    public void validationReportShouldContainDefaultPolicyWhenPolicyIsNotExplicitlyGiven() throws Exception {
        Policy policy = validateWithPolicy("").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainAdesPolicyWhenAdesPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv3").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.ADES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainQESPolicyWhenQESPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv4").getValidationConclusion().getPolicy();
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(PredefinedValidationPolicySource.QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() throws Exception {
        expectedException.expect(InvalidPolicyException.class);
        validateWithPolicy("non-existing-policy");
    }

    private static DataFilesDocument buildDataFilesDocument() throws Exception {
        return DummyDataFilesDocumentBuilder
                .aDataFilesDocument()
                .withDocument(TEST_FILES_LOCATION + VALID_DDOC)
                .build();
    }

    private static ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }
    static class DummyDataFilesDocumentBuilder {
        private static final Logger LOGGER = LoggerFactory.getLogger(DummyDataFilesDocumentBuilder.class);

        private DataFilesDocument dataFilesDocument;

        private DummyDataFilesDocumentBuilder() {
            dataFilesDocument = new DataFilesDocument();
        }

        static DummyDataFilesDocumentBuilder aDataFilesDocument() {
            return new DummyDataFilesDocumentBuilder();
        }

        DummyDataFilesDocumentBuilder withDocument(String filePath) {
            try {
                Path documentPath = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
                dataFilesDocument.setBytes(Files.readAllBytes(documentPath));
            } catch (IOException e) {
                LOGGER.warn("FAiled to load dummy data files document with error: {}", e.getMessage(), e);
            } catch (URISyntaxException e) {
                LOGGER.warn("Dummy document URL is invalid and ended with error: {}", e.getMessage(), e);
            }

            return this;
        }

        public DataFilesDocument build() {
            return dataFilesDocument;
        }
    }
    private SimpleReport validateWithPolicy(String policyName) throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(VALID_DDOC_2_SIGNATURES);
        validationDocument.setSignaturePolicy(policyName);
        return timemarkContainerValidationService.validateDocument(validationDocument).getSimpleReport();
    }
}
