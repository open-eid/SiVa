/*
 * Copyright 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.validation.service.generic.validator.filter.CountryFilter;
import eu.europa.esig.dss.model.x509.CertificateToken;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.cert.X509Certificate;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class OCSPRequestPredicateTest {

    @Mock
    private BiFunction<X509Certificate, ASN1ObjectIdentifier, String> subjectDnAttributeExtractor;
    @Mock
    private CountryFilter countryFilter;
    @InjectMocks
    private OCSPRequestPredicate ocspRequestPredicate;
    @Mock
    private CertificateToken certificateToken;
    @Mock
    private CertificateToken issuerCertificateToken;
    @Mock
    private X509Certificate x509Certificate;

    @ParameterizedTest
    @MethodSource("argumentProvider")
    void test_WhenDistinguishedNameReturnsStringOrNull_ThenReturnsBoolean(String countryValue, boolean returnValue) {
        doReturn(x509Certificate).when(certificateToken).getCertificate();
        doReturn(countryValue).when(subjectDnAttributeExtractor).apply(x509Certificate, BCStyle.C);
        doReturn(returnValue).when(countryFilter).filter(countryValue);

        boolean result = ocspRequestPredicate.test(certificateToken, issuerCertificateToken);

        assertThat(result, Matchers.is(returnValue));

        verifyNoMoreInteractions(certificateToken, subjectDnAttributeExtractor, countryFilter);
        verifyNoInteractions(issuerCertificateToken, x509Certificate);
    }

    private static Stream<Arguments> argumentProvider() {
        return Stream.of("EE", "LT", "", " ", ".!/@*", null)
                .flatMap(country -> Stream.of(true, false).map(result -> Arguments.of(country, result)));
    }
}
