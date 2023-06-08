package ee.openeid.validation.service.generic;

import ee.openeid.validation.service.generic.validator.OCSPRequestPredicate;
import eu.europa.esig.dss.model.x509.CertificateToken;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.cert.X509Certificate;
import java.util.function.BiFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class OCSPRequestPredicateTest {

    @Mock
    private BiFunction<X509Certificate, ASN1ObjectIdentifier, String> subjectDnAttributeExtractor;

    @InjectMocks
    private OCSPRequestPredicate ocspRequestPredicate;

    @Mock
    private CertificateToken certificateToken;
    @Mock
    private CertificateToken issuerCertificateToken;
    @Mock
    private X509Certificate x509Certificate;

    @Test
    void test_WhenDistinguishedNameUtilReturnsEE_ThenReturnFalse() {
        doReturn(x509Certificate).when(certificateToken).getCertificate();
        doReturn("EE").when(subjectDnAttributeExtractor).apply(x509Certificate, BCStyle.C);

        boolean result = ocspRequestPredicate.test(certificateToken, issuerCertificateToken);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(certificateToken);
        verifyNoInteractions(issuerCertificateToken);
    }

    @Test
    void test_WhenDistinguishedNameUtilReturnsNull_ThenReturnTrue() {
        doReturn(x509Certificate).when(certificateToken).getCertificate();
        doReturn(null).when(subjectDnAttributeExtractor).apply(x509Certificate, BCStyle.C);

        boolean result = ocspRequestPredicate.test(certificateToken, issuerCertificateToken);

        assertThat(result, Matchers.is(true));
        verifyNoMoreInteractions(certificateToken);
        verifyNoInteractions(issuerCertificateToken);
    }

    @ParameterizedTest
    @ValueSource(strings = {"LV", "", " ", ".!/@*"})
    void test_WhenDistinguishedNameUtilReturnsStringThatIsNotEE_ThenReturnTrue(String countryValue) {
        doReturn(x509Certificate).when(certificateToken).getCertificate();
        doReturn(countryValue).when(subjectDnAttributeExtractor).apply(x509Certificate, BCStyle.C);

        boolean result = ocspRequestPredicate.test(certificateToken, issuerCertificateToken);

        assertThat(result, Matchers.is(true));
        verifyNoMoreInteractions(certificateToken);
        verifyNoInteractions(issuerCertificateToken);
    }
}
