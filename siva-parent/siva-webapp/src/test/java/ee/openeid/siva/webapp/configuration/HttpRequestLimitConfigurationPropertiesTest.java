/*
 * Copyright 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.configuration;

import ee.openeid.siva.validation.helper.AbstractValidationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.unit.DataSize;

class HttpRequestLimitConfigurationPropertiesTest extends AbstractValidationTest {

    HttpRequestLimitConfigurationProperties configurationProperties;

    @BeforeEach
    void setUpConfigurationProperties() {
        configurationProperties = new HttpRequestLimitConfigurationProperties();
    }

    @Test
    void validate_WhenMaxRequestSizeLimitIsMissing_Succeeds() {
        configurationProperties.setMaxRequestSizeLimit(null);
        validateAndExpectNoErrors(configurationProperties);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 1024L, Long.MAX_VALUE})
    void validate_WhenMaxRequestSizeLimitIsMoreOrEqualThanOneByte_Succeeds(long sizeInBytes) {
        configurationProperties.setMaxRequestSizeLimit(DataSize.ofBytes(sizeInBytes));
        validateAndExpectNoErrors(configurationProperties);
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L})
    void validate_WhenMaxRequestSizeLimitIsLessThanOneByte_Fails(long sizeInBytes) {
        configurationProperties.setMaxRequestSizeLimit(DataSize.ofBytes(sizeInBytes));
        validateAndExpectOneError(configurationProperties, "maxRequestSizeLimit", "must not be less than 1 BYTES");
    }

}
