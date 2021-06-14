package ee.openeid.validation.service.generic;

import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.CertificateStatus;
import eu.europa.esig.dss.enumerations.RevocationType;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class RevocationFreshnessValidatorTest {

    @Mock
    private Reports validationReports;
    @InjectMocks
    private RevocationFreshnessValidator validator;

    @Mock
    private DiagnosticData diagnosticData;
    @Mock
    private SimpleReport simpleReport;

    @Test
    public void testNoSignaturesInDiagnosticDataShouldDoNothing() {
        mockDiagnosticDataGetSignatures();

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verifyNoInteractions(simpleReport);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5})
    public void testNoSigningCertificateInSignaturesFromDiagnosticDataShouldDoNothing(int signatureCount) {
        List<SignatureWrapper> signatureWrappers = new ArrayList<>(signatureCount);
        for (int i = 0; i < signatureCount; ++i) {
            SignatureWrapper signatureWrapper = Mockito.mock(SignatureWrapper.class);
            Mockito.doReturn(null).when(signatureWrapper).getSigningCertificate();
            signatureWrappers.add(signatureWrapper);
        }
        mockDiagnosticDataGetSignatures(signatureWrappers);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        for (int i = 0; i < signatureCount; ++i) {
            SignatureWrapper signatureWrapperMock = signatureWrappers.get(i);
            Mockito.verify(signatureWrapperMock).getSigningCertificate();
            Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        }
        Mockito.verifyNoInteractions(simpleReport);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5})
    public void testNoTimestampsInSignaturesFromDiagnosticDataShouldDoNothing(int signatureCount) {
        List<SignatureWrapper> signatureWrappers = new ArrayList<>(signatureCount);
        List<CertificateWrapper> signingCertificates = new ArrayList<>(signatureCount);
        for (int i = 0; i < signatureCount; ++i) {
            CertificateWrapper certificateWrapper = Mockito.mock(CertificateWrapper.class);
            signingCertificates.add(certificateWrapper);
            SignatureWrapper signatureWrapper = Mockito.mock(SignatureWrapper.class);
            Mockito.doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
            Mockito.doReturn(List.of()).when(signatureWrapper).getTimestampList();
            signatureWrappers.add(signatureWrapper);
        }
        mockDiagnosticDataGetSignatures(signatureWrappers);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        for (int i = 0; i < signatureCount; ++i) {
            SignatureWrapper signatureWrapperMock = signatureWrappers.get(i);
            Mockito.verify(signatureWrapperMock).getSigningCertificate();
            Mockito.verify(signatureWrapperMock).getTimestampList();
            Mockito.verifyNoMoreInteractions(signatureWrapperMock);
            Mockito.verifyNoInteractions(signingCertificates.get(i));
        }
        Mockito.verifyNoInteractions(simpleReport);
    }

    @ParameterizedTest
    @MethodSource("nonSignatureTimestampTypes")
    public void testNoSignatureTimestampsInSignatureFromDiagnosticDataShouldDoNothing(TimestampType timestampType) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createTimestampWrapperMock(timestampType);
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        Mockito.verify(timestampWrapperMock).getType();
        Mockito.verifyNoInteractions(
                certificateWrapperMock, simpleReport
        );
    }

    private static Stream<TimestampType> nonSignatureTimestampTypes() {
        return Stream.of(TimestampType.values()).filter(Predicate.not(TimestampType.SIGNATURE_TIMESTAMP::equals));
    }

    @ParameterizedTest
    @MethodSource("invalidTokenArguments")
    public void testNoValidTimestampsInSignatureFromDiagnosticDataShouldDoNothing(
            boolean signatureIntact, boolean signatureValid, boolean trustedChain, boolean trustedChainFromSigningCertificate
    ) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createTimestampWrapperMock(TimestampType.SIGNATURE_TIMESTAMP);
        Mockito.doReturn(signatureIntact).when(timestampWrapperMock).isSignatureIntact();
        if (signatureIntact) {
            Mockito.doReturn(signatureValid).when(timestampWrapperMock).isSignatureValid();
            if (signatureValid) {
                Mockito.doReturn(trustedChain).when(timestampWrapperMock).isTrustedChain();
                if (!trustedChain) {
                    CertificateWrapper signingCertificate = Mockito.mock(CertificateWrapper.class);
                    Mockito.doReturn(trustedChainFromSigningCertificate).when(signingCertificate).isTrustedChain();
                    Mockito.doReturn(signingCertificate).when(timestampWrapperMock).getSigningCertificate();
                }
            }
        }
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        Mockito.verify(timestampWrapperMock).getType();
        Mockito.verify(timestampWrapperMock).isSignatureIntact();
        if (signatureIntact) {
            Mockito.verify(timestampWrapperMock).isSignatureValid();
            if (signatureValid) {
                Mockito.verify(timestampWrapperMock).isTrustedChain();
                if (!trustedChain) {
                    Mockito.verify(timestampWrapperMock).getSigningCertificate();
                }
            }
        }
        Mockito.verifyNoInteractions(
                certificateWrapperMock, simpleReport
        );
    }

    private static Stream<Arguments> invalidTokenArguments() {
        return Stream.of(
                Arguments.of(false, true, true, true),
                Arguments.of(true, false, true, true),
                Arguments.of(true, true, false, false)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMessageImprintTimestampTokenArguments")
    public void testNoTimestampsWithImprintInSignatureFromDiagnosticDataShouldDoNothing(
            boolean messageImprintDataFound, boolean messageImprintDataIntact
    ) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createTimestampWrapperMock(TimestampType.SIGNATURE_TIMESTAMP);
        Mockito.doReturn(true).when(timestampWrapperMock).isSignatureIntact();
        Mockito.doReturn(true).when(timestampWrapperMock).isSignatureValid();
        Mockito.doReturn(true).when(timestampWrapperMock).isTrustedChain();
        Mockito.doReturn(messageImprintDataFound).when(timestampWrapperMock).isMessageImprintDataFound();
        if (messageImprintDataFound) {
            Mockito.doReturn(messageImprintDataIntact).when(timestampWrapperMock).isMessageImprintDataIntact();
        }
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        Mockito.verify(timestampWrapperMock).getType();
        Mockito.verify(timestampWrapperMock).isSignatureIntact();
        Mockito.verify(timestampWrapperMock).isSignatureValid();
        Mockito.verify(timestampWrapperMock).isTrustedChain();
        Mockito.verify(timestampWrapperMock).isMessageImprintDataFound();
        if (messageImprintDataFound) {
            Mockito.verify(timestampWrapperMock).isMessageImprintDataIntact();
        }
        Mockito.verifyNoInteractions(
                certificateWrapperMock, simpleReport
        );
    }

    private static Stream<Arguments> invalidMessageImprintTimestampTokenArguments() {
        return Stream.of(
                Arguments.of(false, true),
                Arguments.of(true, false)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-06-06T18:30:15Z", "2021-06-06T18:30:16Z", "2021-06-07T18:30:15Z", "2022-06-06T18:30:15Z"})
    public void testCrlWithNextUpdateNotBeforeTimestampInSignatureFromDiagnosticDataShouldDoNothing(String crlNextUpdate) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        CertificateRevocationWrapper crlRevocationWrapperMock = createValidCrlCertificateRevocationWrapperMock(Instant.parse(crlNextUpdate));
        Mockito.doReturn(List.of(crlRevocationWrapperMock)).when(certificateWrapperMock).getCertificateRevocationData();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        assertValidCrlCertificateRevocationWrapperMockInteractions(crlRevocationWrapperMock);
        Mockito.verify(certificateWrapperMock).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verifyNoInteractions(simpleReport);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-06-06T18:30:15.0Z", "2021-06-06T18:30:15.1Z", "2021-06-06T18:30:15.01Z", "2021-06-06T18:30:15.001Z", "2021-06-06T18:30:15.999Z"
    })
    public void testCrlWithNextUpdateInSameSecondAsHighPrecisionTimestampInSignatureFromDiagnosticDataShouldDoNothing(String timestampTime) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse(timestampTime));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        CertificateRevocationWrapper crlRevocationWrapperMock = createValidCrlCertificateRevocationWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(crlRevocationWrapperMock)).when(certificateWrapperMock).getCertificateRevocationData();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        assertValidCrlCertificateRevocationWrapperMockInteractions(crlRevocationWrapperMock);
        Mockito.verify(certificateWrapperMock).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verifyNoInteractions(simpleReport);
    }

    @Test
    public void testNoCrlNorOcspInSignatureFromDiagnosticDataShouldDoNothing() {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        Mockito.doReturn(List.of()).when(certificateWrapperMock).getCertificateRevocationData();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        Mockito.verify(certificateWrapperMock, Mockito.times(2)).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verifyNoInteractions(simpleReport);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-06-06T18:30:15Z", "2021-06-06T18:30:16Z", "2021-06-06T18:45:15Z"})
    public void testOcspWithProducedAtNotBeforeTimestampAndNotAfter15minInSignatureFromDiagnosticDataShouldDoNothing(String ocspProducedAt) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        CertificateRevocationWrapper ocspRevocationWrapperMock = createValidOcspCertificateRevocationWrapperMock(Instant.parse(ocspProducedAt));
        Mockito.doReturn(List.of(ocspRevocationWrapperMock)).when(certificateWrapperMock).getCertificateRevocationData();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        assertValidOcspCertificateRevocationWrapperMockInteractions(ocspRevocationWrapperMock);
        Mockito.verify(certificateWrapperMock, Mockito.times(2)).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verifyNoInteractions(simpleReport);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-06-06T18:30:15.0Z", "2021-06-06T18:30:15.1Z", "2021-06-06T18:30:15.01Z", "2021-06-06T18:30:15.001Z", "2021-06-06T18:30:15.999Z"
    })
    public void testOcspWithProducedAtInTheSameSecondAsHighPrecisionTimestampInSignatureFromDiagnosticDataShouldDoNothing(String timestampTime) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse(timestampTime));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        CertificateRevocationWrapper ocspRevocationWrapperMock = createValidOcspCertificateRevocationWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(ocspRevocationWrapperMock)).when(certificateWrapperMock).getCertificateRevocationData();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);

        validator.validate();

        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verifyNoMoreInteractions(validationReports);
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        assertValidOcspCertificateRevocationWrapperMockInteractions(ocspRevocationWrapperMock);
        Mockito.verify(certificateWrapperMock, Mockito.times(2)).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verifyNoInteractions(simpleReport);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-06-06T18:45:16Z", "2021-06-06T19:30:15Z", "2021-06-07T18:30:15Z"})
    public void testOcspWithProducedAt15minAfterTimestampAndNotAfter24hInSignatureFromDiagnosticDataShouldAddWarningToSignature(String ocspProducedAt) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        CertificateRevocationWrapper ocspRevocationWrapperMock = createValidOcspCertificateRevocationWrapperMock(Instant.parse(ocspProducedAt));
        Mockito.doReturn(List.of(ocspRevocationWrapperMock)).when(certificateWrapperMock).getCertificateRevocationData();
        Mockito.doReturn("S-12345").when(signatureWrapperMock).getId();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);
        List<String> signatureWarnings = new ArrayList<>();
        Mockito.doReturn(simpleReport).when(validationReports).getSimpleReport();
        mockSimpleReportGetSignatureWarnings(Map.of("S-12345", signatureWarnings));

        validator.validate();

        Assertions.assertEquals(
                List.of("The revocation information is not considered as 'fresh'."),
                signatureWarnings
        );
        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verify(signatureWrapperMock).getId();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        assertValidOcspCertificateRevocationWrapperMockInteractions(ocspRevocationWrapperMock);
        Mockito.verify(certificateWrapperMock, Mockito.times(2)).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verify(validationReports).getSimpleReport();
        Mockito.verify(simpleReport).getWarnings("S-12345");
        Mockito.verifyNoMoreInteractions(validationReports);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-06-07T18:30:16Z", "2021-06-07T18:31:15Z", "2021-06-07T19:30:15Z", "2021-06-08T18:30:15Z", "2021-07-06T18:30:15Z", "2022-06-06T18:30:15Z"
    })
    public void testOcspWithProducedAt24hAfterTimestampInSignatureFromDiagnosticDataShouldAddErrorToSignature(String ocspProducedAt) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        CertificateRevocationWrapper ocspRevocationWrapperMock = createValidOcspCertificateRevocationWrapperMock(Instant.parse(ocspProducedAt));
        Mockito.doReturn(List.of(ocspRevocationWrapperMock)).when(certificateWrapperMock).getCertificateRevocationData();
        Mockito.doReturn("S-12345").when(signatureWrapperMock).getId();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);
        List<String> signatureErrors = new ArrayList<>();
        Mockito.doReturn(simpleReport).when(validationReports).getSimpleReport();
        mockSimpleReportGetSignatureErrors(Map.of("S-12345", signatureErrors));

        validator.validate();

        Assertions.assertEquals(
                List.of("The revocation information is not considered as 'fresh'."),
                signatureErrors
        );
        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verify(signatureWrapperMock).getId();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        assertValidOcspCertificateRevocationWrapperMockInteractions(ocspRevocationWrapperMock);
        Mockito.verify(certificateWrapperMock, Mockito.times(2)).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verify(validationReports).getSimpleReport();
        Mockito.verify(simpleReport).getErrors("S-12345");
        Mockito.verifyNoMoreInteractions(validationReports);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-06-06T18:30:15Z", "2021-05-06T18:30:15Z", "2021-06-05T18:30:15Z", "2021-06-06T17:30:15Z", "2021-06-06T18:29:15Z", "2021-06-06T18:30:14Z"
    })
    public void testOcspWithProducedAtBeforeTimestampInSignatureFromDiagnosticDataShouldAddErrorToSignature(String ocspProducedAt) {
        SignatureWrapper signatureWrapperMock = Mockito.mock(SignatureWrapper.class);
        CertificateWrapper certificateWrapperMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(certificateWrapperMock).when(signatureWrapperMock).getSigningCertificate();
        TimestampWrapper timestampWrapperMock = createValidTimestampWrapperMock(Instant.parse("2021-06-06T18:30:15Z"));
        Mockito.doReturn(List.of(timestampWrapperMock)).when(signatureWrapperMock).getTimestampList();
        CertificateRevocationWrapper ocspRevocationWrapperMock = createValidOcspCertificateRevocationWrapperMock(Instant.parse(ocspProducedAt));
        Mockito.doReturn(List.of(ocspRevocationWrapperMock)).when(certificateWrapperMock).getCertificateRevocationData();
        Mockito.doReturn("S-12345").when(signatureWrapperMock).getId();
        mockDiagnosticDataGetSignatures(signatureWrapperMock);
        List<String> signatureErrors = new ArrayList<>();
        Mockito.doReturn(simpleReport).when(validationReports).getSimpleReport();
        mockSimpleReportGetSignatureErrors(Map.of("S-12345", signatureErrors));

        validator.validate();

        Assertions.assertEquals(
                List.of("OCSP response production time is before timestamp time"),
                signatureErrors
        );
        assertDiagnosticDataGetSignaturesCalled();
        Mockito.verify(signatureWrapperMock).getSigningCertificate();
        Mockito.verify(signatureWrapperMock).getTimestampList();
        Mockito.verify(signatureWrapperMock).getId();
        Mockito.verifyNoMoreInteractions(signatureWrapperMock);
        assertValidTimestampWrapperMockInteractions(timestampWrapperMock);
        assertValidOcspCertificateRevocationWrapperMockInteractions(ocspRevocationWrapperMock);
        Mockito.verify(certificateWrapperMock, Mockito.times(2)).getCertificateRevocationData();
        Mockito.verifyNoMoreInteractions(certificateWrapperMock);
        Mockito.verify(validationReports).getSimpleReport();
        Mockito.verify(simpleReport).getErrors("S-12345");
        Mockito.verifyNoMoreInteractions(validationReports);
    }

    private static TimestampWrapper createTimestampWrapperMock(TimestampType timestampType) {
        TimestampWrapper timestampWrapperMock = Mockito.mock(TimestampWrapper.class);
        Mockito.doReturn(timestampType).when(timestampWrapperMock).getType();
        return timestampWrapperMock;
    }

    private static TimestampWrapper createValidTimestampWrapperMock(Instant productionTime) {
        return createValidTimestampWrapperMock(Date.from(productionTime));
    }

    private static TimestampWrapper createValidTimestampWrapperMock(Date productionTime) {
        TimestampWrapper timestampWrapperMock = createTimestampWrapperMock(TimestampType.SIGNATURE_TIMESTAMP);
        Mockito.doReturn(true).when(timestampWrapperMock).isSignatureIntact();
        Mockito.doReturn(true).when(timestampWrapperMock).isSignatureValid();
        Mockito.doReturn(true).when(timestampWrapperMock).isTrustedChain();
        Mockito.doReturn(true).when(timestampWrapperMock).isMessageImprintDataFound();
        Mockito.doReturn(true).when(timestampWrapperMock).isMessageImprintDataIntact();
        Mockito.doReturn(productionTime).when(timestampWrapperMock).getProductionTime();
        return timestampWrapperMock;
    }

    private static void assertValidTimestampWrapperMockInteractions(TimestampWrapper timestampWrapperMock) {
        Mockito.verify(timestampWrapperMock).getType();
        Mockito.verify(timestampWrapperMock).isSignatureIntact();
        Mockito.verify(timestampWrapperMock).isSignatureValid();
        Mockito.verify(timestampWrapperMock).isTrustedChain();
        Mockito.verify(timestampWrapperMock).isMessageImprintDataFound();
        Mockito.verify(timestampWrapperMock).isMessageImprintDataIntact();
        Mockito.verify(timestampWrapperMock).getProductionTime();
        Mockito.verifyNoMoreInteractions(timestampWrapperMock);
    }

    private static CertificateRevocationWrapper createValidCrlCertificateRevocationWrapperMock(Instant nextUpdate) {
        return createValidCrlCertificateRevocationWrapperMock(Date.from(nextUpdate));
    }

    private static CertificateRevocationWrapper createValidCrlCertificateRevocationWrapperMock(Date nextUpdate) {
        CertificateRevocationWrapper certificateRevocationWrapperMock = Mockito.mock(CertificateRevocationWrapper.class);
        Mockito.doReturn(RevocationType.CRL).when(certificateRevocationWrapperMock).getRevocationType();
        Mockito.doReturn(true).when(certificateRevocationWrapperMock).isSignatureIntact();
        Mockito.doReturn(true).when(certificateRevocationWrapperMock).isSignatureValid();
        Mockito.doReturn(true).when(certificateRevocationWrapperMock).isTrustedChain();
        Mockito.doReturn(CertificateStatus.GOOD).when(certificateRevocationWrapperMock).getStatus();
        Mockito.doReturn(nextUpdate).when(certificateRevocationWrapperMock).getNextUpdate();
        return certificateRevocationWrapperMock;
    }

    private static void assertValidCrlCertificateRevocationWrapperMockInteractions(CertificateRevocationWrapper certificateRevocationWrapperMock) {
        Mockito.verify(certificateRevocationWrapperMock).getRevocationType();
        Mockito.verify(certificateRevocationWrapperMock).isSignatureIntact();
        Mockito.verify(certificateRevocationWrapperMock).isSignatureValid();
        Mockito.verify(certificateRevocationWrapperMock).isTrustedChain();
        Mockito.verify(certificateRevocationWrapperMock).getStatus();
        Mockito.verify(certificateRevocationWrapperMock).getNextUpdate();
        Mockito.verifyNoMoreInteractions(certificateRevocationWrapperMock);
    }

    private static CertificateRevocationWrapper createValidOcspCertificateRevocationWrapperMock(Instant producedAt) {
        return createValidOcspCertificateRevocationWrapperMock(Date.from(producedAt));
    }

    private static CertificateRevocationWrapper createValidOcspCertificateRevocationWrapperMock(Date producedAt) {
        CertificateRevocationWrapper certificateRevocationWrapperMock = Mockito.mock(CertificateRevocationWrapper.class);
        Mockito.doReturn(RevocationType.OCSP).when(certificateRevocationWrapperMock).getRevocationType();
        Mockito.doReturn(true).when(certificateRevocationWrapperMock).isSignatureIntact();
        Mockito.doReturn(true).when(certificateRevocationWrapperMock).isSignatureValid();
        Mockito.doReturn(true).when(certificateRevocationWrapperMock).isTrustedChain();
        Mockito.doReturn(CertificateStatus.GOOD).when(certificateRevocationWrapperMock).getStatus();
        Mockito.doReturn(producedAt).when(certificateRevocationWrapperMock).getProductionDate();
        return certificateRevocationWrapperMock;
    }

    private static void assertValidOcspCertificateRevocationWrapperMockInteractions(CertificateRevocationWrapper certificateRevocationWrapperMock) {
        Mockito.verify(certificateRevocationWrapperMock, Mockito.times(2)).getRevocationType();
        Mockito.verify(certificateRevocationWrapperMock).isSignatureIntact();
        Mockito.verify(certificateRevocationWrapperMock).isSignatureValid();
        Mockito.verify(certificateRevocationWrapperMock).isTrustedChain();
        Mockito.verify(certificateRevocationWrapperMock).getStatus();
        Mockito.verify(certificateRevocationWrapperMock).getProductionDate();
        Mockito.verifyNoMoreInteractions(certificateRevocationWrapperMock);
    }

    private void mockDiagnosticDataGetSignatures(List<SignatureWrapper> signatures) {
        Mockito.doReturn(diagnosticData).when(validationReports).getDiagnosticData();
        Mockito.doReturn(signatures).when(diagnosticData).getSignatures();
    }

    private void mockDiagnosticDataGetSignatures(SignatureWrapper... signatures) {
        mockDiagnosticDataGetSignatures(List.of(signatures));
    }

    private void assertDiagnosticDataGetSignaturesCalled() {
        Mockito.verify(validationReports).getDiagnosticData();
        Mockito.verify(diagnosticData).getSignatures();
        Mockito.verifyNoMoreInteractions(diagnosticData);
    }

    private void mockSimpleReportGetSignatureWarnings(Map<String, List<String>> mappingsOfSignatureIdToWarningList) {
        mappingsOfSignatureIdToWarningList.forEach((signatureId, signatureWarningList) -> Mockito
                .doReturn(signatureWarningList).when(simpleReport).getWarnings(signatureId)
        );
    }

    private void mockSimpleReportGetSignatureErrors(Map<String, List<String>> mappingsOfSignatureIdToErrorList) {
        mappingsOfSignatureIdToErrorList.forEach((signatureId, signatureWarningList) -> Mockito
                .doReturn(signatureWarningList).when(simpleReport).getErrors(signatureId)
        );
    }

}
