package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.validation.service.generic.configuration.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(classes = {PDFValidationServiceTest.TestConfiguration.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AsiceSignatureTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    GenericValidationService validationService;

    private ConstraintLoadingSignaturePolicyService signaturePolicyService;

    @Autowired
    private TrustedListsCertificateSource trustedListsCertificateSource;

    @Autowired
    private GenericSignaturePolicyProperties policySettings;

    @Autowired
    private ContainerValidatorFactory containerValidatorFactory;

    @BeforeEach
    public void setUp() {
        validationService = new GenericValidationService();
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        validationService.setContainerValidatorFactory(containerValidatorFactory);
    }

    @Test
    void populatesTimeAssertionMessageImprintForTMAsice() {
        SimpleReport simpleReport = validationService
                .validateDocument(buildValidationDocument("bdoc_tm_valid_2_signatures.asice"))
                .getSimpleReport();
        String imprint1 = simpleReport.getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint();
        String imprint2 = simpleReport.getValidationConclusion().getSignatures().get(1).getInfo().getTimeAssertionMessageImprint();

        assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIGKrO2Grf+WLkmOnj9QQbCXAa2A3881D9PUIOk0M7Nm6", imprint1);
        assertEquals("MDEwDQYJYIZIAWUDBAIBBQAEIDDnPj4HDgSwi+tj/s30GshbBf1L8Nqnt2GMK+6VnEdt", imprint2);
    }

    @ParameterizedTest
    @MethodSource("getFileNameAndExpectedResult")
    void populatesTimeAssertionMessageImprint(String fileName, String expectedResult) {
        SimpleReport simpleReport = validationService
                .validateDocument(buildValidationDocument(fileName))
                .getSimpleReport();
        String imprint = simpleReport.getValidationConclusion().getSignatures().get(0).getInfo().getTimeAssertionMessageImprint();

        assertEquals(expectedResult, imprint);
    }

    private static Stream<Arguments> getFileNameAndExpectedResult() {
        return Stream.of(
                arguments("TS.asice", "MDEwDQYJYIZIAWUDBAIBBQAEIE541TO5ZHHgKv60XxTXJX0Qg04pjs4uN8bELnDUDFp1"),
                arguments("asice_TS_missing_timestamp_signature.xml", ""),
                arguments("asice_TM_missing_ocsp_signature.xml", "")
        );
    }

    static ValidationDocument buildValidationDocument(String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

}
