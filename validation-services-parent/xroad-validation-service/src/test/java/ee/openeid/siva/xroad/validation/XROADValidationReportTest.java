package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Before;
import org.junit.Test;

import static ee.openeid.siva.xroad.validation.XROADTestUtils.*;
import static org.junit.Assert.assertEquals;


public class XROADValidationReportTest {

    private XROADValidationService validationService;

    @Before
    public void setUp() {
        validationService = initializeXROADValidationService();
    }

    @Test
    public void ValidatingXRoadSimpleContainerShouldHaveOnlyTheCNFieldOfTheSingersCerificateAsSignedByFieldInQualifiedReport() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(XROAD_SIMPLE);
        QualifiedReport report = validationService.validateDocument(validationDocument);
        assertEquals("Riigi Infosüsteemi Amet", report.getSignatures().get(0).getSignedBy());
    }

    @Test
    public void validationReportForXroadBatchSignatureShouldHaveCorrectSignatureForm() throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(XROAD_BATCHSIGNATURE);
        QualifiedReport report = validationService.validateDocument(validationDocument);
        assertEquals("ASiC_E_batchsignature", report.getSignatureForm());
    }

    @Test
    public void validationReportForXROADSimpleAndPatchSignatureShouldHaveEmptySignatureLevel() throws Exception {
        QualifiedReport report1= validationService.validateDocument(buildValidationDocument(XROAD_SIMPLE));
        QualifiedReport report2= validationService.validateDocument(buildValidationDocument(XROAD_BATCHSIGNATURE));
        assertEquals("", report1.getSignatures().get(0).getSignatureLevel());
        assertEquals("", report2.getSignatures().get(0).getSignatureLevel());
    }

    @Test
    public void signatureFormInReportShouldBeAsicEWhenValidatingXROADSimpleContainer() throws Exception {
        QualifiedReport report= validationService.validateDocument(buildValidationDocument(XROAD_SIMPLE));
        assertEquals("ASiC_E", report.getSignatureForm());
    }
}
