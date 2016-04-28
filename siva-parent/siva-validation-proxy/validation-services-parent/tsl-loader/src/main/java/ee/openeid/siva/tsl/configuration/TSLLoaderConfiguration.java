package ee.openeid.siva.tsl.configuration;

import ee.openeid.siva.tsl.ReloadableTrustedListAndCustomCertificateSource;
import ee.openeid.siva.tsl.ReloadableTrustedListCertificateSource;
import ee.openeid.siva.tsl.TSLRefreshPolicy;
import ee.openeid.siva.tsl.keystore.DSSKeyStoreFactoryBean;
import eu.europa.esig.dss.client.crl.AlwaysFailingCRLSource;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.client.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.client.ocsp.AlwaysFailingOCSPSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.x509.crl.CRLSource;
import eu.europa.esig.dss.x509.ocsp.OCSPSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@Configuration
@EnableScheduling
public class TSLLoaderConfiguration {

    @Value("${trusted.list.source.fileCacheDirectory}")
    String fileCacheDirectory;

    @Value("${keystore.type}")
    String keystoreType;

    @Value("${keystore.filename}")
    String keystoreFilename;

    @Value("${keystore.password}")
    String keystorePassword;

    @Value("${trusted.list.source.lotlUrl}")
    String lotlUrl;

    @Value("${trusted.list.source.tslRefreshPolicy}")
    String tslRefreshPolicy;

    @Bean
    public FileCacheDataLoader fileCacheDataLoader() {
        FileCacheDataLoader fileCacheDataLoader = new FileCacheDataLoader();
        File fileCacheDir = fileCacheDirectory != null && !fileCacheDirectory.isEmpty() ? new File(fileCacheDirectory) : null;
        fileCacheDataLoader.setFileCacheDirectory(fileCacheDir);
        return fileCacheDataLoader;
    }

    @Bean
    public DSSKeyStoreFactoryBean dssKeyStore() {
        DSSKeyStoreFactoryBean dssKeyStoreFactoryBean = new DSSKeyStoreFactoryBean();
        dssKeyStoreFactoryBean.setKeyStoreType(keystoreType);
        dssKeyStoreFactoryBean.setKeyStoreFilename(keystoreFilename);
        dssKeyStoreFactoryBean.setKeyStorePassword(keystorePassword);
        return dssKeyStoreFactoryBean;
    }

    @Bean
    public CRLSource crlSource() {
        return new AlwaysFailingCRLSource();
    }

    @Bean
    public OCSPSource ocspSource() {
        return new AlwaysFailingOCSPSource();
    }

    @Bean
    public ReloadableTrustedListCertificateSource trustedListSource(KeyStoreCertificateSource keyStoreCertificateSource, FileCacheDataLoader fileCacheDataLoader) throws Exception {
        ReloadableTrustedListCertificateSource reloadableTrustedListCertificateSource = new ReloadableTrustedListAndCustomCertificateSource();
        reloadableTrustedListCertificateSource.setDataLoader(fileCacheDataLoader);
        reloadableTrustedListCertificateSource.setCheckSignature(true);
        reloadableTrustedListCertificateSource.setKeyStoreCertificateSource(keyStoreCertificateSource);
        reloadableTrustedListCertificateSource.setLotlUrl(lotlUrl);
        reloadableTrustedListCertificateSource.setTslRefreshPolicy(TSLRefreshPolicy.valueOf(tslRefreshPolicy));
        return reloadableTrustedListCertificateSource;
    }

    @Bean
    public CommonCertificateVerifier certificateVerifier(ReloadableTrustedListCertificateSource trustedListSource, OCSPSource ocspSource, CRLSource crlSource) {
       return new CommonCertificateVerifier(trustedListSource, crlSource, ocspSource, new CommonsDataLoader());
    }

}
