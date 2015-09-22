package ee.sk.tsl.downloader;

import eu.europa.esig.dss.tsl.ReloadableTrustedListCertificateSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TslOfflineDownloader {
    public static void main(String... args) {
        BeanFactory context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");

        ReloadableTrustedListCertificateSource source = context.getBean(ReloadableTrustedListCertificateSource.class);
        source.refresh();
    }
}