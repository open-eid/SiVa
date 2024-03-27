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

package ee.openeid.validation.service.generic.validator;

import ee.openeid.validation.service.generic.validator.filter.CountryFilter;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TLevelSignatureOfNonListedCountryPredicateTest {

    @Mock
    private CountryFilter countryFilter;
    @Mock
    private SignatureWrapper signatureWrapper;
    @Mock
    private CertificateWrapper certificateWrapper;

    @ParameterizedTest
    @MethodSource("countryNameAndSignatureLevelProvider")
    void test_WhenCountryFilterReturnsTrueAndSignatureFormatIsPresentAndSignatureLevelListIsEmpty_ReturnsFalse(String countryName, SignatureLevel signatureLevel) {
        List<String> tLevelSignatures = Collections.emptyList();
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(countryName).when(certificateWrapper).getCountryName();
        doReturn(signatureLevel).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(countryName);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("countryNameAndSignatureLevelProvider")
    void test_WhenCountryFilterReturnsTrueAndSignatureFormatIsPresentAndSignatureLevelListConsistsOfGivenSignatureFormat_ReturnsTrue(String countryName, SignatureLevel signatureLevel) {
        List<String> tLevelSignatures = List.of(signatureLevel.toString());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(countryName).when(certificateWrapper).getCountryName();
        doReturn(signatureLevel).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(countryName);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(true));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("countryNameAndSignatureLevelProvider")
    void test_WhenCountryFilterReturnsTrueAndSignatureFormatIsPresentAndSignatureLevelListContainsAllSignatureFormats_ReturnsTrue(String countryName, SignatureLevel signatureLevel) {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(countryName).when(certificateWrapper).getCountryName();
        doReturn(signatureLevel).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(countryName);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(true));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("countryNameAndLimitedSignatureLevelProvider")
    void test_WhenCountryFilterReturnsTrueAndSignatureFormatIsPresentAndSignatureLevelListContainsGivenSignatureFormat_ReturnsTrue(String countryName, SignatureLevel signatureLevel) {
        List<String> tLevelSignatures = List.of(
                SignatureLevel.CAdES_BASELINE_LT.toString(),
                SignatureLevel.PAdES_BASELINE_B.toString(),
                SignatureLevel.XAdES_BASELINE_LTA.toString()
        );
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(countryName).when(certificateWrapper).getCountryName();
        doReturn(signatureLevel).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(countryName);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(true));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("countryNameAndExcludedSignatureLevelProvider")
    void test_WhenCountryFilterReturnsTrueAndSignatureFormatIsPresentAndSignatureLevelListDoesNotContainGivenSignatureFormat_ReturnsFalse(String countryName, SignatureLevel signatureLevel) {
        List<String> tLevelSignatures = List.of(
                SignatureLevel.CAdES_BASELINE_LT.toString(),
                SignatureLevel.PAdES_BASELINE_B.toString(),
                SignatureLevel.XAdES_BASELINE_LTA.toString()
        );
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(countryName).when(certificateWrapper).getCountryName();
        doReturn(signatureLevel).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(countryName);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("countryNameProvider")
    void test_WhenCountryFilterReturnsTrueAndSignatureFormatIsMissing_ReturnsFalse(String countryName) {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(countryName).when(certificateWrapper).getCountryName();
        doReturn(null).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(countryName);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("countryNameProvider")
    void test_WhenCountryFilterReturnsFalse_NoSignatureFormatIsQueriedAndFalseIsReturned(String countryName) {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(countryName).when(certificateWrapper).getCountryName();
        doReturn(false).when(countryFilter).filter(countryName);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @Test
    void test_WhenSigningCertificateIsMissingAndCountryFilterReturnsFalse_NoSignatureFormatIsQueriedAndFalseIsReturned() {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(null).when(signatureWrapper).getSigningCertificate();
        doReturn(false).when(countryFilter).filter(null);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(signatureWrapper, countryFilter);
    }

    @Test
    void test_WhenSigningCertificateCountryNameIsMissingAndCountryFilterReturnsFalse_NoSignatureFormatIsQueriedAndFalseIsReturned() {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(null).when(certificateWrapper).getCountryName();
        doReturn(false).when(countryFilter).filter(null);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(signatureWrapper, countryFilter);
    }

    @Test
    void test_WhenSigningCertificateIsMissingAndCountryFilterReturnsTrueAndSignatureFormatIsMissing_ReturnsFalse() {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(null).when(signatureWrapper).getSigningCertificate();
        doReturn(null).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(null);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(signatureWrapper, countryFilter);
    }

    @Test
    void test_WhenSigningCertificateCountryNameIsMissingAndCountryFilterReturnsTrueAndSignatureFormatIsMissing_ReturnsFalse() {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(null).when(certificateWrapper).getCountryName();
        doReturn(null).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(null);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(false));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("signatureLevelProvider")
    void test_WhenSigningCertificateIsMissingAndCountryFilterReturnsTrueAndSignatureFormatIsInSignatureLevelList_ReturnsTrue(SignatureLevel signatureLevel) {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(null).when(signatureWrapper).getSigningCertificate();
        doReturn(signatureLevel).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(null);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(true));
        verifyNoMoreInteractions(signatureWrapper, countryFilter);
    }

    @ParameterizedTest
    @MethodSource("signatureLevelProvider")
    void test_WhenSigningCertificateCountryNameIsMissingAndCountryFilterReturnsTrueAndSignatureFormatIsInSignatureLevelList_ReturnsTrue(SignatureLevel signatureLevel) {
        List<String> tLevelSignatures = signatureLevelProvider()
                .map(SignatureLevel::toString)
                .collect(Collectors.toList());
        TLevelSignatureOfNonListedCountryPredicate predicate = new TLevelSignatureOfNonListedCountryPredicate(countryFilter, tLevelSignatures);

        doReturn(certificateWrapper).when(signatureWrapper).getSigningCertificate();
        doReturn(null).when(certificateWrapper).getCountryName();
        doReturn(signatureLevel).when(signatureWrapper).getSignatureFormat();
        doReturn(true).when(countryFilter).filter(null);

        boolean result = predicate.test(signatureWrapper);

        assertThat(result, Matchers.is(true));
        verifyNoMoreInteractions(certificateWrapper, signatureWrapper, countryFilter);
    }

    private static Stream<String> countryNameProvider() {
        return Stream.of("TestValue", "", " ", ".!/@*", null);
    }

    private static Stream<Arguments> countryNameAndSignatureLevelProvider() {
        return countryNameProvider()
                .flatMap(country -> signatureLevelProvider()
                .map(level -> Arguments.of(country, level.name())));
    }

    private static Stream<Arguments> countryNameAndLimitedSignatureLevelProvider() {
        return countryNameProvider()
                .flatMap(country -> Stream.of(
                            SignatureLevel.CAdES_BASELINE_LT,
                            SignatureLevel.PAdES_BASELINE_B,
                            SignatureLevel.XAdES_BASELINE_LTA
                        ).map(level -> Arguments.of(country, level.name())));
    }

    private static Stream<Arguments> countryNameAndExcludedSignatureLevelProvider() {
        return countryNameProvider()
                .flatMap(country -> signatureLevelProvider()
                        .filter(level -> level != SignatureLevel.CAdES_BASELINE_LT)
                        .filter(level -> level != SignatureLevel.PAdES_BASELINE_B)
                        .filter(level -> level != SignatureLevel.XAdES_BASELINE_LTA)
                        .map(level -> Arguments.of(country, level.name())));
    }

    private static Stream<SignatureLevel> signatureLevelProvider() {
        return Stream.of(
                SignatureLevel.CAdES_BASELINE_B,
                SignatureLevel.CAdES_BASELINE_T,
                SignatureLevel.CAdES_BASELINE_LT,
                SignatureLevel.CAdES_BASELINE_LTA,
                SignatureLevel.PAdES_BASELINE_B,
                SignatureLevel.PAdES_BASELINE_T,
                SignatureLevel.PAdES_BASELINE_LT,
                SignatureLevel.PAdES_BASELINE_LTA,
                SignatureLevel.XAdES_BASELINE_B,
                SignatureLevel.XAdES_BASELINE_T,
                SignatureLevel.XAdES_BASELINE_LT,
                SignatureLevel.XAdES_BASELINE_LTA
        );
    }
}