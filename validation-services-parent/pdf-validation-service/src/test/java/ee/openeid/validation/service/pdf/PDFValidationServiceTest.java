/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.tsl.CustomCertificatesLoader;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.TSLValidationJobFactory;
import ee.openeid.tsl.configuration.TSLLoaderConfiguration;
import ee.openeid.validation.service.pdf.configuration.PDFSignaturePolicyProperties;
import ee.openeid.validation.service.pdf.configuration.PDFValidationServiceConfiguration;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest(classes = {PDFValidationServiceTest.TestConfiguration.class})
@RunWith(SpringRunner.class)
public class PDFValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";

    PDFValidationService validationService;

    private ConstraintLoadingSignaturePolicyService signaturePolicyService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private PDFSignaturePolicyProperties policySettings;

    @Before
    public void setUp() {
        validationService = new PDFValidationService();
        validationService.setCertificateVerifier(certificateVerifier);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
    }

    @Test
    public void testConfiguration() {
        assertNotNull(certificateVerifier);
        assertTrue(certificateVerifier instanceof CommonCertificateVerifier);
        assertEquals(2, signaturePolicyService.getSignaturePolicies().size());
        assertNotNull(signaturePolicyService.getPolicy(null));
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

    @Import({
        TSLLoaderConfiguration.class,
        PDFValidationServiceConfiguration.class
    })
    public static class TestConfiguration {
        @Bean
        public TSLLoader tslLoader() {
            return new TSLLoader();
        }

        @Bean
        public CustomCertificatesLoader customCertificatesLoader() {
            return new CustomCertificatesLoader();
        }

        @Bean
        public TSLValidationJobFactory tslValidationJobFactory() {
            return new TSLValidationJobFactory();
        }
    }


}
