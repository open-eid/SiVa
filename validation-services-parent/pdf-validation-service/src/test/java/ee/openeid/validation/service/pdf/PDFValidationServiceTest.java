package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = PDFValidationServiceTest.TestTslLoaderConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PDFValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    PDFValidationService validationService;

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Before
    public void setUp() {
        validationService = new PDFValidationService();
        validationService.setCertificateVerifier(certificateVerifier);
    }

    @Test
    public void testConfiguration() {
        assertNotNull(certificateVerifier);
        assertTrue(certificateVerifier instanceof CommonCertificateVerifier);
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


    @Import(TSLLoaderConfiguration.class)
    public static class TestTslLoaderConfiguration {

        @Bean
        public YamlPropertiesFactoryBean yamlProperties() {
            YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
            yamlProperties.setResources(new ClassPathResource("test-tsl-loader-props.yml"));
            return yamlProperties;
        }

        @Bean
        public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer(YamlPropertiesFactoryBean yamlProperties) {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setProperties(yamlProperties.getObject());
            return ppc;
        }

        @Bean TSLLoader tslLoader() {
            return new TSLLoader();
        }

    }


}
