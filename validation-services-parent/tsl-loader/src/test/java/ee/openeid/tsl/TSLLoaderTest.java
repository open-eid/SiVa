/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

import ee.openeid.tsl.configuration.LotlConfigurationProperties;
import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import ee.openeid.tsl.configuration.TSLValidationKeystoreProperties;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@Disabled("DSSKeyStoreFactory error")
@ExtendWith(MockitoExtension.class)
public class TSLLoaderTest {

    private static final String TSL_URL = "url";
    private static final String TSL_OJ_URL = "ojUrl";
    private static final String TSL_INFO_URL = "infoUrl";

    private static final List<String> DEFAULT_TRUSTED_TERRITORIES = Arrays.asList(/*AT*/ "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GR", "HU", "HR", "IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT", "NO", "NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK");
    @Mock
    private TSLValidationJobFactory tslValidationJobFactory;
    @Mock
    private TLValidationJob tslValidationJob;
    @Mock
    private TrustedListsCertificateSource trustedListSource;
    @Mock
    private KeyStoreCertificateSource keyStoreCertificateSource;

    @InjectMocks
    private TSLLoader tslLoader;

    @BeforeEach
    public void setUp() throws Exception {
        when(tslValidationJobFactory.createValidationJob()).thenReturn(tslValidationJob);
        lenient().doNothing().when(tslValidationJob).offlineRefresh();
        lenient().doNothing().when(tslValidationJob).onlineRefresh();
    }

    private void initCacheLoadingConfigurationProperties() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(true));
        tslLoader.init();
    }

    private void initOnlineLoadingConfigurationProperties() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(false));
        tslLoader.init();
    }

    private TSLLoaderConfigurationProperties createConfigurationProperties(boolean loadFromCache) {
        TSLLoaderConfigurationProperties props = new TSLLoaderConfigurationProperties();
        List<LotlConfigurationProperties> lotls = new ArrayList<>();
        lotls.add(new LotlConfigurationProperties());
        props.setLotls(lotls);
        props.getLotls().get(0).setUrl(TSL_URL);
        props.getLotls().get(0).setOjurl(TSL_OJ_URL);
        props.getLotls().get(0).setLotlRootSchemeInfoUri(TSL_INFO_URL);
        props.getLotls().get(0).setOtherTslPointer("");
        props.setLoadFromCache(loadFromCache);
        props.getLotls().get(0).setTrustedTerritories(DEFAULT_TRUSTED_TERRITORIES);
        props.getLotls().get(0).setValidationTruststore(new TSLValidationKeystoreProperties());
        props.getLotls().get(0).getValidationTruststore().setFilename("test-siva-keystore.jks");
        props.getLotls().get(0).getValidationTruststore().setType("JKS");
        props.getLotls().get(0).getValidationTruststore().setPassword("siva-keystore-password");
        return props;
    }

    @Test
    public void whenLoadFromCacheIsNotSetInPropertiesThenTSLShouldNotBeRefreshed() {
        initCacheLoadingConfigurationProperties();
        verify(tslValidationJob).offlineRefresh();
        verify(tslValidationJob, never()).onlineRefresh();
    }

    @Test
    public void whenLoadFromCacheIsSetInPropertiesThenTSLShouldBeRefreshed() {
        initOnlineLoadingConfigurationProperties();
        verify(tslValidationJob).onlineRefresh();
        verify(tslValidationJob, never()).offlineRefresh();
    }

}
