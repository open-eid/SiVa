/*
 * Copyright 2021 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.RelatedRevocationWrapper;
import eu.europa.esig.dss.diagnostic.RevocationWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.TokenProxy;
import eu.europa.esig.dss.enumerations.CertificateStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

class TokenUtilsTest {

    @ParameterizedTest
    @MethodSource("invalidTokenArguments")
    void testIsTokenSignatureIntactAndSignatureValidAndTrustedChainShouldReturnFalse(
            boolean signatureIntact, boolean signatureValid, boolean trustedChain, boolean signingCertificatePresent
    ) {
        TokenProxy tokenMock = Mockito.mock(TokenProxy.class);
        CertificateWrapper signingCertificateMock = null;
        Mockito.doReturn(signatureIntact).when(tokenMock).isSignatureIntact();
        if (signatureIntact) {
            Mockito.doReturn(signatureValid).when(tokenMock).isSignatureValid();
            if (signatureValid) {
                Mockito.doReturn(trustedChain).when(tokenMock).isTrustedChain();
                if (!trustedChain) {
                    if (signingCertificatePresent) {
                        signingCertificateMock = Mockito.mock(CertificateWrapper.class);
                        Mockito.doReturn(false).when(signingCertificateMock).isTrustedChain();
                    }
                    Mockito.doReturn(signingCertificateMock).when(tokenMock).getSigningCertificate();
                }
            }
        }

        boolean result = TokenUtils.isTokenSignatureIntactAndSignatureValidAndTrustedChain(tokenMock);

        Assertions.assertFalse(result);
        Mockito.verify(tokenMock).isSignatureIntact();
        if (signatureIntact) {
            Mockito.verify(tokenMock).isSignatureValid();
            if (signatureValid) {
                Mockito.verify(tokenMock).isTrustedChain();
                if (!trustedChain) {
                    Mockito.verify(tokenMock).getSigningCertificate();
                    if (signingCertificatePresent) {
                        Mockito.verify(signingCertificateMock).isTrustedChain();
                        Mockito.verifyNoMoreInteractions(signingCertificateMock);
                    }
                }
            }
        }
        Mockito.verifyNoMoreInteractions(tokenMock);
    }

    private static Stream<Arguments> invalidTokenArguments() {
        return Stream.of(
                Arguments.of(false, true, true, true),
                Arguments.of(true, false, true, true),
                Arguments.of(true, true, false, true),
                Arguments.of(true, true, false, false)
        );
    }

    @Test
    void testIsTokenSignatureIntactAndSignatureValidAndTrustedChainShouldReturnTrueWhenTokenTrustedChainIsTrue() {
        TokenProxy tokenMock = Mockito.mock(TokenProxy.class);
        Mockito.doReturn(true).when(tokenMock).isSignatureIntact();
        Mockito.doReturn(true).when(tokenMock).isSignatureValid();
        Mockito.doReturn(true).when(tokenMock).isTrustedChain();

        boolean result = TokenUtils.isTokenSignatureIntactAndSignatureValidAndTrustedChain(tokenMock);

        Assertions.assertTrue(result);
        Mockito.verify(tokenMock).isSignatureIntact();
        Mockito.verify(tokenMock).isSignatureValid();
        Mockito.verify(tokenMock).isTrustedChain();
        Mockito.verifyNoMoreInteractions(tokenMock);
    }

    @Test
    void testIsTokenSignatureIntactAndSignatureValidAndTrustedChainShouldReturnTrueWhenSigningCertificateTrustedChainIsTrue() {
        TokenProxy tokenMock = Mockito.mock(TokenProxy.class);
        Mockito.doReturn(true).when(tokenMock).isSignatureIntact();
        Mockito.doReturn(true).when(tokenMock).isSignatureValid();
        Mockito.doReturn(false).when(tokenMock).isTrustedChain();
        CertificateWrapper signingCertificateMock = Mockito.mock(CertificateWrapper.class);
        Mockito.doReturn(true).when(signingCertificateMock).isTrustedChain();
        Mockito.doReturn(signingCertificateMock).when(tokenMock).getSigningCertificate();

        boolean result = TokenUtils.isTokenSignatureIntactAndSignatureValidAndTrustedChain(tokenMock);

        Assertions.assertTrue(result);
        Mockito.verify(tokenMock).isSignatureIntact();
        Mockito.verify(tokenMock).isSignatureValid();
        Mockito.verify(tokenMock).isTrustedChain();
        Mockito.verify(tokenMock).getSigningCertificate();
        Mockito.verify(signingCertificateMock).isTrustedChain();
        Mockito.verifyNoMoreInteractions(tokenMock, signingCertificateMock);
    }

    @ParameterizedTest
    @ValueSource(classes = {RevocationWrapper.class, RelatedRevocationWrapper.class})
    void testIsRevocationTokenForCertificateAndCertificateStatusGoodShouldReturnFalseWhenRevocationIsNotCertificateRevocation(
            Class<? extends RevocationWrapper> revocationTokenType
    ) {
        RevocationWrapper revocationTokenMock = Mockito.mock(revocationTokenType);

        boolean result = TokenUtils.isRevocationTokenForCertificateAndCertificateStatusGood(revocationTokenMock);

        Assertions.assertFalse(result);
        Mockito.verifyNoInteractions(revocationTokenMock);
    }

    @ParameterizedTest
    @EnumSource(value = CertificateStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "GOOD")
    void testIsRevocationTokenForCertificateAndCertificateStatusGoodShouldReturnFalseWhenCertificateStatusIsNotGood(
            CertificateStatus certificateStatus
    ) {
        CertificateRevocationWrapper revocationTokenMock = Mockito.mock(CertificateRevocationWrapper.class);
        Mockito.doReturn(certificateStatus).when(revocationTokenMock).getStatus();

        boolean result = TokenUtils.isRevocationTokenForCertificateAndCertificateStatusGood(revocationTokenMock);

        Assertions.assertFalse(result);
        Mockito.verify(revocationTokenMock).getStatus();
        Mockito.verifyNoMoreInteractions(revocationTokenMock);
    }

    @Test
    void testIsRevocationTokenForCertificateAndCertificateStatusGoodShouldReturnTrue() {
        CertificateRevocationWrapper revocationTokenMock = Mockito.mock(CertificateRevocationWrapper.class);
        Mockito.doReturn(CertificateStatus.GOOD).when(revocationTokenMock).getStatus();

        boolean result = TokenUtils.isRevocationTokenForCertificateAndCertificateStatusGood(revocationTokenMock);

        Assertions.assertTrue(result);
        Mockito.verify(revocationTokenMock).getStatus();
        Mockito.verifyNoMoreInteractions(revocationTokenMock);
    }

    @ParameterizedTest
    @MethodSource("invalidTimestampTokenArguments")
    void testIsTimestampTokenMessageImprintDataFoundAndMessageImprintDataIntactShouldReturnFalse(
            boolean messageImprintDataFound, boolean messageImprintDataIntact
    ) {
        TimestampWrapper timestampTokenMock = Mockito.mock(TimestampWrapper.class);
        Mockito.doReturn(messageImprintDataFound).when(timestampTokenMock).isMessageImprintDataFound();
        if (messageImprintDataFound) {
            Mockito.doReturn(messageImprintDataIntact).when(timestampTokenMock).isMessageImprintDataIntact();
        }

        boolean result = TokenUtils.isTimestampTokenMessageImprintDataFoundAndMessageImprintDataIntact(timestampTokenMock);

        Assertions.assertFalse(result);
        Mockito.verify(timestampTokenMock).isMessageImprintDataFound();
        if (messageImprintDataFound) {
            Mockito.verify(timestampTokenMock).isMessageImprintDataIntact();
        }
        Mockito.verifyNoMoreInteractions(timestampTokenMock);
    }

    private static Stream<Arguments> invalidTimestampTokenArguments() {
        return Stream.of(
                Arguments.of(false, true),
                Arguments.of(true, false)
        );
    }

    @Test
    void testIsTimestampTokenMessageImprintDataFoundAndMessageImprintDataIntactShouldReturnTrue() {
        TimestampWrapper timestampTokenMock = Mockito.mock(TimestampWrapper.class);
        Mockito.doReturn(true).when(timestampTokenMock).isMessageImprintDataFound();
        Mockito.doReturn(true).when(timestampTokenMock).isMessageImprintDataIntact();

        boolean result = TokenUtils.isTimestampTokenMessageImprintDataFoundAndMessageImprintDataIntact(timestampTokenMock);

        Assertions.assertTrue(result);
        Mockito.verify(timestampTokenMock).isMessageImprintDataFound();
        Mockito.verify(timestampTokenMock).isMessageImprintDataIntact();
        Mockito.verifyNoMoreInteractions(timestampTokenMock);
    }

}
