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

import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.tsl.*;
import eu.europa.esig.dss.util.TimeDependentValues;
import eu.europa.esig.dss.validation.process.qualification.EIDASUtils;
import eu.europa.esig.dss.x509.CertificateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Profile("test")
public class CustomCertificatesLoader implements CertificatesLoader{

    private static final String QC_WITH_QSCD = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithQSCD";
    private static final String QC_STATEMENT = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCStatement";
    private static final String QC_FOR_ESIG = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESig";

    private static final String CA_QC = "http://uri.etsi.org/TrstSvc/Svctype/CA/QC";
    private static final String OCSP_QC = "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC";
    private static final Logger LOGGER = LoggerFactory.getLogger(TSLLoader.class);

    public void loadExtraCertificates(TrustedListsCertificateSource tlCertSource) {
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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of EE Certification Centre Root CA", getServiceInfoStartDate("2018-11-02")));

        // TEST of EE-GovCA2018
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFLDCCBI2gAwIBAgIQImvqKVwtGyZbh+ecdKPc7zAKBggqhkjOPQQDBDBiMQsw\n" +
                        "CQYDVQQGEwJFRTEbMBkGA1UECgwSU0sgSUQgU29sdXRpb25zIEFTMRcwFQYDVQRh\n" +
                        "DA5OVFJFRS0xMDc0NzAxMzEdMBsGA1UEAwwUVEVTVCBvZiBFRS1Hb3ZDQTIwMTgw\n" +
                        "HhcNMTgwODMwMTI0ODI4WhcNMzMwODMwMTI0ODI4WjBiMQswCQYDVQQGEwJFRTEb\n" +
                        "MBkGA1UECgwSU0sgSUQgU29sdXRpb25zIEFTMRcwFQYDVQRhDA5OVFJFRS0xMDc0\n" +
                        "NzAxMzEdMBsGA1UEAwwUVEVTVCBvZiBFRS1Hb3ZDQTIwMTgwgZswEAYHKoZIzj0C\n" +
                        "AQYFK4EEACMDgYYABABZN0DFpEKsj3SzsySoR/bcwAUoLc+S2HrvHY0xIDkFFTtU\n" +
                        "QXfjxXyexNIx+ALe2IYJZLTl0T79C5by4/mO/5H7UgCxZZCRKtdcKqSGYJOVpT0X\n" +
                        "oA51yX8eBk8aPVrTcwABcBhU6nTNGEoNXfeS7mrZB6Gs3eFxEVdejIEjNObWVFYM\n" +
                        "bqOCAuAwggLcMBIGA1UdEwEB/wQIMAYBAf8CAQEwDgYDVR0PAQH/BAQDAgEGMDQG\n" +
                        "A1UdJQEB/wQqMCgGCCsGAQUFBwMJBggrBgEFBQcDAgYIKwYBBQUHAwQGCCsGAQUF\n" +
                        "BwMBMB0GA1UdDgQWBBR/DHDY9OWPAXfux20pKbn0yfxqwDAfBgNVHSMEGDAWgBR/\n" +
                        "DHDY9OWPAXfux20pKbn0yfxqwDCCAiQGA1UdIASCAhswggIXMAgGBgQAj3oBAjAJ\n" +
                        "BgcEAIvsQAECMDIGCysGAQQBg5EhAQIBMCMwIQYIKwYBBQUHAgEWFWh0dHBzOi8v\n" +
                        "d3d3LnNrLmVlL0NQUzANBgsrBgEEAYORIQECAjANBgsrBgEEAYORfwECATANBgsr\n" +
                        "BgEEAYORIQECBTANBgsrBgEEAYORIQECBjANBgsrBgEEAYORIQECBzANBgsrBgEE\n" +
                        "AYORIQECAzANBgsrBgEEAYORIQECBDANBgsrBgEEAYORIQECCDANBgsrBgEEAYOR\n" +
                        "IQECCTANBgsrBgEEAYORIQECCjANBgsrBgEEAYORIQECCzANBgsrBgEEAYORIQEC\n" +
                        "DDANBgsrBgEEAYORIQECDTANBgsrBgEEAYORIQECDjANBgsrBgEEAYORIQECDzAN\n" +
                        "BgsrBgEEAYORIQECEDANBgsrBgEEAYORIQECETANBgsrBgEEAYORIQECEjANBgsr\n" +
                        "BgEEAYORIQECEzANBgsrBgEEAYORIQECFDANBgsrBgEEAYORfwECAjANBgsrBgEE\n" +
                        "AYORfwECAzANBgsrBgEEAYORfwECBDANBgsrBgEEAYORfwECBTANBgsrBgEEAYOR\n" +
                        "fwECBjBVBgorBgEEAYORIQoBMEcwIQYIKwYBBQUHAgEWFWh0dHBzOi8vd3d3LnNr\n" +
                        "LmVlL0NQUzAiBggrBgEFBQcCAjAWGhRURVNUIG9mIEVFLUdvdkNBMjAxODAYBggr\n" +
                        "BgEFBQcBAwQMMAowCAYGBACORgEBMAoGCCqGSM49BAMEA4GMADCBiAJCAeTjfRrM\n" +
                        "t+4ecVYozAfdpTjCikf332XcuRkuJ6fbLqqMm7C3v/d5ebyOqvDG6wWAp8Z0GZA5\n" +
                        "ONIvS2rm8kJ7HR5tAkIAoFn7n5ZW62dXMmPk+LReR1hUyTpxrxC31QjqvMqM2AbM\n" +
                        "8luw0f/AaC5qsEdwKrKT+p1xvnjSyIVfcMiu6Q3T2EE=");

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of EE-GovCA2018", getServiceInfoStartDate("2018-11-02")));

        // TEST of ESTEID2018
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFfDCCBN2gAwIBAgIQNhjzSfd2UEpbkO14EY4ORTAKBggqhkjOPQQDBDBiMQsw\n" +
                        "CQYDVQQGEwJFRTEbMBkGA1UECgwSU0sgSUQgU29sdXRpb25zIEFTMRcwFQYDVQRh\n" +
                        "DA5OVFJFRS0xMDc0NzAxMzEdMBsGA1UEAwwUVEVTVCBvZiBFRS1Hb3ZDQTIwMTgw\n" +
                        "HhcNMTgwOTA2MDkwMzUyWhcNMzMwODMwMTI0ODI4WjBgMQswCQYDVQQGEwJFRTEb\n" +
                        "MBkGA1UECgwSU0sgSUQgU29sdXRpb25zIEFTMRcwFQYDVQRhDA5OVFJFRS0xMDc0\n" +
                        "NzAxMzEbMBkGA1UEAwwSVEVTVCBvZiBFU1RFSUQyMDE4MIGbMBAGByqGSM49AgEG\n" +
                        "BSuBBAAjA4GGAAQBxYug4cEqwmIj+3TVaUlhfxCV9FQgfuglC2/0Ux1Ieqw11mDj\n" +
                        "NvnGJhkWxaLbWJi7QtthMG5R104l7Np7lBevrBgBDtfgja9e3MLTQkY+cFS+UQxj\n" +
                        "t9ZihTUJVsR7lowYlaGEiqqsGbEhlwfu27Xsm8b2rhSiTOvNdjTtG57NnwVAX+ij\n" +
                        "ggMyMIIDLjAfBgNVHSMEGDAWgBR/DHDY9OWPAXfux20pKbn0yfxqwDAdBgNVHQ4E\n" +
                        "FgQUwISZKcROnzsCNPaZ4QpWAAgpPnswDgYDVR0PAQH/BAQDAgEGMBIGA1UdEwEB\n" +
                        "/wQIMAYBAf8CAQAwggHNBgNVHSAEggHEMIIBwDAIBgYEAI96AQIwCQYHBACL7EAB\n" +
                        "AjAyBgsrBgEEAYORIQECATAjMCEGCCsGAQUFBwIBFhVodHRwczovL3d3dy5zay5l\n" +
                        "ZS9DUFMwDQYLKwYBBAGDkSEBAgIwDQYLKwYBBAGDkX8BAgEwDQYLKwYBBAGDkSEB\n" +
                        "AgUwDQYLKwYBBAGDkSEBAgYwDQYLKwYBBAGDkSEBAgcwDQYLKwYBBAGDkSEBAgMw\n" +
                        "DQYLKwYBBAGDkSEBAgQwDQYLKwYBBAGDkSEBAggwDQYLKwYBBAGDkSEBAgkwDQYL\n" +
                        "KwYBBAGDkSEBAgowDQYLKwYBBAGDkSEBAgswDQYLKwYBBAGDkSEBAgwwDQYLKwYB\n" +
                        "BAGDkSEBAg0wDQYLKwYBBAGDkSEBAg4wDQYLKwYBBAGDkSEBAg8wDQYLKwYBBAGD\n" +
                        "kSEBAhAwDQYLKwYBBAGDkSEBAhEwDQYLKwYBBAGDkSEBAhIwDQYLKwYBBAGDkSEB\n" +
                        "AhMwDQYLKwYBBAGDkSEBAhQwDQYLKwYBBAGDkX8BAgIwDQYLKwYBBAGDkX8BAgMw\n" +
                        "DQYLKwYBBAGDkX8BAgQwDQYLKwYBBAGDkX8BAgUwDQYLKwYBBAGDkX8BAgYwKgYD\n" +
                        "VR0lAQH/BCAwHgYIKwYBBQUHAwkGCCsGAQUFBwMCBggrBgEFBQcDBDB3BggrBgEF\n" +
                        "BQcBAQRrMGkwLgYIKwYBBQUHMAGGImh0dHA6Ly9haWEuZGVtby5zay5lZS9lZS1n\n" +
                        "b3ZjYTIwMTgwNwYIKwYBBQUHMAKGK2h0dHA6Ly9jLnNrLmVlL1Rlc3Rfb2ZfRUUt\n" +
                        "R292Q0EyMDE4LmRlci5jcnQwGAYIKwYBBQUHAQMEDDAKMAgGBgQAjkYBATA4BgNV\n" +
                        "HR8EMTAvMC2gK6AphidodHRwOi8vYy5zay5lZS9UZXN0X29mX0VFLUdvdkNBMjAx\n" +
                        "OC5jcmwwCgYIKoZIzj0EAwQDgYwAMIGIAkIBIF+LqytyaV4o5wUSm30VysB8LdWt\n" +
                        "oOrzNq2QhB6tGv4slg5z+CR58e60eRFqNxT7eccA/HgoPWs0B1Z+L067qtUCQgCB\n" +
                        "8OP0kHx/j1t7htN2CXjpSjGFZw5TTI4s1eGyTbe0UJRBXEkUKfFbZVmzGPFPprwU\n" +
                        "dSPi8PpO7+xGBYlFHA4z+Q==");

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of ESTEID2018", getServiceInfoStartDate("2018-04-05")));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of ESTEID-SK 2011", getServiceInfoStartDate("2016-06-30")));

        // TEST of ESTEID-SK 2015
        certToken = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIGgzCCBWugAwIBAgIQEDb9gCZi4PdWc7IoNVIbsTANBgkqhkiG9w0BAQwFADB9\n" +
                        "MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1\n" +
                        "czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290\n" +
                        "IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwIBcNMTUxMjE4MDcxMzQ0WhgP\n" +
                        "MjAzMDEyMTcyMzU5NTlaMGsxCzAJBgNVBAYTAkVFMSIwIAYDVQQKDBlBUyBTZXJ0\n" +
                        "aWZpdHNlZXJpbWlza2Vza3VzMRcwFQYDVQRhDA5OVFJFRS0xMDc0NzAxMzEfMB0G\n" +
                        "A1UEAwwWVEVTVCBvZiBFU1RFSUQtU0sgMjAxNTCCAiIwDQYJKoZIhvcNAQEBBQAD\n" +
                        "ggIPADCCAgoCggIBAMTeAFvLxmAeaOsRKaf+hlkOhW+CdEilmUIKWs+qCWVq+w8E\n" +
                        "8PA/TohAZdUcO4KFXothmPDmfOCb0ExXcnOPCr2NndavzB39htlyYKYxkOkZi3pL\n" +
                        "z8bZg/HvpBoy8KIg0sYdbhVPYHf6i7fuJjDac4zN1vKdVQXA6Tv5wS/e90/ZyF95\n" +
                        "5vycxdNLticdozm5yCDMNgsEji6QNA1zIi3+C2YmnDXx6VyxhuC2R3q0xNkwtJ4e\n" +
                        "zs1RZGxWokTNPzQc3ilGhEJlVsS8vP624hUHwufQnwrKWpc3+D+plMIO0j3E+hmh\n" +
                        "46gIadDRweFR/dzb+CIBHRaFh0LEBjd/cDFQlBI+E8vpkhqeWp6rp1xwnhCL201M\n" +
                        "3E1E1Mw+51Xqj7WOfY0TzjOmQJy8WJPEwU2m44KxW1SnpeEBVkgb4XYFeQHAllc7\n" +
                        "J7JDv50BoIPpecgaqn1vKR7l//wDsL0MN1tDlBhl3x7TJ/fwMnwB1E3zVZR74TUZ\n" +
                        "h5J49CAcFrfM4RmP/0hcDW8+4wNWMg2Qgst2qmPZmHCI/OJt5yMt0Ud5yPF8AWxV\n" +
                        "ot3TxOBGjMiM8m6WsksFsQxp5WtA0DANGXIIfydTaTV16Mg+KpYVqFKxkvFBmfVp\n" +
                        "6xApMaFl3dY/m56O9JHEqFpBDF+uDQIMjFJxJ4Pt7Mdk40zfL4PSw9Qco2T3AgMB\n" +
                        "AAGjggINMIICCTAfBgNVHSMEGDAWgBS1NAqdpS8QxechDr7EsWVHGwN2/jAdBgNV\n" +
                        "HQ4EFgQUScDyRDll1ZtGOw04YIOx1i0ohqYwDgYDVR0PAQH/BAQDAgEGMGYGA1Ud\n" +
                        "IARfMF0wMQYKKwYBBAHOHwMBATAjMCEGCCsGAQUFBwIBFhVodHRwczovL3d3dy5z\n" +
                        "ay5lZS9DUFMwDAYKKwYBBAHOHwMBAjAMBgorBgEEAc4fAwEDMAwGCisGAQQBzh8D\n" +
                        "AQQwEgYDVR0TAQH/BAgwBgEB/wIBADBBBgNVHR4EOjA4oTYwBIICIiIwCocIAAAA\n" +
                        "AAAAAAAwIocgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwJwYDVR0l\n" +
                        "BCAwHgYIKwYBBQUHAwkGCCsGAQUFBwMCBggrBgEFBQcDBDCBiQYIKwYBBQUHAQEE\n" +
                        "fTB7MCUGCCsGAQUFBzABhhlodHRwOi8vZGVtby5zay5lZS9jYV9vY3NwMFIGCCsG\n" +
                        "AQUFBzAChkZodHRwOi8vd3d3LnNrLmVlL2NlcnRzL1RFU1Rfb2ZfRUVfQ2VydGlm\n" +
                        "aWNhdGlvbl9DZW50cmVfUm9vdF9DQS5kZXIuY3J0MEMGA1UdHwQ8MDowOKA2oDSG\n" +
                        "Mmh0dHBzOi8vd3d3LnNrLmVlL3JlcG9zaXRvcnkvY3Jscy90ZXN0X2VlY2NyY2Eu\n" +
                        "Y3JsMA0GCSqGSIb3DQEBDAUAA4IBAQDBOYTpbbQuoJKAmtDPpAomDd9mKZCarIPx\n" +
                        "AH8UXphSndMqOmIUA4oQMrLcZ6a0rMyCFR8x4NX7abc8T81cvgUAWjfNFn8+bi6+\n" +
                        "DgbjhYY+wZ010MHHdUo2xPajfog8cDWJPkmz+9PAdyjzhb1eYoEnm5D6o4hZQCiR\n" +
                        "yPnOKp7LZcpsVz1IFXsqP7M5WgHk0SqY1vs+Yhu7zWPSNYFIzNNXGoUtfKhhkHiR\n" +
                        "WFX/wdzr3fqeaQ3gs/PyD53YuJXRzFrktgJJoJWnHEYIhEwbai9+OeKr4L4kTkxv\n" +
                        "PKTyjjpLKcjUk0Y0cxg7BuzwevonyBtL72b/FVs6XsXJJqCa3W4T");

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of ESTEID-SK 2015", getServiceInfoStartDate("2016-06-30")));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfo(certToken, "Nortal NQSK16 Test Cert Signing", getServiceInfoStartDate("2016-06-30")));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "TEST of KLASS3-SK 2010", getServiceInfoStartDate("2016-06-30")));

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

        tlCertSource.addCertificate(certToken, getOCSPServiceInfo(certToken, "TEST of SK OCSP RESPONDER 2011", getServiceInfoStartDate("2016-06-30")));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "QuoVadis Time-Stamp Authority 1", getServiceInfoStartDate("2018-11-02")));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "DEMO of SK TSA 2014", getServiceInfoStartDate("2016-06-30")));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfoWithQcConditions(certToken, "QuoVadis Time-Stamp Authority 2", getServiceInfoStartDate("2018-11-02")));

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

        tlCertSource.addCertificate(certToken, getCAServiceInfo(certToken, "Management CA", getServiceInfoStartDate("2018-11-02")));

        LOGGER.info("Finished Loading Estonian Test Certificates");
    }

    private List<ServiceInfo> getCAServiceInfo(CertificateToken certToken, String serviceName, Date statusStartDate) {
        ServiceInfo serviceInfo = new ServiceInfo();

        Map<String, List<Condition>> qualifiersAndConditions = new HashMap<>();
        serviceInfo.setStatus(getServiceInfoStatuses(serviceName, certToken, CA_QC, qualifiersAndConditions, statusStartDate));
        serviceInfo.setTlCountryCode("EU");
        serviceInfo.setTspName(serviceName);
        return Collections.singletonList(serviceInfo);
    }

    private List<ServiceInfo> getCAServiceInfoWithQcConditions(CertificateToken certToken, String serviceName, Date statusStartDate) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setTspName(serviceName);
        serviceInfo.setTlCountryCode("EU");
        Map<String, List<Condition>> qualifiersAndConditions = new HashMap<>();
        qualifiersAndConditions.put(QC_WITH_QSCD, Collections.singletonList(createDigitalSignatureOrNonRepudiationListCondition()));
        qualifiersAndConditions.put(QC_STATEMENT, Collections.singletonList(createNonRepudiationCriteriaListCondition()));
        qualifiersAndConditions.put(QC_FOR_ESIG, Collections.singletonList(createNonRepudiationCriteriaListCondition()));
        serviceInfo.setStatus(getServiceInfoStatuses(serviceName, certToken, CA_QC, qualifiersAndConditions, statusStartDate));

        return Collections.singletonList(serviceInfo);
    }

    private CompositeCondition createNonRepudiationCriteriaListCondition() {
        CompositeCondition condition = new CompositeCondition(MatchingCriteriaIndicator.all);
        condition.addChild(new KeyUsageCondition(KeyUsageBit.nonRepudiation, true));
        return condition;
    }

    private CompositeCondition createDigitalSignatureOrNonRepudiationListCondition() {
        CompositeCondition condition = new CompositeCondition(MatchingCriteriaIndicator.atLeastOne);
        condition.addChild(new KeyUsageCondition(KeyUsageBit.digitalSignature, true));
        condition.addChild(new KeyUsageCondition(KeyUsageBit.nonRepudiation, true));
        return condition;
    }


    private List<ServiceInfo> getOCSPServiceInfo(CertificateToken certToken, String serviceName, Date statusStartDate) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setTlCountryCode("EU");
        Map<String, List<Condition>> qualifiersAndConditions = new HashMap<>();
        serviceInfo.setStatus(getServiceInfoStatuses(serviceName, certToken, OCSP_QC, qualifiersAndConditions, statusStartDate));

        return Collections.singletonList(serviceInfo);
    }

    private TimeDependentValues<ServiceInfoStatus> getServiceInfoStatuses(String serviceName, CertificateToken certToken, String type, Map<String, List<Condition>> qualifiersAndConditions, Date statusStartDate) {
        return new TimeDependentValues(Collections.singletonList(createServiceStatus(serviceName, certToken, type, qualifiersAndConditions, statusStartDate)));
    }

    private Date getServiceInfoStartDate(String statusStartDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            return sdf.parse(statusStartDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private ServiceInfoStatus createServiceStatus(String serviceName, CertificateToken certToken, String type, Map<String, List<Condition>> qualifiersAndConditions, Date statusStartDate) {

        return new ServiceInfoStatus(serviceName, type, getStatus(statusStartDate), qualifiersAndConditions, Arrays.asList("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures"), null, certToken.getCertificate().getNotBefore(), statusStartDate, null);
    }

    private String getStatus(Date startDate) {
        if (EIDASUtils.isPostEIDAS(startDate)) {
            return "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted";
        } else {
            return "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision";
        }
    }

}
