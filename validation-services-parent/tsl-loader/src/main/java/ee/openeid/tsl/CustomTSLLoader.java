/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.tsl;


import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("customTslLoader")
@Profile("test")
public class CustomTSLLoader extends TSLLoader{
    private List<String> testTrustedTerritories =  Arrays.asList("BE", "BG", "CY", "CZ","DE","DK", "EE", "ES", "FI", "FR", "GR", "HU","HR","IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT","NO","NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK");

    @Override
    @Autowired
    public void setTslLoaderConfigurationProperties(TSLLoaderConfigurationProperties configurationProperties) {
        configurationProperties.setTrustedTerritories(testTrustedTerritories);
        super.setTslLoaderConfigurationProperties(configurationProperties);
    }
}
