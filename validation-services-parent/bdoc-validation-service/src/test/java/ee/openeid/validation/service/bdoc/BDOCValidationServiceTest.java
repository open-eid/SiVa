package ee.openeid.validation.service.bdoc;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import org.digidoc4j.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class BDOCValidationServiceTest {

    private static BDOCValidationService validationService = new BDOCValidationServiceSpy();
    private static QualifiedReport validationResult2Signatures;
    private static QualifiedReport validationResultSignedNoManifest;

    @BeforeClass
    public static void setUpClass() throws Exception {
        validationResult2Signatures = validationService.validateDocument(bdocValid2Signatures());
        validationResultSignedNoManifest = validationService.validateDocument(bdocTSIndeterminateNoManifest());
    }

    private static ValidationDocument bdocValid2Signatures() throws Exception {
        return BDOCTestUtils.buildValidationDocument(BDOCTestUtils.VALID_BDOC_TM_2_SIGNATURES);
    }

    private static ValidationDocument bdocTSIndeterminateNoManifest() throws Exception {
        return BDOCTestUtils.buildValidationDocument(BDOCTestUtils.TS_NO_MANIFEST);
    }

    @Test
    public void validatingABDOCWithMalformedBytesResultsInMalformedDocumentException() throws Exception {
        ValidationDocument validationDocument = BDOCTestUtils.buildValidationDocument(BDOCTestUtils.TS_NO_MANIFEST);
        validationDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));
        String message = "";
        try {
            validationService.validateDocument(validationDocument);
        } catch (MalformedDocumentException e) {
            message = e.getMessage();
        }
        assertEquals("the document is malformed", message);
    }

    @Test
    public void bdocValidationResultShouldIncludeQualifiedReportPOJO() throws Exception {
        assertNotNull(validationResult2Signatures);
    }

    @Test
    public void qualifiedReportShouldIncludeRequiredFields() throws Exception {
        assertNotNull(validationResult2Signatures.getPolicy());
        assertNotNull(validationResult2Signatures.getValidationTime());
        assertEquals(BDOCTestUtils.VALID_BDOC_TM_2_SIGNATURES, validationResult2Signatures.getDocumentName());
        assertTrue(validationResult2Signatures.getSignatures().size() == 2);
        assertTrue(validationResult2Signatures.getValidSignaturesCount() == 2);
        assertTrue(validationResult2Signatures.getSignaturesCount() == 2);
    }

    @Test
    public void qualifiedReportShouldHaveCorrectSignatureValidationDataForSignature1() {
        SignatureValidationData sig1 = validationResult2Signatures.getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S0"))
                .findFirst()
                .get();

        assertEquals("XAdES_BASELINE_LT_TM", sig1.getSignatureFormat());
        assertEquals("QES", sig1.getSignatureLevel());
        assertEquals("JUHANSON,ALLAN,38608014910", sig1.getSignedBy());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig1.getIndication());
        assertNull(sig1.getSubIndication());
        assertTrue(sig1.getErrors().size() == 0);
        assertTrue(sig1.getWarnings().size() == 0);
        assertTrue(sig1.getSignatureScopes().size() == 1);
        SignatureScope scope = sig1.getSignatureScopes().get(0);
        assertEquals("chrome-signing.log", scope.getName());
        assertEquals("Full document", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2016-05-04T08:43:55Z", sig1.getClaimedSigningTime());
        assertEquals("2016-05-04T08:44:23Z", sig1.getInfo().getBestSignatureTime());
    }

    @Test
    public void qualifiedReportShouldHaveCorrectSignatureValidationDataForSignature2() {
        SignatureValidationData sig2 = validationResult2Signatures.getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S1"))
                .findFirst()
                .get();

        assertEquals("XAdES_BASELINE_LT_TM", sig2.getSignatureFormat());
        assertEquals("QES", sig2.getSignatureLevel());
        assertEquals("VOLL,ANDRES,39004170346", sig2.getSignedBy());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig2.getIndication());
        assertNull(sig2.getSubIndication());
        assertTrue(sig2.getErrors().size() == 0);
        assertTrue(sig2.getWarnings().size() == 0);
        assertTrue(sig2.getSignatureScopes().size() == 1);
        SignatureScope scope = sig2.getSignatureScopes().get(0);
        assertEquals("chrome-signing.log", scope.getName());
        assertEquals("Full document", scope.getContent());
        assertEquals("FullSignatureScope", scope.getScope());
        assertEquals("2016-05-04T11:14:23Z", sig2.getClaimedSigningTime());
        assertEquals("2016-05-04T11:14:32Z", sig2.getInfo().getBestSignatureTime());
    }

    @Test
    public void reportForBdocTSWithUntrustedRevocationDataShouldContainError() {
        assertTrue(validationResultSignedNoManifest.getValidSignaturesCount() == 0);
        SignatureValidationData sig = validationResultSignedNoManifest.getSignatures().get(0);
        assertTrue(sig.getErrors().size() == 1);

        Error error = sig.getErrors().get(0);
        assertEquals("BBB_XCV_IRDTFC_ANS", error.getNameId());
        assertEquals(sig.getIndication(), SignatureValidationData.Indication.INDETERMINATE.toString());
    }

    private static class BDOCValidationServiceSpy extends BDOCValidationService {

        @Override
        public void initConfiguration() {
            super.setConfiguration(new Configuration(Configuration.Mode.PROD));
        }
    }
}
