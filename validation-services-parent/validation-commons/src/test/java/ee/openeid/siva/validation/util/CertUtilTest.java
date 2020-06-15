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

package ee.openeid.siva.validation.util;

import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertUtilTest {
    private static final String CERTIFICATE = "MIIEnDCCA4SgAwIBAgIQL9ObKmnMcfRSaL7iJK7tXDANBgkqhkiG9w0BAQUF\n" +
            "ADBkMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1p\n" +
            "c2tlc2t1czEXMBUGA1UEAwwORVNURUlELVNLIDIwMTExGDAWBgkqhkiG9w0B\n" +
            "CQEWCXBraUBzay5lZTAeFw0xMzEwMjQwNjMyMDJaFw0xODEwMjMyMDU5NTla\n" +
            "MIGSMQswCQYDVQQGEwJFRTEPMA0GA1UECgwGRVNURUlEMRowGAYDVQQLDBFk\n" +
            "aWdpdGFsIHNpZ25hdHVyZTEgMB4GA1UEAwwXVk9MTCxBTkRSRVMsMzkwMDQx\n" +
            "NzAzNDYxDTALBgNVBAQMBFZPTEwxDzANBgNVBCoMBkFORFJFUzEUMBIGA1UE\n" +
            "BRMLMzkwMDQxNzAzNDYwggEjMA0GCSqGSIb3DQEBAQUAA4IBEAAwggELAoIB\n" +
            "AQDLfISVybY0GHV/RxTCqSkZEyuOBoo3iPa7164l+cxXGSUNqn8u5yfR2GOM\n" +
            "PAF69hAfsI8ljQdLBDmIlQbV7BzQFFW8hXgD3xSUih+djyzthYvGwhofVlpA\n" +
            "6V0VltolxUroC1DeW1RTCJruVTw5Np32vcC3sFdyc7g64TxBaRiZT2Wys/1B\n" +
            "xP1lcOSuCI9T0hbETYmM0eN5+IAUMYIRhU2869D8zZIupP0Ajw0d2sgxxP25\n" +
            "xeO14TInXCNXM0gMg02UiX5BRpcDpCZ9gzl5VQd9Bhzr3PoXRqp9E8ED6wA3\n" +
            "XI3YOmqTAVOtpcOIKcT6IKj1PI6frGRNAttKMkoF82xLAgRcUXhVo4IBGDCC\n" +
            "ARQwCQYDVR0TBAIwADAOBgNVHQ8BAf8EBAMCBkAwUQYDVR0gBEowSDBGBgsr\n" +
            "BgEEAc4fAQEDAzA3MBIGCCsGAQUFBwICMAYaBG5vbmUwIQYIKwYBBQUHAgEW\n" +
            "FWh0dHA6Ly93d3cuc2suZWUvY3BzLzAdBgNVHQ4EFgQUxierbfXZNbg8WIzJ\n" +
            "ie85PJgI2zgwIgYIKwYBBQUHAQMEFjAUMAgGBgQAjkYBATAIBgYEAI5GAQQw\n" +
            "HwYDVR0jBBgwFoAUe2ryVVBcuNl6CIdBrvqiKz1bV3YwQAYDVR0fBDkwNzA1\n" +
            "oDOgMYYvaHR0cDovL3d3dy5zay5lZS9yZXBvc2l0b3J5L2NybHMvZXN0ZWlk\n" +
            "MjAxMS5jcmwwDQYJKoZIhvcNAQEFBQADggEBAGg8cHTqByzFeQyNmGv0jt22\n" +
            "OzNTwiY0htEzpHh840hfd3sP57nVxlk+btUJtmkbtpQYJ8Lh22bnNC6JSein\n" +
            "fONTjIKH0WbqtmmlRHlIzwEzT2rG0zTy8wDMyfpQUTqkbfiKq/aBAr1JvKm2\n" +
            "/7yMARSAp1Oe4//8dF8NZIzY3+BEZiJtuVTofR62ohfAMwYLXYMYqipeZ1xF\n" +
            "x61vydvbr1SDKkBItIkrNepftbaduigJne6UxzDXUsq6oUIeNh6dRD/VjY7C\n" +
            "Qmxfn+zGIQh7x6KvLP5/i9aKlrsPBB25GaL6ZbZcMoQ2K5AnOzo5Kx7sF9Bu\n" +
            "IrzvrBIen9lWIwFB2xU=";

    @Test
    public void validCountryFromCertificate() throws CertificateException {
        Assert.assertEquals("EE", CertUtil.getCountryCode(getCertificate()));
    }

    @Test
    public void validCommonNameFromCertificate() throws CertificateException {
        Assert.assertEquals("VOLL,ANDRES,39004170346", CertUtil.getCommonName(getCertificate()));
    }

    private X509Certificate getCertificate() throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(Base64.decode(CERTIFICATE)));

    }
}
