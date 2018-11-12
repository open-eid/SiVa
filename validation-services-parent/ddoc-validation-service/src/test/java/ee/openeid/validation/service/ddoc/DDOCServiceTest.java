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

package ee.openeid.validation.service.ddoc;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.*;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.ddoc.configuration.DDOCSignaturePolicyProperties;
import ee.openeid.validation.service.ddoc.configuration.DDOCValidationServiceProperties;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;

import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.security.*")
public class DDOCServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String VALID_DDOC_2_SIGNATURES = "ddoc_valid_2_signatures.ddoc";
    private static final String DATAFILE_XMLNS_MISSING = "datafile_xmlns_missing.ddoc";
    private static final String DDOC_1_3_HASHCODE = "DigiDoc 1.3 hashcode.ddoc";
    private static final String DDOC_1_0_HASHCODE = "DigiDoc 1.0 hashcode.ddoc";
    private static final String DDOC_1_2_HASHCODE = "DigiDoc 1.2 hashcode.ddoc";
    private static final String VALID_DDOC = "ddoc_valid_2_signatures.ddoc";
    private static DDOCDataFilesService dataFilesService = new DDOCDataFilesService();
    private static DDOCValidationService validationService = new DDOCValidationService();
    private static DDOCSignaturePolicyProperties policyProperties = new DDOCSignaturePolicyProperties();
    private static SignaturePolicyService<ValidationPolicy> signaturePolicyService;
    private static XMLEntityAttackValidator xmlEntityAttackValidator;
    private static SimpleReport validationResult2Signatures;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws Exception {
        DDOCValidationServiceProperties properties = new DDOCValidationServiceProperties();
        properties.setJdigidocConfigurationFile("/siva-jdigidoc.cfg");

        policyProperties.initPolicySettings();
        signaturePolicyService = new SignaturePolicyService<>(policyProperties);
        xmlEntityAttackValidator = new XMLEntityAttackValidator();

        validationService.setProperties(properties);
        validationService.setXMLEntityAttackValidator(xmlEntityAttackValidator);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.initConfig();
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        dataFilesService.setXMLEntityAttackValidator(xmlEntityAttackValidator);
    }

    private static ValidationDocument ddocValid2Signatures() throws Exception {
        return buildValidationDocument(VALID_DDOC_2_SIGNATURES);
    }

    private static ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

    private static DataFilesDocument buildDataFilesDocument() throws Exception {
        return DummyDataFilesDocumentBuilder
                .aDataFilesDocument()
                .withDocument(TEST_FILES_LOCATION + VALID_DDOC)
                .build();
    }

    @Test
    public void getDataFilesDDOCWithMalformedBytesResultsInMalformedDocumentException() throws Exception {
        DataFilesDocument dataFilesDocument = buildDataFilesDocument();
        dataFilesDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));
        expectedException.expect(MalformedDocumentException.class);
        dataFilesService.getDataFiles(dataFilesDocument);
    }

    @Test
    public void ddocGetDataFilesResultShouldIncludeDataFilesReportPOJO() throws Exception {
        DataFilesReport dataFilesReport = dataFilesService.getDataFiles(buildDataFilesDocument());
        assertNotNull(dataFilesReport);
    }

    @Test
    public void ddocGetDataFilesShouldReturnDataFilesReportWithDataFileIncluded() throws Exception {
        DataFilesReport report = dataFilesService.getDataFiles(buildDataFilesDocument());

        List<DataFileData> dataFiles = report.getDataFiles();

        assertFalse(dataFiles.isEmpty());

        DataFileData dataFileData = dataFiles.get(0);

        assertEquals("VGVzdDENClRlc3QyDQpUZfB0Mw0KS2H+bWFhciAuLi4uDQo=\n", dataFileData.getBase64());
        assertEquals("Šužlikud sõid ühe õuna ära.txt", dataFileData.getFilename());
        assertEquals("text/plain", dataFileData.getMimeType());
        assertEquals(35, dataFileData.getSize());
    }

    @Test
    @PrepareForTest(ConfigManager.class)
    public void getDataFilesFailsWithExceptionWillThrowValidationServiceException() throws Exception {
        DataFilesDocument dataFilesDocument = new DataFilesDocument();
        dataFilesDocument.setBytes("".getBytes());

        mockStatic(ConfigManager.class);
        ConfigManager configManager = mock(ConfigManager.class);
        DigiDocFactory digiDocFactory = mock(DigiDocFactory.class);

        given(configManager.getDigiDocFactory()).willReturn(digiDocFactory);
        given(ConfigManager.instance()).willReturn(configManager);
        when(digiDocFactory.readSignedDocFromStreamOfType(any(ByteArrayInputStream.class), anyBoolean(), anyList())).thenThrow(new DigiDocException(101, "Testing error", new Exception()));

        DDOCDataFilesService dataFilesServiceSpy = spy(new DDOCDataFilesService());

        XMLEntityAttackValidator xmlEntityAttackValidator = spy(new XMLEntityAttackValidator());
        dataFilesServiceSpy.setXMLEntityAttackValidator(xmlEntityAttackValidator);
        doNothing().when(xmlEntityAttackValidator).validateAgainstXMLEntityAttacks(any(byte[].class));

        expectedException.expect(ValidationServiceException.class);
        dataFilesServiceSpy.getDataFiles(dataFilesDocument);

    }

    @Test
    public void validatingADDOCWithMalformedBytesResultsInMalformedDocumentException() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(VALID_DDOC_2_SIGNATURES);
        validationDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));
        expectedException.expect(MalformedDocumentException.class);
        validationService.validateDocument(validationDocument);
    }

    @Test
    public void ddocValidationResultShouldIncludeValidationReportPOJO() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures()).getSimpleReport();
        assertNotNull(validationResult2Signatures);
    }

    @Test
    public void vShouldIncludeSignatureFormWithCorrectPrefixAndVersion() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures()).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.3", validationResult2Signatures.getValidationConclusion().getSignatureForm());
    }

    @Test
    public void vShouldIncludeRequiredFields() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures()).getSimpleReport();
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
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures()).getSimpleReport();
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
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures()).getSimpleReport();
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
        SimpleReport report = validationService.validateDocument(buildValidationDocument(DATAFILE_XMLNS_MISSING)).getSimpleReport();
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(validationConclusion.getSignaturesCount(), validationConclusion.getValidSignaturesCount());
        SignatureValidationData signature = validationConclusion.getSignatures().get(0);
        assertTrue(signature.getErrors().isEmpty());
        assertTrue(signature.getWarnings().size() == 1);
        assertEquals("Bad digest for DataFile: D0 alternate digest matches!", signature.getWarnings().get(0).getContent());
    }

    @Test
    public void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode13Format() throws Exception {
        SimpleReport report = validationService.validateDocument(buildValidationDocument(DDOC_1_3_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.3_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    @Ignore("SIVARIA2-126")
    public void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode10Format() throws Exception {
        SimpleReport report = validationService.validateDocument(buildValidationDocument(DDOC_1_0_HASHCODE)).getSimpleReport();
        assertEquals("DIGIDOC_XML_1.0_hashcode", report.getValidationConclusion().getSignatureForm());
    }

    @Test
    public void reportShouldHaveHashcodeSingnatureFormSuffixWhenValidatingDdocHashcode12Format() throws Exception {
        SimpleReport report = validationService.validateDocument(buildValidationDocument(DDOC_1_2_HASHCODE)).getSimpleReport();
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
    public void digiDocExceptionIsError() throws Exception {
        ArrayList errors = getJdigiDocExceptionArrayList(DigiDocException.ERR_READ_CERT);
        boolean hasErrors = validationService.hasNonWarningErrs(getSampleSignedDoc(), errors);
        Assert.assertTrue(hasErrors);
    }

    @Test
    public void weakDigestIsWarning() throws DigiDocException {
        ArrayList errors = getJdigiDocExceptionArrayList(DigiDocException.WARN_WEAK_DIGEST);
        boolean hasErrors = validationService.hasNonWarningErrs(getSampleSignedDoc(), errors);
        Assert.assertFalse(hasErrors);
    }

    @Test
    public void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() throws Exception {
        expectedException.expect(InvalidPolicyException.class);
        validateWithPolicy("non-existing-policy");
    }

    @Test
    @PrepareForTest(ConfigManager.class)
    public void validationFailsWithExceptionWillThrowValidationServiceException() throws Exception {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setBytes("".getBytes());

        mockStatic(ConfigManager.class);
        ConfigManager configManager = mock(ConfigManager.class);
        DigiDocFactory digiDocFactory = mock(DigiDocFactory.class);


        given(configManager.getDigiDocFactory()).willReturn(digiDocFactory);
        given(ConfigManager.instance()).willReturn(configManager);
        when(digiDocFactory.readSignedDocFromStreamOfType(any(ByteArrayInputStream.class), anyBoolean(), anyList())).thenThrow(new DigiDocException(101, "Testing error", new Exception()));

        DDOCValidationService validationServiceSpy = spy(new DDOCValidationService());
        validationServiceSpy.setSignaturePolicyService(signaturePolicyService);

        XMLEntityAttackValidator xmlEntityAttackValidator = spy(new XMLEntityAttackValidator());
        validationServiceSpy.setXMLEntityAttackValidator(xmlEntityAttackValidator);
        doNothing().when(xmlEntityAttackValidator).validateAgainstXMLEntityAttacks(any(byte[].class));

        expectedException.expect(ValidationServiceException.class);
        validationServiceSpy.validateDocument(validationDocument);
    }

    private ArrayList getJdigiDocExceptionArrayList(int exceptionCode) {
        ArrayList errors = new ArrayList<>();
        DigiDocException exception = new DigiDocException(exceptionCode, "", new RuntimeException());
        errors.add(exception);
        return errors;
    }

    private SignedDoc getSampleSignedDoc() throws DigiDocException {
        SignedDoc signedDoc = new SignedDoc();
        signedDoc.setFormat("DIGIDOC-XML");
        return signedDoc;
    }

    private SimpleReport validateWithPolicy(String policyName) throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(VALID_DDOC_2_SIGNATURES);
        validationDocument.setSignaturePolicy(policyName);
        return validationService.validateDocument(validationDocument).getSimpleReport();
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
}
