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


import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.tsl.*;
import eu.europa.esig.dss.util.TimeDependentValues;
import eu.europa.esig.dss.x509.CertificateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("test")
public class CustomCertificatesLoader {

    private static final String QC_WITH_QSCD = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithQSCD";
    private static final String QC_STATEMENT = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCStatement";
    private static final String QC_FOR_ESIG = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESig";

    private static final String CA_QC = "http://uri.etsi.org/TrstSvc/Svctype/CA/QC";
    private static final String OCSP_QC = "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC";
    private static final String UNDER_SUPERVISION = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision";
    private static final String GRANTED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted";
    private static final Logger LOGGER = LoggerFactory.getLogger(TSLLoader.class);

    private TrustedListsCertificateSource trustedListSource;

    @PostConstruct
    public void init() {
        loadEstonianTestCertificates(trustedListSource);
    }

    private void loadEstonianTestCertificates(TrustedListsCertificateSource tlCertSource) {
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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of EE Certification Centre Root CA"));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of ESTEID-SK 2011"));

        // Nortal NQSK16 Test Cert Signing
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFWjCCA0KgAwIBAgIQWU++Mz4tewZXz+854YWLgzANBgkqhkiG9w0BAQUFADBD\n" +
                        "MRIwEAYDVQQKDAlTYWZlbGF5ZXIxGDAWBgNVBAsMD0tleU9uZSBUcmFpbmluZzET\n" +
                        "MBEGA1UEAwwKTXkgUm9vdCBDQTAeFw0xNjA5MDcxMDQzMDVaFw0yNTAxMDExMTQy\n" +
                        "MzRaMCoxKDAmBgNVBAMMH05vcnRhbCBOUVNLMTYgVGVzdCBDZXJ0IFNpZ25pbmcw\n" +
                        "ggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQDXG5PMuMLq6q9fwjBGJmX8\n" +
                        "VtsYkgqkwLK/9Yy2Dr4k92K8AHeLG22h8BCHujV3ssHQp0rKrAitUKQahCAxGbZh\n" +
                        "KVZXTT6yzpcYycL+csaLlD58K2JGLUwkb8OJw6Qp/wy2VYMWI/KmT0/LTsTBTMhj\n" +
                        "MRm3U5YPrV308gbeyyFtmczEst/dm+hKhztu8x56DC++uIK/d4KFNE5Xiv1PIEbT\n" +
                        "GwXhXTNSozkUiTeg6usMShpYfr2CJuRwJW/wvlKOYYOFgB8KIq6RPtzsSeVzeyYF\n" +
                        "KKyqEP7vV3XcwDsyNglLiv5SdPUz4BhgvXLwMFJVGBWM209EZR3su8PVVSqEjuEP\n" +
                        "vsu80PCqaaPbddB8HDkirSH6z5jMAS0nW+ER+OPiS/fUOtBjL9IVW9x5ed3hxH45\n" +
                        "S4qsc9YroNt2AJ4ePaOHSVIp/N3NuN7FOlPoWyzzVzayOc9mXrOq+pGNfqhc2Sqw\n" +
                        "nYB3Y2WHqKoajnRjn4NAK/sRCG/nqFP6JA7Awi1J8kAIkUFEjD8+VTp/+cX0L3Qg\n" +
                        "CM9hyo0ZNO18W9ibZFvWANUNhp6rOm7ApiKz1uXUgGG8N/gMTBvlHd8yZuPZzwhE\n" +
                        "NsWKZT4R+ER5n/BebH7B/yMCyboy2YIKDK4h0zD6AkajsUugSS/iqMVStbor3wfl\n" +
                        "3Pmz5bqwBhK3lKHGLV9yNQIDAQABo2MwYTAPBgNVHRMBAf8EBTADAQH/MA4GA1Ud\n" +
                        "DwEB/wQEAwICBDAdBgNVHQ4EFgQU7EWOweBYUfzFSGWcIR8l9RlZYb0wHwYDVR0j\n" +
                        "BBgwFoAUX2PMACLCN34rf3QzgqYHVblJleswDQYJKoZIhvcNAQEFBQADggIBADFf\n" +
                        "97bqDOXpfeAZ/wlXF1idaG19lGehjEKJibDbNfQ58gOrk2vxAz5PO8AYpW9L3CGn\n" +
                        "r7nEzKXuam1WpdhQo8UvL1RDa0FiKhltvanmj/bh8xeLaQIBUaBZcCSz7dGCeH50\n" +
                        "FaQU4gbSajRlURdCfajWhyGF1sc6g+nGRPf9dvZiGdYh8xSxf77nWbsiYcYuYoiy\n" +
                        "daOEvoxqvsOUoiw99POG3SVnBpiJaETM3xpjUpdnyNBjIa0vgrLb8ChDOptunkcS\n" +
                        "QicjK06FUai9tNZpJz13dJEq/E4R3mOnIevEvX+GdKZFOZCPsu+mLp2KU31m7Tsw\n" +
                        "XrZAgBeOAQO8Rg1gOupLH+7ZYnAHJG0jJ4mJ/EFzXD/bNvQLEiLXl96GglabJYiY\n" +
                        "efHzacf1kGWWOl5UbYFBORBa9daI2sEK3zA10ah4ku0yBMvG6vIJhVOov4n7eiRs\n" +
                        "PFWVhduaYxDVPlc1e/AIWSuCyvGJHxDp1PQj4Z7BTN6AwRZV3DAnftxWuHj9FTpu\n" +
                        "5PRkpGqi3BSbw6adlFgPZnTTeTkbiE4+XMygrJGRbEa587KWSbwiINpGoeBE1AbZ\n" +
                        "26x1lOcCk0KRBr/mBk9gaC0TxmYhuum99V5+fM5sJ6WwFRS1ruLyt1piQiATIRVe\n" +
                        "pcPZlmxrjmZcfQ+dp1jWj3cS7pJ9mCZsr5H74U3K");

        tlCertSource.addCertificate(certToken, getCAServiceInfo(GRANTED, certToken, "Nortal NQSK16 Test Cert Signing"));
        tlCertSource.addCertificate(certToken, getCAServiceInfo(UNDER_SUPERVISION, certToken, "Nortal NQSK16 Test Cert Signing"));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of KLASS3-SK 2010"));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "QuoVadis Time-Stamp Authority 1"));

        // DEMO of SK TSA 2014
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIEFTCCAv2gAwIBAgIQTqz7bCP8W45UBZa7tztTTDANBgkqhkiG9w0BAQsFADB9\n" +
                        "MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1\n" +
                        "czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290\n" +
                        "IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwHhcNMTQwOTAyMTAwNjUxWhcN\n" +
                        "MjQwOTAyMTAwNjUxWjBdMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlm\n" +
                        "aXRzZWVyaW1pc2tlc2t1czEMMAoGA1UECwwDVFNBMRwwGgYDVQQDDBNERU1PIG9m\n" +
                        "IFNLIFRTQSAyMDE0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAysgr\n" +
                        "VnVPxH8jNgCsJw0y+7fmmBDTM/tNB+xielnP9KcuQ+nyTgNu1JMpnry7Rh4ndr54\n" +
                        "rPLXNGVdb/vsgsi8B558DisPVUn3Rur3/8XQ+BCkhTQIg1cSmyCsWxJgeaQKJi6W\n" +
                        "GVaQWB2he35aVhL5F6ae/gzXT3sGGwnWujZkY9o5RapGV15+/b7Uv+7jWYFAxcD6\n" +
                        "ba5jI00RY/gmsWwKb226Rnz/pXKDBfuN3ox7y5/lZf5+MyIcVe1qJe7VAJGpJFjN\n" +
                        "q+BEEdvfqvJ1PiGQEDJAPhRqahVjBSzqZhJQoL3HI42NRCFwarvdnZYoCPxjeYpA\n" +
                        "ynTHgNR7kKGX1iQ8OQIDAQABo4GwMIGtMA4GA1UdDwEB/wQEAwIGwDAWBgNVHSUB\n" +
                        "Af8EDDAKBggrBgEFBQcDCDAdBgNVHQ4EFgQUJwScZQxzlzySVqZXviXpKZDV5Nww\n" +
                        "HwYDVR0jBBgwFoAUtTQKnaUvEMXnIQ6+xLFlRxsDdv4wQwYDVR0fBDwwOjA4oDag\n" +
                        "NIYyaHR0cHM6Ly93d3cuc2suZWUvcmVwb3NpdG9yeS9jcmxzL3Rlc3RfZWVjY3Jj\n" +
                        "YS5jcmwwDQYJKoZIhvcNAQELBQADggEBAIq02SVKwP1UolKjqAQe7SVY/Kgi++G2\n" +
                        "kqAd40UmMqa94GTu91LFZR5TvdoyZjjnQ2ioXh5CV2lflUy/lUrZMDpqEe7IbjZW\n" +
                        "5+b9n5aBvXYJgDua9SYjMOrcy3siytqq8UbNgh79ubYgWhHhJSnLWK5YJ+5vQjTp\n" +
                        "OMdRsLp/D+FhTUa6mP0UDY+U82/tFufkd9HW4zbalUWhQgnNYI3oo0CsZ0HExuyn\n" +
                        "OOZmM1Bf8PzD6etlLSKkYB+mB77Omqgflzz+Jjyh45o+305MRzHDFeJZx7WxC+XT\n" +
                        "NWQ0ZFTFfc0ozxxzUWUlfNfpWyQh3+4LbeSQRWrNkbNRfCpYotyM6AY=");

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "DEMO of SK TSA 2014"));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "QuoVadis Time-Stamp Authority 2"));

        //Management CA for soft cert
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIDWzCCAkOgAwIBAgIIM8q/reh6L2QwDQYJKoZIhvcNAQELBQAwOzEVMBMGA1UE" +
                        "AwwMTWFuYWdlbWVudENBMRUwEwYDVQQKDAxFSkJDQSBTYW1wbGUxCzAJBgNVBAYT" +
                        "AlNFMB4XDTE0MTAyMDIxMzYwNFoXDTI0MTAxNzIxMzYwNFowOzEVMBMGA1UEAwwM" +
                        "TWFuYWdlbWVudENBMRUwEwYDVQQKDAxFSkJDQSBTYW1wbGUxCzAJBgNVBAYTAlNF" +
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgWlIWRGePfPPJNUPsT+X" +
                        "WdYN/ZP0IvNqODFRf8mGuZ/Pj5tmdCQBEwFKNP+R8VkXvYRXkZDQ4WQkul3MZ7c8" +
                        "bxBcUN3K+nzP3JEafWR4E+zWHNSjB0VPDIGNl33zi1zD6c3eAdVwnm9h9eHFoM6V" +
                        "b45BR6E9wrp/YnkDVq9bBBL6CzXlkLYw6VSPOzs22KScTl2hGB4NRdfoermyx8JV" +
                        "o1NreK9SlKINit+/M7hBTQ5p3hYOL2SulVkgw0/38Qc0sS7Emps8Ejr38BgPNQji" +
                        "jkuXwpfRujROrVGExhBVdLNvZpERxpr03PzvhiX73nlzoCLw+uC4sm8uS/NdR6dF" +
                        "dQIDAQABo2MwYTAdBgNVHQ4EFgQUPW27M+6uIGgi3cf1zgBipX/FI8swDwYDVR0T" +
                        "AQH/BAUwAwEB/zAfBgNVHSMEGDAWgBQ9bbsz7q4gaCLdx/XOAGKlf8UjyzAOBgNV" +
                        "HQ8BAf8EBAMCAYYwDQYJKoZIhvcNAQELBQADggEBAF/ezoK6KPtNWEcXwWxAgZoc" +
                        "f1ZrE6rM+TZ20jFKHVlQu1u4rluAxnxORLJwKaZNVbO1FYf2Jh3ksfgVoB7K2i15" +
                        "RPcqJovMiZZ8koWJuMv2UJDVCwRPP1xXupmUc4lDv32TNIK3LorvhgoJs+yMQSdz" +
                        "jva6a6MGLHF2T6kFiQeMVobGM6GF5WJIWGA6oPbwwaBjHk7+3jY38wUNMre/7um3" +
                        "B7TYeIrgMTT01SNXY0cC+cWAqHot6NWZQtKOGwu8TlqTjkZd7E0sq3a6QWBb5/22" +
                        "0xDd5B09RzzLbIhKS/PKsdVR/UQNdYOhQ/H3kBRCJeMENNRi2iuUtw2SAyRBwHY=");

        tlCertSource.addCertificate(certToken, getCAServiceInfo(UNDER_SUPERVISION, certToken, "Management CA"));

        LOGGER.info("Finished Loading Estonian Test Certificates");
    }

    private ServiceInfo getCAServiceInfo(String status , CertificateToken certToken, String serviceName) {
        ServiceInfo serviceInfo = new ServiceInfo();
        Map<String, List<Condition>> qualifiersAndConditions = new HashMap<>();
        serviceInfo.setStatus(getServiceInfoStatuses(status, certToken, CA_QC, qualifiersAndConditions));

        serviceInfo.setServiceName(serviceName);
        return serviceInfo;
    }

    private ServiceInfo getCAServiceInfoWithQcConditions(CertificateToken certToken, String serviceName) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceName(serviceName);
        Map<String, List<Condition>> qualifiersAndConditions = new HashMap<>();
        qualifiersAndConditions.put(QC_WITH_QSCD, Collections.singletonList(createDigitalSignatureOrNonRepudiationListCondition()));
        qualifiersAndConditions.put(QC_STATEMENT, Collections.singletonList(createNonRepudiationCriteriaListCondition()));
        qualifiersAndConditions.put(QC_FOR_ESIG, Collections.singletonList(createNonRepudiationCriteriaListCondition()));
        serviceInfo.setStatus(getServiceInfoStatuses(UNDER_SUPERVISION, certToken, CA_QC, qualifiersAndConditions));

        return serviceInfo;
    }

    private CriteriaListCondition createNonRepudiationCriteriaListCondition() {
        CriteriaListCondition condition = new CriteriaListCondition(MatchingCriteriaIndicator.all);
        condition.addChild(new KeyUsageCondition(KeyUsageBit.nonRepudiation, true));
        return condition;
    }

    private CriteriaListCondition createDigitalSignatureOrNonRepudiationListCondition() {
        CriteriaListCondition condition = new CriteriaListCondition(MatchingCriteriaIndicator.atLeastOne);
        condition.addChild(new KeyUsageCondition(KeyUsageBit.digitalSignature, true));
        condition.addChild(new KeyUsageCondition(KeyUsageBit.nonRepudiation, true));
        return condition;
    }


    private ServiceInfo getOCSPServiceInfo(CertificateToken certToken) {
        ServiceInfo serviceInfo = new ServiceInfo();
        Map<String, List<Condition>> qualifiersAndConditions = new HashMap<>();
        serviceInfo.setStatus(getServiceInfoStatuses(UNDER_SUPERVISION, certToken, OCSP_QC, qualifiersAndConditions));

        return serviceInfo;
    }

    private TimeDependentValues<ServiceInfoStatus> getServiceInfoStatuses(String status, CertificateToken certToken, String type, Map<String, List<Condition>> qualifiersAndConditions) {
        return new TimeDependentValues(Collections.singletonList(createUnderSupervisionStatus(status, certToken, type, qualifiersAndConditions)));
    }

    private ServiceInfoStatus createUnderSupervisionStatus(String status, CertificateToken certToken, String type, Map<String, List<Condition>> qualifiersAndConditions) {
        return new ServiceInfoStatus(type, status, qualifiersAndConditions, null, null, certToken.getCertificate().getNotBefore(), null);
    }

    @Autowired
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListSource) {
        this.trustedListSource = trustedListSource;
    }

}
