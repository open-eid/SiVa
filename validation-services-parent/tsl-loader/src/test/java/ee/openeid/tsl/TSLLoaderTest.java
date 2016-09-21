package ee.openeid.tsl;

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TSLLoaderTest {

    private static final String TSL_URL = "url";
    private static final String TSL_CODE = "CO";

    private static final List<String> DEFAULT_TRUESTED_TERRITORIES = Arrays.asList("AT", "BE", "BG", "CY", "CZ",/*"DE",*/"DK", "EE", "ES", "FI", "FR", "GR", "HU",/*"HR",*/"IE", "IS", "IT", "LT", "LU", "LV", "LI", "MT",/*"NO",*/"NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK");

    @Mock
    private TSLValidationJobFactory tslValidationJobFactory;
    @Mock
    private TSLValidationJob tslValidationJob;
    @Mock
    private TrustedListsCertificateSource trustedListSource;
    @Mock
    private KeyStoreCertificateSource keyStoreCertificateSource;

    @InjectMocks
    private TSLLoader tslLoader;

    @Before
    public void setUp() throws Exception {
        when(tslValidationJobFactory.createValidationJob()).thenReturn(tslValidationJob);
        doNothing().when(tslValidationJob).initRepository();
        doNothing().when(tslValidationJob).refresh();
    }

    private void initCacheLoadingConfigurationProperties() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(true, TSL_URL, TSL_CODE));
        tslLoader.init();
    }

    private void initOnlineLoadingConfigurationProperties() {
        tslLoader.setTslLoaderConfigurationProperties(createConfigurationProperties(false, TSL_URL, TSL_CODE));
        tslLoader.init();
    }

    private TSLLoaderConfigurationProperties createConfigurationProperties(boolean loadFromCache, String url, String code) {
        TSLLoaderConfigurationProperties props = new TSLLoaderConfigurationProperties();
        props.setUrl(url);
        props.setCode(code);
        props.setLoadFromCache(loadFromCache);
        return props;
    }

    @Test
    public void whenLoadFromCacheIsNotSetInPropertiesThenTSLShouldNotBeRefreshed() {
        initCacheLoadingConfigurationProperties();
        verify(tslValidationJob).initRepository();
        verify(tslValidationJob, never()).refresh();
    }

    @Test
    public void whenLoadFromCacheIsSetInPropertiesThenTSLShouldBeRefreshed() {
        initOnlineLoadingConfigurationProperties();
        verify(tslValidationJob).refresh();
        verify(tslValidationJob, never()).initRepository();
    }

    @Test
    public void tslValidationJobLotlUrlAndCodeShouldBeInitializedFromConfigurationProperties() {
        initOnlineLoadingConfigurationProperties();
        verify(tslValidationJob).setLotlCode(TSL_CODE);
        verify(tslValidationJob).setLotlUrl(TSL_URL);
    }

    @Test
    public void tslValidationJobShouldBeInitializedWithCorrectTerritories() {
        initCacheLoadingConfigurationProperties();
        verify(tslValidationJob).setFilterTerritories(DEFAULT_TRUESTED_TERRITORIES);
    }
}