package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.tsl.CustomCertificatesLoader;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.pdf.configuration.PDFPolicySettings;
import ee.openeid.validation.service.pdf.configuration.PDFValidationServiceConfiguration;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@SpringBootTest(classes = PDFValidationServiceTest.TestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PDFValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    PDFValidationService validationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private PDFPolicySettings policySettings;

    @Before
    public void setUp() {
        validationService = new PDFValidationService();
        validationService.setCertificateVerifier(certificateVerifier);
        validationService.setPolicySettings(policySettings);
    }

    @Test
    public void testConfiguration() {
        assertNotNull(certificateVerifier);
        assertTrue(certificateVerifier instanceof CommonCertificateVerifier);
        assertEquals("/pdf_constraint.xml", policySettings.getPolicy());
    }

    @Test
    public void givenInvalidPDFFileShouldThrowServiceException() throws Exception {
        expectedException.expect(ValidationServiceException.class);
        validationService.validateDocument(new ValidationDocument());
    }

    void assertNoErrorsOrWarnings(SignatureValidationData signatureValidationData) {
        assertNoErrors(signatureValidationData);
        assertNoWarnings(signatureValidationData);
    }

    void assertNoErrors(SignatureValidationData signatureValidationData) {
        assertTrue(signatureValidationData.getErrors().size() == 0);
    }

    void assertNoWarnings(SignatureValidationData signatureValidationData) {
        assertTrue(signatureValidationData.getWarnings().size() == 0);
    }

    ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }


    @Import({TSLLoaderConfiguration.class, PDFValidationServiceConfiguration.class})
    public static class TestConfiguration {

        @Bean
        public YamlPropertiesFactoryBean yamlProperties() {
            YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
            yamlProperties.setResources(new ClassPathResource("application-test.yml"));
            return yamlProperties;
        }

        @Bean
        public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer(YamlPropertiesFactoryBean yamlProperties) {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setProperties(yamlProperties.getObject());
            return ppc;
        }

        @Bean
        public TSLLoader tslLoader() {
            return new TSLLoader();
        }

        @Bean
        public CustomCertificatesLoader customCertificatesLoader() {
            return new CustomCertificatesLoader();
        }

    }


}
