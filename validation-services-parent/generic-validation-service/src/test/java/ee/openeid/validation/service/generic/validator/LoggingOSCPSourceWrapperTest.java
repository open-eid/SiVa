package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.enumerations.CertificateStatus;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.spi.x509.revocation.OnlineRevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import helper.TestLog;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class LoggingOSCPSourceWrapperTest {

    private TestLog testLog;
    @Mock
    private OnlineRevocationSource<OCSP> ocspOnlineRevocationSource;
    @InjectMocks
    private LoggingOSCPSourceWrapper loggingOSCPSourceWrapper;
    @Mock
    private CertificateToken certificateToken;
    @Mock
    private CertificateToken issuerCertificateToken;

    @BeforeEach
    void setUp() {
        testLog = new TestLog(LoggingOSCPSourceWrapper.class);
    }

    @Test
    void getRevocationToken_WhenOnlineRevocationSourceThrowsException_LogsWarningAndReturnsNull() {
        Exception exception = new RuntimeException("Test exception message");
        doThrow(exception).when(ocspOnlineRevocationSource)
                .getRevocationTokenAndUrl(certificateToken, issuerCertificateToken);
        doReturn("test id").when(certificateToken)
                .getDSSIdAsString();

        OCSPToken result = loggingOSCPSourceWrapper.getRevocationToken(certificateToken, issuerCertificateToken);

        assertNull(result);
        testLog.verifyLogInOrder(Matchers.equalTo(
                "Failed to perform OCSP request for certificate token 'test id': Test exception message"
        ));
        verifyNoMoreInteractions(ocspOnlineRevocationSource, certificateToken);
        verifyNoInteractions(issuerCertificateToken);
    }

    @Test
    void getRevocationToken_WhenOnlineRevocationSourceReturnsNull_LogsWarningAndReturnsNull() {
        doReturn(null).when(ocspOnlineRevocationSource)
                .getRevocationTokenAndUrl(certificateToken, issuerCertificateToken);
        doReturn("test id").when(certificateToken)
                .getDSSIdAsString();

        OCSPToken result = loggingOSCPSourceWrapper.getRevocationToken(certificateToken, issuerCertificateToken);

        assertNull(result);
        testLog.verifyLogInOrder(Matchers.equalTo("No OCSP token found for certificate token 'test id'"));
        verifyNoMoreInteractions(ocspOnlineRevocationSource, certificateToken);
        verifyNoInteractions(issuerCertificateToken);
    }

    @ParameterizedTest
    @EnumSource(CertificateStatus.class)
    void getRevocationToken_WhenRetrievingRevocationTokenIsSuccessful_LogsProcessAndReturnsOcspToken(
            CertificateStatus status
    ) {
        OCSPToken ocspToken = Mockito.mock(OCSPToken.class);
        OnlineRevocationSource.RevocationTokenAndUrl<OCSP> ocspTokenAndUrl = new OnlineRevocationSource.RevocationTokenAndUrl<>("test url", ocspToken);

        doReturn(ocspTokenAndUrl).when(ocspOnlineRevocationSource)
                .getRevocationTokenAndUrl(certificateToken, issuerCertificateToken);
        doReturn("test id").when(certificateToken)
                .getDSSIdAsString();
        doReturn(status).when(ocspToken)
                .getStatus();

        loggingOSCPSourceWrapper.getRevocationToken(certificateToken, issuerCertificateToken);

        testLog.verifyLogInOrder(Matchers.equalTo(
                "Performed OCSP request for certificate token 'test id' from 'test url', status: '" +
                        status.name() + "'"
        ));
        verifyNoMoreInteractions(ocspOnlineRevocationSource, ocspToken, certificateToken);
        verifyNoInteractions(issuerCertificateToken);
    }

}