package ee.openeid.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.validation.service.bdoc.testutils.DummyValidationDocumentBuilder;
import org.digidoc4j.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class BDOCValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String VALID_BDOC_TM_2_SIGNATURES = "bdoc_tm_valid_2_signatures.bdoc";
    private static final String TS_NO_MANIFEST = "asic-e-baseline-lt_allan_live_no_manifest.bdoc";

    private static BDOCValidationService validationService = new BDOCValidationServiceSpy();
    private static BDOCValidationResult validationResult2Signatures;
    private static BDOCValidationResult validationResultSignedNoManifest;

    @BeforeClass
    public static void setUpClass() throws Exception {
        validationResult2Signatures = validationService.validateDocument(bdocValid2Signatures());
        validationResultSignedNoManifest = validationService.validateDocument(bdocTSIndeterminateNoManifest());
    }

    @Test
    public void callingValidateWithValidBdocShouldReturnBDOCValidationResultWithReportsIncluded() throws Exception {
        assertNotNull(validationResult2Signatures.getSimpleReport());
        assertNotNull(validationResult2Signatures.getDetailedReport());
    }

    @Test
    public void bdocValidationResultShouldIncludeQualifiedReportPOJO() throws Exception {
        assertNotNull(validationResult2Signatures.getQualifiedReport());
    }

    @Test
    public void qualifiedReportShouldIncludeRequiredFields() throws Exception {
        QualifiedReport report = validationResult2Signatures.getQualifiedReport();
        assertNotNull(report.getPolicy());
        assertNotNull(report.getValidationTime());
        assertEquals(VALID_BDOC_TM_2_SIGNATURES, report.getDocumentName());
        assertTrue(report.getSignatures().size() == 2);
        assertTrue(report.getValidSignaturesCount() == 2);
        assertTrue(report.getSignaturesCount() == 2);
    }

    @Test
    public void qualifiedReportShouldHaveCorrectSignatureValidationDataForSignature1() {
        QualifiedReport report = validationResult2Signatures.getQualifiedReport();

        SignatureValidationData sig1 = report.getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S0"))
                .findFirst()
                .get();

        assertEquals("XAdES_BASELINE_LT_TM", sig1.getSignatureFormat());
        assertEquals("QES", sig1.getSignatureLevel());
        assertEquals("JUHANSON,ALLAN,38608014910", sig1.getSignedBy());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig1.getIndication());
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
        SignatureValidationData sig2 = validationResult2Signatures.getQualifiedReport().getSignatures()
                .stream()
                .filter(sig -> sig.getId().equals("S1"))
                .findFirst()
                .get();

        assertEquals("XAdES_BASELINE_LT_TM", sig2.getSignatureFormat());
        assertEquals("QES", sig2.getSignatureLevel());
        assertEquals("VOLL,ANDRES,39004170346", sig2.getSignedBy());
        assertEquals(SignatureValidationData.Indication.TOTAL_PASSED.toString(), sig2.getIndication());
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
        QualifiedReport report = validationResultSignedNoManifest.getQualifiedReport();
        assertTrue(report.getValidSignaturesCount() == 0);
        SignatureValidationData sig = report.getSignatures().get(0);
        assertTrue(sig.getErrors().size() == 1);

        Error error = sig.getErrors().get(0);
        assertEquals("BBB_XCV_IRDTFC_ANS", error.getNameId());
        assertEquals(sig.getIndication(), SignatureValidationData.Indication.INDETERMINATE.toString());
    }

    private static ValidationDocument bdocValid2Signatures() throws Exception {
        return buildValidationDocument(VALID_BDOC_TM_2_SIGNATURES);
    }

    private static ValidationDocument bdocTSIndeterminateNoManifest() throws Exception {
        return buildValidationDocument(TS_NO_MANIFEST);
    }

    private static ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

    private static class BDOCValidationServiceSpy extends BDOCValidationService {

        @Override
        public void initConfiguration() {
            super.setConfiguration(new Configuration(Configuration.Mode.PROD));
        }
    }
}
