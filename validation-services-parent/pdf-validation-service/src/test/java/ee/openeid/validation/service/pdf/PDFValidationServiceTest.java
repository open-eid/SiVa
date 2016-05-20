package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.tsl.CustomTSLValidationJob;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration( classes = PDFValidationServiceTest.TestTslLoaderConfiguration.class)
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

        @Value("${trusted.list.source.lotlUrl}")
        String lotlUrl;

        @Value("${trusted.list.source.lotlCode}")
        String lotlCode;

        @Bean
        public TSLValidationJob tslValidationJob(CommonsDataLoader dataLoader, TSLRepository tslRepository, KeyStoreCertificateSource keyStoreCertificateSource) {
            CustomTSLValidationJob tslValidationJob = new CustomTSLValidationJob();
            tslValidationJob.setDataLoader(dataLoader);
            tslValidationJob.setRepository(tslRepository);
            tslValidationJob.setLotlUrl(lotlUrl);
            tslValidationJob.setLotlCode(lotlCode);
            tslValidationJob.setDssKeyStore(keyStoreCertificateSource);
            tslValidationJob.setFilterTerritories(Collections.singletonList("EE"));
            tslValidationJob.setCheckLOTLSignature(true);
            tslValidationJob.setCheckTSLSignatures(true);
            return tslValidationJob;
        }

        @Bean
        public YamlPropertiesFactoryBean yamlProperties() {
            YamlPropertiesFactoryBean  yamlProperties = new YamlPropertiesFactoryBean();
            yamlProperties.setResources(new ClassPathResource("test-tsl-loader-props.yml"));
            return yamlProperties;
        }

        @Bean
        public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer(YamlPropertiesFactoryBean yamlProperties) {
            PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
            ppc.setProperties(yamlProperties.getObject());
            return ppc;
        }
    }


}
