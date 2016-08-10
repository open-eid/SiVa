package ee.openeid.validation.service.ddoc;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.security.*")
public class DDOCValidationServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String VALID_DDOC_2_SIGNATURES = "ddoc_valid_2_signatures.ddoc";

    private static DDOCValidationService validationService = new DDOCValidationService();

    private static QualifiedReport validationResult2Signatures;
    private Object lock = new Object();

    @BeforeClass
    public static void setUpClass() throws Exception {
        validationService.initConfig();
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

    @Test
    public void validatingADDOCWithMalformedBytesResultsInMalformedDocumentException() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(VALID_DDOC_2_SIGNATURES);
        validationDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));

        expectedException.expect(MalformedDocumentException.class);
        DDOCValidationService validationService = new DDOCValidationService();
        validationService.validateDocument(validationDocument);
    }

    @Test
    public void ddocValidationResultShouldIncludeQualifiedReportPOJO() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures());
        assertNotNull(validationResult2Signatures);
    }

    @Test
    public void qualifiedReportShouldIncludeSignatureFormWithCorrectPrefixAndVersion() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures());
        assertEquals("DIGIDOC_XML_1.3",validationResult2Signatures.getSignatures().get(0).getSignatureForm());
        assertEquals("DIGIDOC_XML_1.3",validationResult2Signatures.getSignatures().get(1).getSignatureForm());
    }

    @Test
    public void qualifiedReportShouldIncludeRequiredFields() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures());
        assertNotNull(validationResult2Signatures.getPolicy());
        assertNotNull(validationResult2Signatures.getValidationTime());
        assertEquals(VALID_DDOC_2_SIGNATURES, validationResult2Signatures.getDocumentName());
        assertTrue(validationResult2Signatures.getSignatures().size() == 2);
        assertTrue(validationResult2Signatures.getValidSignaturesCount() == 2);
        assertTrue(validationResult2Signatures.getSignaturesCount() == 2);
    }

    @Test
    public void qualifiedReportShouldHaveCorrectSignatureValidationDataForSignature1() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures());
        SignatureValidationData sig1 = validationResult2Signatures.getSignatures()
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
        assertEquals("Full document", scope.getContent());
        assertTrue(StringUtils.isEmpty(scope.getScope()));
        assertEquals("2005-02-11T16:23:21Z", sig1.getClaimedSigningTime());
        assertNotNull(sig1.getInfo());
        assertTrue(StringUtils.isEmpty(sig1.getInfo().getBestSignatureTime()));
    }

    @Test
    public void qualifiedReportShouldHaveCorrectSignatureValidationDataForSignature2() throws Exception {
        validationResult2Signatures = validationService.validateDocument(ddocValid2Signatures());
        SignatureValidationData sig2 = validationResult2Signatures.getSignatures()
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
        assertTrue(StringUtils.isEmpty(scope.getScope()));
        assertEquals("2009-02-13T09:22:49Z", sig2.getClaimedSigningTime());
        assertNotNull(sig2.getInfo());
        assertTrue(StringUtils.isEmpty(sig2.getInfo().getBestSignatureTime()));
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

            expectedException.expect(ValidationServiceException.class);
            validationService.validateDocument(validationDocument);

    }
}
