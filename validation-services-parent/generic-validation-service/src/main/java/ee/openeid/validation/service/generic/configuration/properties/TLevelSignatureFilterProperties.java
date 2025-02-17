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

package ee.openeid.validation.service.generic.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static ee.openeid.validation.service.generic.configuration.properties.TLevelSignatureFilterProperties.PROPERTIES_PREFIX;

@Getter
@Setter
@Validated
@ConditionalOnProperty(prefix = PROPERTIES_PREFIX, name = "filter-type")
@Configuration
@ConfigurationProperties(prefix = PROPERTIES_PREFIX)
public class TLevelSignatureFilterProperties {
    static final String PROPERTIES_PREFIX = "t-level-signature-filter";

    @NotNull
    private CountryFilterType filterType;
    @NotNull
    private List<@NotBlank String> countries = new ArrayList<>();

    public enum CountryFilterType {
        ALLOWED_COUNTRIES, NOT_ALLOWED_COUNTRIES;
    }
}
