package ee.openeid.validation.service.bdoc;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static ee.openeid.validation.service.bdoc.BDOCTestUtils.VALID_BDOC_TM_2_SIGNATURES;
import static ee.openeid.validation.service.bdoc.BDOCTestUtils.buildValidationDocument;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BDOCValidationService.class)
@ActiveProfiles("test")
public class BDOCValidationServiceIntegrationTest {
    
    @Autowired
    private BDOCValidationService bdocValidationService;

    @MockBean
    private TrustedListsCertificateSource certificateSource;

    @Test
    public void validateDocumentWithNullConfiguration() throws Exception {
        bdocValidationService.setConfiguration(null);
        QualifiedReport qualifiedReport = bdocValidationService.validateDocument(buildValidationDocument(VALID_BDOC_TM_2_SIGNATURES));

        assertEquals(Integer.valueOf(2), qualifiedReport.getSignaturesCount());
        Mockito.verify(certificateSource).getCertificates();
    }
}