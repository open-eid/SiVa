/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.tsl.TrustedListsCertificateSourceHolder;
import ee.openeid.tsl.configuration.AlwaysFailingCRLSource;
import ee.openeid.tsl.configuration.AlwaysFailingOCSPSource;
import ee.openeid.validation.service.generic.validator.container.ContainerValidator;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

// Pins how the validator each request gets is wired: which trust and revocation sources the certificate
// verifier holds, and that the trust source is resolved from the holder per request.
// A failure after a DSS bump means re-read the wiring and decide again what it should be; it is not a
// prompt to update the expected values until the test passes.
@ExtendWith(MockitoExtension.class)
class GenericValidationServiceVerifierTest {

    private final TrustedListsCertificateSourceHolder trustedListsCertificateSource = new TrustedListsCertificateSourceHolder();
    private final OCSPSource ocspSource = new AlwaysFailingOCSPSource();

    @Mock
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    @Mock
    private SignedDocumentValidator validator;

    private GenericValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new GenericValidationService(
                trustedListsCertificateSource,
                signaturePolicyService,
                new ReportConfigurationProperties(false),
                new ProxyConfig(),
                (reports, document) -> ContainerValidator.NO_OP_INSTANCE,
                () -> ocspSource
        );
    }

    @Test
    void verifierHoldsTheCurrentTrustedListSource() {
        CommonCertificateVerifier verifier = createVerifier();

        assertThat(verifier.getTrustedCertSources().getSources(), hasItem(sameInstance(trustedListsCertificateSource.current())));
    }

    @Test
    void verifierResolvesTheTrustedListSourceFromTheHolderPerRequest() {
        TrustedListsCertificateSource previous = trustedListsCertificateSource.current();
        CommonCertificateVerifier before = createVerifier();
        TrustedListsCertificateSource published = new TrustedListsCertificateSource();
        trustedListsCertificateSource.publish(published);

        CommonCertificateVerifier after = createVerifier();

        assertThat(before.getTrustedCertSources().getSources(), hasItem(sameInstance(previous)));
        assertThat(after.getTrustedCertSources().getSources(), hasItem(sameInstance(published)));
        assertThat(after.getTrustedCertSources().getSources(), not(hasItem(sameInstance(previous))));
    }

    @Test
    void verifierUsesTheConfiguredRevocationSources() {
        CommonCertificateVerifier verifier = createVerifier();

        assertThat(verifier.getOcspSource(), sameInstance(ocspSource));
        assertThat(verifier.getCrlSource(), instanceOf(AlwaysFailingCRLSource.class));
        assertThat(verifier.isCheckRevocationForUntrustedChains(), is(false));
    }

    private CommonCertificateVerifier createVerifier() {
        createValidator();
        ArgumentCaptor<CertificateVerifier> certificateVerifier = ArgumentCaptor.forClass(CertificateVerifier.class);
        verify(validator, atLeastOnce()).setCertificateVerifier(certificateVerifier.capture());
        return (CommonCertificateVerifier) certificateVerifier.getValue();
    }

    private void createValidator() {
        try (MockedStatic<SignedDocumentValidator> fromDocument = Mockito.mockStatic(SignedDocumentValidator.class)) {
            fromDocument.when(() -> SignedDocumentValidator.fromDocument(any(DSSDocument.class))).thenReturn(validator);
            validationService.createValidatorFromDocument(document());
        }
    }

    private static ValidationDocument document() {
        ValidationDocument document = new ValidationDocument();
        document.setName("document.asice");
        document.setBytes(new byte[0]);
        return document;
    }

}
