/*
 * Copyright 2023 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.ocsp;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiPredicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CompositeOCSPSourceTest {

    @Mock
    private OCSPSource ocspSource;
    @Mock
    private BiPredicate<CertificateToken, CertificateToken> ocspRequestRequirementPredicate;
    @InjectMocks
    private CompositeOCSPSource compositeOCSPSource;

    @Mock
    private CertificateToken certificateToken;
    @Mock
    private CertificateToken issuerCertificateToken;

    @Test
    void getRevocationToken_WhenPredicateReturnsTrue_ThenOCSPTokenIsRequestedFromNestedOCSPSource() {
        doReturn(true).when(ocspRequestRequirementPredicate).test(certificateToken, issuerCertificateToken);
        OCSPToken ocspToken = Mockito.mock(OCSPToken.class);
        doReturn(ocspToken).when(ocspSource).getRevocationToken(certificateToken, issuerCertificateToken);

        OCSPToken result = compositeOCSPSource.getRevocationToken(certificateToken, issuerCertificateToken);

        assertThat(result, Matchers.sameInstance(ocspToken));
        verifyNoMoreInteractions(ocspSource, ocspRequestRequirementPredicate);
        verifyNoInteractions(certificateToken, issuerCertificateToken, ocspToken);
    }

    @Test
    void getRevocationToken_WhenPredicateReturnsFalse_ThenReturnNull() {
        doReturn(false).when(ocspRequestRequirementPredicate).test(certificateToken, issuerCertificateToken);

        OCSPToken result = compositeOCSPSource.getRevocationToken(certificateToken, issuerCertificateToken);

        assertThat(result, Matchers.nullValue());
        verifyNoMoreInteractions(ocspRequestRequirementPredicate);
        verifyNoInteractions(certificateToken, issuerCertificateToken);
    }
}
