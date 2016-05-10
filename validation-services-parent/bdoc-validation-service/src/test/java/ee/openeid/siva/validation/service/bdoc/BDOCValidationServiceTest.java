package ee.openeid.siva.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.bdoc.report.qualified.QualifiedReport;
import ee.openeid.siva.validation.service.bdoc.testutils.DummyValidationDocumentBuilder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BDOCValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";
    private static final String VALID_BDOC_TM_1_SIGNATURE = "bdoc_tm_valid_1_signature.bdoc";
    private static final String VALID_BDOC_TM_2_SIGNATURES = "bdoc_tm_valid_2_signatures.bdoc";

    private BDOCValidationService validationService = new BDOCValidationService();

    @Test
    public void callingValidateWithValidBdocShouldReturnBDOCValidationResultWithReportsIncluded() throws Exception {
        BDOCValidationResult validationResult = validationService.validateDocument(bdocValid2Signatures());
        assertNotNull(validationResult.getSimpleReport());
        assertNotNull(validationResult.getDetailedReport());

        System.out.println(validationResult.getSimpleReport());
    }

    @Test
    public void bdocValidationResultShouldIncludeQualifiedReportPOJO() throws Exception {
        BDOCValidationResult validationResult = validationService.validateDocument(bdocValid2Signatures());
        assertNotNull(validationResult.getQualifiedReport());
    }

    @Test
    public void qualifiedReportShouldIncludeRequiredNonSignatureProperties() throws Exception {
        QualifiedReport report = validationService.validateDocument(bdocValid2Signatures()).getQualifiedReport();
        assertNotNull(report.getPolicy());
        assertNotNull(report.getSignaturesCount());
        assertNotNull(report.getValidSignaturesCount());
        assertNotNull(report.getValidationTime());
        System.out.println(report);
    }

    private ValidationDocument bdocValid2Signatures() throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + VALID_BDOC_TM_2_SIGNATURES)
                .withName(VALID_BDOC_TM_2_SIGNATURES)
                .build();
    }
}
