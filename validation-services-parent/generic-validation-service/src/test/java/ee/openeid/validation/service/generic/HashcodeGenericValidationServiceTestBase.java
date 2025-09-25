/*
 * Copyright 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.validation.service.generic.configuration.properties.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPSourceFactory;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {GenericValidationServiceTestBase.TestConfiguration.class})
abstract class HashcodeGenericValidationServiceTestBase {

    protected static final String TEST_FILES_LOCATION = "test-files/";

    protected HashcodeGenericValidationService validationService;
    protected ConstraintLoadingSignaturePolicyService signaturePolicyService;
    @Autowired
    protected TrustedListsCertificateSource trustedListsCertificateSource;
    @Autowired
    protected GenericSignaturePolicyProperties policySettings;
    @Autowired
    protected ContainerValidatorFactory containerValidatorFactory;
    @Autowired
    protected OCSPSourceFactory ocspSourceFactory;

    @BeforeEach
    public void setUp() {
        validationService = new HashcodeGenericValidationService();
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));

        validationService.setContainerValidatorFactory(containerValidatorFactory);
        validationService.setOcspSourceFactory(ocspSourceFactory);
    }

    protected static ValidationDocument buildValidationDocument(String testFile, Datafile... datafiles) {
        return buildValidationDocument(testFile, List.of(datafiles));
    }

    protected static ValidationDocument buildValidationDocument(String testFile, List<Datafile> datafiles) {
        ValidationDocument validationDocument = DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();

        validationDocument.setDatafiles(datafiles);
        validationDocument.setSignaturePolicy("POLv4");
        return validationDocument;
    }

}
