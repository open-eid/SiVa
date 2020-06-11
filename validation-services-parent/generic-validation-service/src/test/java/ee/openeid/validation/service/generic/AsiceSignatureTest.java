package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.validation.service.generic.configuration.GenericSignaturePolicyProperties;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {PDFValidationServiceTest.TestConfiguration.class})
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AsiceSignatureTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    GenericValidationService validationService;

    private ConstraintLoadingSignaturePolicyService signaturePolicyService;

    @Autowired
    private TrustedListsCertificateSource trustedListsCertificateSource;

    @Autowired
    private GenericSignaturePolicyProperties policySettings;

    @Before
    public void setUp() {
        validationService = new GenericValidationService();
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));
    }

    @Test
    public void populatesTimeAssertionMessageImprintForTMAsice() {
        SimpleReport simpleReport = validationService
                .validateDocument(buildValidationDocument("bdoc_tm_valid_2_signatures.asice"))
                .getSimpleReport();
        String imprint1 = simpleReport.getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint();
        String imprint2 = simpleReport.getValidationConclusion().getSignatures().get(1).getInfo().getTimeAssertionMessageImprint();

        Assert.assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIGKrO2Grf+WLkmOnj9QQbCXAa2A3881D9PUIOk0M7Nm6", imprint1);
        Assert.assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIDDnPj4HDgSwi+tj/s30GshbBf1L8Nqnt2GMK+6VnEdt", imprint2);
    }

    @Test
    public void populatesTimeAssertionMessageImprintForTSAsice() {
        SimpleReport simpleReport = validationService
                .validateDocument(buildValidationDocument("TS.asice"))
                .getSimpleReport();
        String imprint = simpleReport.getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint();

        Assert.assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIE541TO5ZHHgKv60XxTXJX0Qg04pjs4uN8bELnDUDFp1", imprint);
    }

    @Test
    public void timeAssertionMessageImprintIsEmptyForMissingTimestamp() {
        SimpleReport simpleReport = validationService
                .validateDocument(buildValidationDocument("asice_TS_missing_timestamp_signature.xml"))
                .getSimpleReport();
        String imprint = simpleReport.getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint();

        assertEquals("", imprint);
    }

    @Test
    public void timeAssertionMessageImprintIsEmptyForMissingOcspData() {
        SimpleReport simpleReport = validationService
                .validateDocument(buildValidationDocument("asice_TM_missing_ocsp_signature.xml"))
                .getSimpleReport();
        String imprint = simpleReport.getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint();

        assertEquals("", imprint);
    }

    static ValidationDocument buildValidationDocument(String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

}
