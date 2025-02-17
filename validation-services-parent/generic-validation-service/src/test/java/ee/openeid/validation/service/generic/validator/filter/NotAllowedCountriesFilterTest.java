/*
 * Copyright 2023 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.filter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class NotAllowedCountriesFilterTest {

    @ParameterizedTest
    @MethodSource("argumentSource")
    void filter_WhenCountryIsStringOrNull_ThenReturnBoolean(String country, boolean expectedResult) {
        List<String> countries = List.of("EE", "LT", " ", ".!/@*");

        CountryFilter countryFilter = new NotAllowedCountriesFilter(countries);

        boolean result = countryFilter.filter(country);

        assertThat(result, equalTo(expectedResult));
    }

    private static Stream<Arguments> argumentSource() {
        return Stream.of(
                Arguments.of("DE", true),
                Arguments.of("LT", false),
                Arguments.of("", true),
                Arguments.of(" ", false),
                Arguments.of(".!/@*", false),
                Arguments.of("BE", true),
                Arguments.of(null, true)
        );
    }
}