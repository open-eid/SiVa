package ee.openeid.validation.service.generic;

import ee.openeid.validation.service.generic.validator.CompositeOCSPSource;
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
