package ee.openeid.tsl;

import ee.openeid.tsl.configuration.TSLLoaderConfigurationProperties;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TSLLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSLLoader.class);

    private TSLValidationJob tslValidationJob;

    private TrustedListsCertificateSource trustedListSource;

    private TSLLoaderConfigurationProperties configurationProperties;

    private KeyStoreCertificateSource keyStoreCertificateSource;

    @PostConstruct
    public void init() {
        initTslValidatonJob();
        loadTSL();
        addEstonianTestCertificates(trustedListSource);
    }

    private void initTslValidatonJob() {
        TSLValidationJob tslValidationJob = new TSLValidationJob();
        tslValidationJob.setDataLoader(new CommonsDataLoader());
        TSLRepository tslRepository = new TSLRepository();
        tslRepository.setTrustedListsCertificateSource(trustedListSource);
        tslValidationJob.setRepository(tslRepository);
        tslValidationJob.setLotlUrl(configurationProperties.getUrl());
        tslValidationJob.setLotlCode(configurationProperties.getCode());
        tslValidationJob.setDssKeyStore(keyStoreCertificateSource);
        tslValidationJob.setCheckLOTLSignature(true);
        tslValidationJob.setCheckTSLSignatures(true);
        this.tslValidationJob = tslValidationJob;
    }

    private void loadTSL() {
        if (configurationProperties.isLoadAlwaysFromCache()) {
            LOGGER.info("Loading TSL from cache");
            tslValidationJob.initRepository();
            LOGGER.info("Finished loading TSL from cache");
        } else {
            LOGGER.info("Loading TSL over the network");
            tslValidationJob.refresh();
            LOGGER.info("Finished loading TSL from cache");
        }
    }

    @Scheduled(cron = "${tsl.schedulerCron}")
    public void refreshTSL() {
        loadTSL();
        addEstonianTestCertificates(trustedListSource);
    }

    private void addEstonianTestCertificates(TrustedListsCertificateSource tlCertSource) {
        LOGGER.info("Loading Estonian Test Certificates");
        CertificateToken certToken;

        // TEST of EE Certification Centre Root CA
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIEEzCCAvugAwIBAgIQc/jtqiMEFERMtVvsSsH7sjANBgkqhkiG9w0BAQUFADB9" +
                        "MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1" +
                        "czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290" +
                        "IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwIhgPMjAxMDEwMDcxMjM0NTZa" +
                        "GA8yMDMwMTIxNzIzNTk1OVowfTELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNl" +
                        "cnRpZml0c2VlcmltaXNrZXNrdXMxMDAuBgNVBAMMJ1RFU1Qgb2YgRUUgQ2VydGlm" +
                        "aWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVl" +
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1gGpqCtDmNNEHUjC8LXq" +
                        "xRdC1kpjDgkzOTxQynzDxw/xCjy5hhyG3xX4RPrW9Z6k5ZNTNS+xzrZgQ9m5U6uM" +
                        "ywYpx3F3DVgbdQLd8DsLmuVOz02k/TwoRt1uP6xtV9qG0HsGvN81q3HvPR/zKtA7" +
                        "MmNZuwuDFQwsguKgDR2Jfk44eKmLfyzvh+Xe6Cr5+zRnsVYwMA9bgBaOZMv1TwTT" +
                        "VNi9H1ltK32Z+IhUX8W5f2qVP33R1wWCKapK1qTX/baXFsBJj++F8I8R6+gSyC3D" +
                        "kV5N/pOlWPzZYx+kHRkRe/oddURA9InJwojbnsH+zJOa2VrNKakNv2HnuYCIonzu" +
                        "pwIDAQABo4GKMIGHMA8GA1UdEwEB/wQFMAMBAf8wDgYDVR0PAQH/BAQDAgEGMB0G" +
                        "A1UdDgQWBBS1NAqdpS8QxechDr7EsWVHGwN2/jBFBgNVHSUEPjA8BggrBgEFBQcD" +
                        "AgYIKwYBBQUHAwEGCCsGAQUFBwMDBggrBgEFBQcDBAYIKwYBBQUHAwgGCCsGAQUF" +
                        "BwMJMA0GCSqGSIb3DQEBBQUAA4IBAQAj72VtxIw6p5lqeNmWoQ48j8HnUBM+6mI0" +
                        "I+VkQr0EfQhfmQ5KFaZwnIqxWrEPaxRjYwV0xKa1AixVpFOb1j+XuVmgf7khxXTy" +
                        "Bmd8JRLwl7teCkD1SDnU/yHmwY7MV9FbFBd+5XK4teHVvEVRsJ1oFwgcxVhyoviR" +
                        "SnbIPaOvk+0nxKClrlS6NW5TWZ+yG55z8OCESHaL6JcimkLFjRjSsQDWIEtDvP4S" +
                        "tH3vIMUPPiKdiNkGjVLSdChwkW3z+m0EvAjyD9rnGCmjeEm5diLFu7VMNVqupsbZ" +
                        "SfDzzBLc5+6TqgQTOG7GaZk2diMkn03iLdHGFrh8ML+mXG9SjEPI");

        tlCertSource.addCertificate(certToken, getCAServiceInfo(certToken));

        // TEST of ESTEID-SK 2011
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIEuzCCA6OgAwIBAgIQSxRID7FoIaNNdNhBeucLvDANBgkqhkiG9w0BAQUF" +
                        "ADB9MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1p" +
                        "c2tlc2t1czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENl" +
                        "bnRyZSBSb290IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwHhcNMTEw" +
                        "MzA3MTMwNjA5WhcNMjMwOTA3MTIwNjA5WjBsMQswCQYDVQQGEwJFRTEiMCAG" +
                        "A1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEfMB0GA1UEAwwWVEVT" +
                        "VCBvZiBFU1RFSUQtU0sgMjAxMTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVl" +
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0SMr+A2QGMJuNpu6" +
                        "0MgqKG0yLL7JfvjNtgs2hqWADDn1AQeD79o+8r4SRYp9kowSFA8E1v38XXTH" +
                        "Rq3nSZeToOC5DMAWjsKlm4x8hwwp31BXCs/Hrl9VmikIgAlaHvv3Z+MzS6qe" +
                        "LdzyYi/glPVrY42A6/kBApOJlOVLvAFdySNmFkY+Ky7MZ9jbBr+Nx4py/V7x" +
                        "m9VD62Oe1lku4S4qd+VYcQ5jftbr4OFjBp9Nn58/5svQxrLjv3B67i19d7sN" +
                        "h7UPnMiO6BeBb6yb3P1lqdHofE1lElStIPViJlzjPOh4puxWadHDvVYUCJgW" +
                        "2aM58mTfjFhZbVfcrVn5OyIiTQIDAQABo4IBRjCCAUIwDwYDVR0TAQH/BAUw" +
                        "AwEB/zAOBgNVHQ8BAf8EBAMCAQYwgZkGA1UdIASBkTCBjjCBiwYKKwYBBAHO" +
                        "HwMBATB9MFgGCCsGAQUFBwICMEweSgBBAGkAbgB1AGwAdAAgAHQAZQBzAHQA" +
                        "aQBtAGkAcwBlAGsAcwAuACAATwBuAGwAeQAgAGYAbwByACAAdABlAHMAdABp" +
                        "AG4AZwAuMCEGCCsGAQUFBwIBFhVodHRwczovL3d3dy5zay5lZS9DUFMwHQYD" +
                        "VR0OBBYEFEG2/sWxsbRTE4z6+mLQNG1tIjQKMB8GA1UdIwQYMBaAFLU0Cp2l" +
                        "LxDF5yEOvsSxZUcbA3b+MEMGA1UdHwQ8MDowOKA2oDSGMmh0dHBzOi8vd3d3" +
                        "LnNrLmVlL3JlcG9zaXRvcnkvY3Jscy90ZXN0X2VlY2NyY2EuY3JsMA0GCSqG" +
                        "SIb3DQEBBQUAA4IBAQBdh5R23K7qkrO78j51xN6CR2qwxUcK/cgcTLWv0obP" +
                        "mJ7jRax3PX0pFhaUE6EhAR0dgS4u6XZrjPgVrt/mwq1h8lJP1MF2ueAHKyS0" +
                        "SGj7aFLkcC+ULwu1k6yiortFJ0Ds49ZGA+ioGzYWPQ+g1Zl4wSDIz52ot0cH" +
                        "Uijnf39Szq7E2z7MDfZkYg8HZeHrO493EFghXcnSH7J7z47cgP3GWFNUKv1V" +
                        "2c0eVE4OxRulZ3KmBLPWbJKZ0TyGa/Aooc+TorEjxz//WzcF/Sklp4FeD0MU" +
                        "39UURIlg7LfEcm832bPzZzVGFd4drBd5Dy0Uquu63kW7RDqr+wQFSxKr9DIH");

        tlCertSource.addCertificate(certToken, getCAServiceInfo(certToken));

        // TEST of KLASS3-SK 2010
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIExzCCA6+gAwIBAgIQIrzOHDuBOQRPabRVIaWqEzANBgkqhkiG9w0BAQUFADB9" +
                        "MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1" +
                        "czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290" +
                        "IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwHhcNMTIwMzIxMTA1ODI5WhcN" +
                        "MjUwMzIxMTA1ODI5WjB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlm" +
                        "aXRzZWVyaW1pc2tlc2t1czEhMB8GA1UECwwYU2VydGlmaXRzZWVyaW1pc3RlZW51" +
                        "c2VkMR8wHQYDVQQDDBZURVNUIG9mIEtMQVNTMy1TSyAyMDEwMIIBIjANBgkqhkiG" +
                        "9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7Ua/6IAAqXvy2COvRLW9yCSeImQ23XRAzf/x" +
                        "HmJ6fYiSs0LaR8c19IOSOkOarAgUrt0XDG5KWBdE5qwuVc0gQN9ZjYwmDg3dq4LV" +
                        "o89FdvATKk2Tao01Iu1N+2eGSEifgGBH5iWfYqu1hGJ2nJPHIG9bKXZXfw+ET2gy" +
                        "mO5bHnt8iOJmq+YynMXEvxtUTl2hKh0o6m28i3YKXru0jm5qe5/YCJ9HFw9yKn8e" +
                        "LnI2/9Jfruxe5F1AMOkVXhVtA57yeQARv18a8uEQZV0CS/WDmCPi+hl6GqSwhWZB" +
                        "dB9igOMsnZdNS1s7kr2/46doZQVPa2X3vJyYMTl/oaWB++VfAQIDAQABo4IBSTCC" +
                        "AUUwEgYDVR0TAQH/BAgwBgEB/wIBADAOBgNVHQ8BAf8EBAMCAcYwgZkGA1UdIASB" +
                        "kTCBjjCBiwYKKwYBBAHOHwMBATB9MCEGCCsGAQUFBwIBFhVodHRwczovL3d3dy5z" +
                        "ay5lZS9jcHMwWAYIKwYBBQUHAgIwTB5KAEEAaQBuAHUAbAB0ACAAdABlAHMAdABp" +
                        "AG0AaQBzAGUAawBzAC4AIABPAG4AbAB5ACAAZgBvAHIAIAB0AGUAcwB0AGkAbgBn" +
                        "AC4wHQYDVR0OBBYEFNFoQ5JN1Wc5Ii9tzYgGEg662Wp2MB8GA1UdIwQYMBaAFLU0" +
                        "Cp2lLxDF5yEOvsSxZUcbA3b+MEMGA1UdHwQ8MDowOKA2oDSGMmh0dHBzOi8vd3d3" +
                        "LnNrLmVlL3JlcG9zaXRvcnkvY3Jscy90ZXN0X2VlY2NyY2EuY3JsMA0GCSqGSIb3" +
                        "DQEBBQUAA4IBAQCiE8G8gwZpeeHm0PoqHd54/KbfH2cAklqO5rcg2m9fhhvPNtMQ" +
                        "9Wc19JWauE2YuNsnJNKetglUAnA/yd64DhQKBf+2JUgYJas4dTscRgXXlz8huuve" +
                        "nNUpfPd3iispVc2WNBQ2qjBEZXdqhbkG0/RgM42Hb28+1v23HsoLhCGY2YjHhYyn" +
                        "mOk/8BULI/ArsgA7FJflXi5Xp/cdC8BJQ87vtPlAnxm0axZMvASNXMUvvQTUjCTg" +
                        "0yczX3d8+I3EBNBlzfPMsyU1LCn6Opbs2/DGF/4enhRGk/49L6ltfOyOA73buSog" +
                        "S2JkvCweSx6Y2cs1fXVyFszm2HJmQgwbZYfR");

        tlCertSource.addCertificate(certToken, getCAServiceInfo(certToken));

        // TEST of SK OCSP RESPONDER 2011
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIEijCCA3KgAwIBAgIQaI8x6BnacYdNdNwlYnn/mzANBgkqhkiG9w0BAQUF" +
                        "ADB9MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1p" +
                        "c2tlc2t1czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENl" +
                        "bnRyZSBSb290IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwHhcNMTEw" +
                        "MzA3MTMyMjQ1WhcNMjQwOTA3MTIyMjQ1WjCBgzELMAkGA1UEBhMCRUUxIjAg" +
                        "BgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxDTALBgNVBAsMBE9D" +
                        "U1AxJzAlBgNVBAMMHlRFU1Qgb2YgU0sgT0NTUCBSRVNQT05ERVIgMjAxMTEY" +
                        "MBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMIIBIjANBgkqhkiG9w0BAQEFAAOC" +
                        "AQ8AMIIBCgKCAQEA0cw6Cja17BbYbHi6frwccDI4BIQLk/fiCE8L45os0xhP" +
                        "gEGR+EHE8LPCIqofPgf4gwN1vDE6cQNUlK0Od+Ush39i9Z45esnfpGq+2HsD" +
                        "JaFmFr5+uC1MEz5Kn1TazEvKbRjkGnSQ9BertlGer2BlU/kqOk5qA5RtJfhT" +
                        "0psc1ixKdPipv59wnf+nHx1+T+fPWndXVZLoDg4t3w8lIvIE/KhOSMlErvBI" +
                        "HIAKV7yH1hOxyeGLghqzMiAn3UeTEOgoOS9URv0C/T5C3mH+Y/uakMSxjNuz" +
                        "41PneimCzbEJZJRiEaMIj8qPAubcbL8GtY03MWmfNtX6/wh6u6TMfW8S2wID" +
                        "AQABo4H+MIH7MBYGA1UdJQEB/wQMMAoGCCsGAQUFBwMJMB0GA1UdDgQWBBR9" +
                        "/5CuRokEgGiqSzYuZGYAogl8TzCBoAYDVR0gBIGYMIGVMIGSBgorBgEEAc4f" +
                        "AwEBMIGDMFgGCCsGAQUFBwICMEweSgBBAGkAbgB1AGwAdAAgAHQAZQBzAHQA" +
                        "aQBtAGkAcwBlAGsAcwAuACAATwBuAGwAeQAgAGYAbwByACAAdABlAHMAdABp" +
                        "AG4AZwAuMCcGCCsGAQUFBwIBFhtodHRwOi8vd3d3LnNrLmVlL2FqYXRlbXBl" +
                        "bC8wHwYDVR0jBBgwFoAUtTQKnaUvEMXnIQ6+xLFlRxsDdv4wDQYJKoZIhvcN" +
                        "AQEFBQADggEBAAbaj7kTruTAPHqToye9ZtBdaJ3FZjiKug9/5RjsMwDpOeqF" +
                        "DqCorLd+DBI4tgdu0g4lhaI3aVnKdRBkGV18kqp84uU97JRFWQEf6H8hpJ9k" +
                        "/LzAACkP3tD+0ym+md532mV+nRz1Jj+RPLAUk9xYMV7KPczZN1xnl2wZDJwB" +
                        "bQpcSVH1DjlZv3tFLHBLIYTS6qOK4SxStcgRq7KdRczfW6mfXzTCRWM3G9nm" +
                        "Dei5Q3+XTED41j8szRWglzYf6zOv4djkja64WYraQ5zb4x8Xh7qTCk6UupZ7" +
                        "je+0oRfuz0h/3zyRdjcRPkjloSpQp/NG8Rmrcnr874p8d9fdwCrRI7U=");

        tlCertSource.addCertificate(certToken, getOCSPServiceInfo(certToken));

        // QuoVadis Time-Stamp Authority 1
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIGOzCCBSOgAwIBAgIUe6m/OP/GwmsrkHR8Mz8LJoNedfgwDQYJKoZIhvcNAQEL" +
                        "BQAwfzELMAkGA1UEBhMCQk0xGTAXBgNVBAoTEFF1b1ZhZGlzIExpbWl0ZWQxJTAj" +
                        "BgNVBAsTHFJvb3QgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkxLjAsBgNVBAMTJVF1" +
                        "b1ZhZGlzIFJvb3QgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTIwMjIwMTYy" +
                        "NTAwWhcNMjEwMzE3MTgzMzMzWjCBuTELMAkGA1UEBhMCQ0gxGTAXBgNVBAoTEFF1" +
                        "b1ZhZGlzIExpbWl0ZWQxHTAbBgNVBAsTFFRpbWUtc3RhbXAgQXV0aG9yaXR5MScw" +
                        "JQYDVQQLEx5uQ2lwaGVyIERTRSBFU046RkJDOS0zMDU2LUNFMEExJDAiBgNVBAsT" +
                        "GzEuMy42LjEuNC4xLjgwMjQuMC4yMDAwLjYuMDEhMB8GA1UEAxMYdHNhMDEucXVv" +
                        "dmFkaXNnbG9iYWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA" +
                        "xRhvUNoyEkX0cSQoOPpmnue7iF2o4mkdccfqJl5gZRkbIPoEUB5y70tnzBCBaCs8" +
                        "kxkkREPQoIOlL5bG+qj/j2hR6j2ZTmMAagplQ3hCRxkUj2wv1z8Va3k3fxLihh4z" +
                        "ElJHYw9LXmwWp0djSG3JxoVtu7tYlktoKsVVG1mznxs8ZA8D+uIPYxbUsME2k9lC" +
                        "1xiTGTtdfLBOdS5+NQmZw4HxaF+dycMcA94BhACv1me1T0yvC6GfzponBk7cg4WW" +
                        "BLk6BL4khofv0mujTvWJ5axzE+eGcHem+PbGS8b/fCv22l7kAKvmeiMhdnyvzFMG" +
                        "HPj747mI0U29VBqHawJYsQIDAQABo4ICcjCCAm4wOgYIKwYBBQUHAQEELjAsMCoG" +
                        "CCsGAQUFBzABhh5odHRwOi8vb2NzcC5xdW92YWRpc2dsb2JhbC5jb20wggEiBgNV" +
                        "HSAEggEZMIIBFTCCAREGCysGAQQBvlgAj1AGMIIBADCBxwYIKwYBBQUHAgIwgboa" +
                        "gbdSZWxpYW5jZSBvbiB0aGUgUXVvVmFkaXMgUm9vdCBDZXJ0aWZpY2F0ZSBieSBh" +
                        "bnkgcGFydHkgYXNzdW1lcyBhY2NlcHRhbmNlIG9mIHRoZSB0aGVuIGFwcGxpY2Fi" +
                        "bGUgc3RhbmRhcmQgdGVybXMgYW5kIGNvbmRpdGlvbnMgb2YgdXNlLCBhbmQgdGhl" +
                        "IFF1b1ZhZGlzIENlcnRpZmljYXRlIFByYWN0aWNlIFN0YXRlbWVudC4wNAYIKwYB" +
                        "BQUHAgEWKGh0dHA6Ly93d3cucXVvdmFkaXNnbG9iYWwuY29tL3JlcG9zaXRvcnkw" +
                        "LgYIKwYBBQUHAQMEIjAgMAoGCCsGAQUFBwsCMAgGBgQAjkYBATAIBgYEAI5GAQQw" +
                        "DgYDVR0PAQH/BAQDAgbAMBYGA1UdJQEB/wQMMAoGCCsGAQUFBwMIMDgGA1UdEgQx" +
                        "MC+kLTArMSkwJwYDVQQKEyBaZXJ0RVMgUmVjb2duaXRpb24gQm9keTogS1BNRyBB" +
                        "RzAfBgNVHSMEGDAWgBSLS23t0ym5BhnsOTmp8JeEasvv3zA4BgNVHR8EMTAvMC2g" +
                        "K6AphidodHRwOi8vY3JsLnF1b3ZhZGlzZ2xvYmFsLmNvbS9xdnJjYS5jcmwwHQYD" +
                        "VR0OBBYEFKbXP8sJeOWM8yiBjD3lz5XNrW9AMA0GCSqGSIb3DQEBCwUAA4IBAQBJ" +
                        "cm6H6CIxXc9hczAIQ6EJEHWLokhiVPj97Dar6Lm5hBATxkZOxZ+n/F8s3ccDt7Tv" +
                        "6qBB/1x5LlTz2leR/4YUxOTx1CIOwbeX6/aV3cMmKbpFqNaM8L29obWm6nm51kS1" +
                        "yyvQaJAAmVlAXRoj4TOlq/SLkUZU4n7mDB5oaZLgCRr45ZpsGiGnmPfLg2nMIXu9" +
                        "eUwQm/Xffp0+wRWXvZMc7UhLtJsrHFuHzFW2w8yLRzyC1RB8+syf9f5cweArBjuy" +
                        "VjdeCe3o0E9dUVSBgp4Ulu3x9hLJ9ps1+xt/HtM2VYEDiIlF5CLzyhm/0Egdss8o" +
                        "+TJRSQWq43roK5RW7Gle");

        tlCertSource.addCertificate(certToken, getCAServiceInfo(certToken));

        // QuoVadis Time-Stamp Authority 2
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIGOjCCBSKgAwIBAgIUZ0A6K0buOdtz0tj6CNRea1UKIF0wDQYJKoZIhvcNAQEL" +
                        "BQAwfzELMAkGA1UEBhMCQk0xGTAXBgNVBAoTEFF1b1ZhZGlzIExpbWl0ZWQxJTAj" +
                        "BgNVBAsTHFJvb3QgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkxLjAsBgNVBAMTJVF1" +
                        "b1ZhZGlzIFJvb3QgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTMwMTIyMTQ0" +
                        "OTU3WhcNMjEwMzE3MTgzMzMzWjCBuDELMAkGA1UEBhMCQ0gxGTAXBgNVBAoTEFF1" +
                        "b1ZhZGlzIExpbWl0ZWQxHTAbBgNVBAsTFFRpbWUtc3RhbXAgQXV0aG9yaXR5MSYw" +
                        "JAYDVQQLEx1UaGFsZXMgVFNTIEVTTjpCODdFLUQxMDctOTE3RjEkMCIGA1UECxMb" +
                        "MS4zLjYuMS40LjEuODAyNC4wLjIwMDAuNi4zMSEwHwYDVQQDExh0c2EwMi5xdW92" +
                        "YWRpc2dsb2JhbC5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDH" +
                        "uj2IlLT6l/ouvtvV4qqhZaTpfO3lxIuOeeN5wsMx/51wL8nTYdQSe7o92Pah/+v9" +
                        "Zbkw9poHoG3GYxm8mkK+tNj4pYCyF7iaiCkg4Frzcqw4r0NQhQ76RiWnfXZ1h7Id" +
                        "AErWnITY3GMPN8ALjp5YWCnZFkxeHz8tE22d48y6wlVhlYXDPnVm8/rAZQEBCcPe" +
                        "Xvb/V3FEGeADU6fRsTvOE2cqhgzIv6SQppZgUJEmQvEXZxXusPrPo/8Wg1hb44p0" +
                        "5U8WZXpWpq5XJvACGR7HQT0R8v7QfRH4JL6cKcMFYvNzXeDaczaecrUPKn3CkSWm" +
                        "6UKiFqQ5qgLtx/9cq52rAgMBAAGjggJyMIICbjA6BggrBgEFBQcBAQQuMCwwKgYI" +
                        "KwYBBQUHMAGGHmh0dHA6Ly9vY3NwLnF1b3ZhZGlzZ2xvYmFsLmNvbTCCASIGA1Ud" +
                        "IASCARkwggEVMIIBEQYLKwYBBAG+WACPUAYwggEAMIHHBggrBgEFBQcCAjCBuhqB" +
                        "t1JlbGlhbmNlIG9uIHRoZSBRdW9WYWRpcyBSb290IENlcnRpZmljYXRlIGJ5IGFu" +
                        "eSBwYXJ0eSBhc3N1bWVzIGFjY2VwdGFuY2Ugb2YgdGhlIHRoZW4gYXBwbGljYWJs" +
                        "ZSBzdGFuZGFyZCB0ZXJtcyBhbmQgY29uZGl0aW9ucyBvZiB1c2UsIGFuZCB0aGUg" +
                        "UXVvVmFkaXMgQ2VydGlmaWNhdGUgUHJhY3RpY2UgU3RhdGVtZW50LjA0BggrBgEF" +
                        "BQcCARYoaHR0cDovL3d3dy5xdW92YWRpc2dsb2JhbC5jb20vcmVwb3NpdG9yeTAu" +
                        "BggrBgEFBQcBAwQiMCAwCgYIKwYBBQUHCwEwCAYGBACORgEBMAgGBgQAjkYBBDAO" +
                        "BgNVHQ8BAf8EBAMCBsAwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwOAYDVR0SBDEw" +
                        "L6QtMCsxKTAnBgNVBAoTIFplcnRFUyBSZWNvZ25pdGlvbiBCb2R5OiBLUE1HIEFH" +
                        "MB8GA1UdIwQYMBaAFItLbe3TKbkGGew5Oanwl4Rqy+/fMDgGA1UdHwQxMC8wLaAr" +
                        "oCmGJ2h0dHA6Ly9jcmwucXVvdmFkaXNnbG9iYWwuY29tL3F2cmNhLmNybDAdBgNV" +
                        "HQ4EFgQUo19dd17CLC4cG3Yyh97l2vwu/V0wDQYJKoZIhvcNAQELBQADggEBALa5" +
                        "LddowvCFD38SZ7CUrreBnke3QLDkWP62qRzANckbmcewWHFtuEDxNxnOlpxUoA6L" +
                        "O05Li4VVKRWL4lul+ectnFzruda9GyrP6+WS9KTfSYBf2wP2pqhlbLY4BUyqYxXU" +
                        "OqHS8yheps70OEb76L2caBcIrOW6cHj08MJPAsY8QxF8MglmdG1InImKB5g0STBe" +
                        "ZNXUqSsNEjTbG+M1XF+3dEgM7MhnwsxjZZv7FKclEw9j84vCI4+tWwV3S/MT7TJ+" +
                        "0QFoiIbPLv8vFH/ObkV/tivBelxHNYZt0JrgNQQAG7TcQ4GSxWzxmXU35BHvaOuj" +
                        "h2qLqIv3l1rSlPt82HY=");

        tlCertSource.addCertificate(certToken, getCAServiceInfo(certToken));

        LOGGER.info("Finished Loading Estonian Test Certificates");
    }

    private ServiceInfo getCAServiceInfo(CertificateToken certToken) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setStatus("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision");
        serviceInfo.setType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        serviceInfo.setStatusStartDate(certToken.getCertificate().getNotBefore());
        return serviceInfo;
    }

    private ServiceInfo getOCSPServiceInfo(CertificateToken certToken) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setStatus("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision");
        serviceInfo.setType("http://uri.etsi.org/TrstSvc/Svctype/OCSP/QC");
        serviceInfo.setStatusStartDate(certToken.getCertificate().getNotBefore());
        return serviceInfo;
    }

    @Autowired
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListSource) {
        this.trustedListSource = trustedListSource;
    }

    @Autowired
    public void setTslLoaderConfigurationProperties(TSLLoaderConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Autowired
    public void setKeyStoreCertificateSource(KeyStoreCertificateSource keyStoreCertificateSource) {
        this.keyStoreCertificateSource = keyStoreCertificateSource;
    }

}
