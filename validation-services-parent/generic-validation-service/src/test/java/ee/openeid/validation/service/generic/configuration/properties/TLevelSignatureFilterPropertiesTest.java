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

package ee.openeid.validation.service.generic.configuration.properties;

import ee.openeid.siva.validation.helper.AbstractValidationTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.List;

class TLevelSignatureFilterPropertiesTest extends AbstractValidationTest {

    private TLevelSignatureFilterProperties configurationProperties;

    @BeforeEach
    void setUpConfigurationProperties() {
        configurationProperties = new TLevelSignatureFilterProperties();

        configurationProperties.setFilterType(TLevelSignatureFilterProperties.CountryFilterType.ALLOWED_COUNTRIES);
        configurationProperties.setCountries(List.of("AA", "BB"));
    }

    @Test
    void validate_WhenAllConfigurationParametersProvidedAndValid_Succeeds() {
        validateAndExpectNoErrors(configurationProperties);
    }

    @Test
    void validate_WhenFilterTypeIsMissing_Fails() {
        configurationProperties.setFilterType(null);

        validateAndExpectOneError(configurationProperties, "filterType", "must not be null");
    }

    @ParameterizedTest
    @EnumSource(TLevelSignatureFilterProperties.CountryFilterType.class)
    void validate_WhenFilterTypeIsAnyOfPossibleValues_Succeeds(TLevelSignatureFilterProperties.CountryFilterType filterType) {
        configurationProperties.setFilterType(filterType);

        validateAndExpectNoErrors(configurationProperties);
    }

    @Test
    void validate_WhenCountriesIsMissing_Fails() {
        configurationProperties.setCountries(null);

        validateAndExpectOneError(configurationProperties, "countries", "must not be null");
    }

    @Test
    void validate_WhenCountriesIsEmpty_Succeeds() {
        configurationProperties.setCountries(Collections.emptyList());

        validateAndExpectNoErrors(configurationProperties);
    }

    @Test
    void validate_WhenCountriesContainsNull_Fails() {
        configurationProperties.setCountries(Collections.singletonList(null));

        validateAndExpectOneError(configurationProperties, "countries[0].<list element>", "must not be blank");
    }

    @Test
    void validate_WhenCountriesContainsEmpty_Fails() {
        configurationProperties.setCountries(Collections.singletonList(StringUtils.EMPTY));

        validateAndExpectOneError(configurationProperties, "countries[0].<list element>", "must not be blank");
    }

    @Test
    void validate_WhenCountriesContainsBlank_Fails() {
        configurationProperties.setCountries(Collections.singletonList(StringUtils.SPACE));

        validateAndExpectOneError(configurationProperties, "countries[0].<list element>", "must not be blank");
    }
}