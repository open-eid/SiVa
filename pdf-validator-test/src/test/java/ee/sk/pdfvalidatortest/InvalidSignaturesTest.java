package ee.sk.pdfvalidatortest;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class InvalidSignaturesTest extends PdfValidatorSoapTests {
	
    @Test
    public void missingSignedAttributeForSigningCertificate() {
        String httpBody = post(validationRequestFor(readFile("missing_signing_certificate_attribute.pdf"))).
                andReturn().body().asString();
        //System.out.println(httpBody.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&#xD;", "").replaceAll("&quot;", "\""));
        
        assertEquals(
                "The signed attribute: 'signing-certificate' is absent!",
                findErrorById("BBB_ICS_ISASCP_ANS", detailedReport(httpBody)));
    }

    @Test
    public void adesLtaBaselineProfileShouldPass() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-7.pdf"))).
                andReturn().body().asString();
        
        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void adesTBaselineProfileShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-5.pdf"))).
                andReturn().body().asString();

        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void noOcspRequestsAreMadeForBaselineB() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-AT-1.pdf"))).
                andReturn().body().asString();

        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void adesBBaselineRevokedShouldFail() {
        String httpBody = post(validationRequestFor(readFile("Signature-P-EE_AS-1.pdf"))).
                andReturn().body().asString();
        assertEquals(0, validSignatures(simpleReport(httpBody)));

    }

    @Test
    public void adesLtBaselineShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-pades-lt-sha256-sign.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));

    }

    @Test
    public void adesLtBaselineNonRepudiationIsMandatoryShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-pades-lt-sha256-auth.pdf"))).
                andReturn().body().asString();
        
        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void adesLtaBaselineNoOcspCrlInSignatureShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lta-no-ocsp.pdf"))).
                andReturn().body().asString();
        
        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void adesPdfSignedWithSha512CertifikateShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha512.pdf"))).
                andReturn().body().asString();
        
        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void adesPdfSignedWithSha1CertifikateShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha1.pdf"))).
                andReturn().body().asString();
        
        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void adesPdfSignedWithRevokedCertificateShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-revoked.pdf"))).
                andReturn().body().asString();
        
        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }
    
    @Test
    public void adesLtBaselineOcspOver15MinDelayShouldPassButWarn() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-15min1s.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "The validation failed, because OCSP is too long after the best-signature-time!",
                findWarningById("ADEST_IOTNLABST_ANS", detailedReport(httpBody)));
        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }
	
	@Test
    public void adesLtBaselineOcspOver24hDelayShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-28h.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "The validation failed, because OCSP is too long after the best-signature-time!",
                findErrorById("ADEST_IOTNLABST_ANS", detailedReport(httpBody)));
        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }
	
	@Ignore
	@Test
    public void adesLtBaselineOcspBeforeBestSignatureTimeShouldFail() {
        String httpBody = post(validationRequestFor(readFile("some_pdf_file"))).
                andReturn().body().asString();

        assertEquals(
        		"The validation failed, because OCSP is before the best-signature-time!",
                findErrorById("ADEST_IOABST_ANS", detailedReport(httpBody)));
        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void certificateContentsAreIncludedInResponse() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-15min1s.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhkiG9w0BAQUFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMB4XDTExMDMxODEwMTQ1OVoXDTI0MDMxODEwMTQ1OVowZDELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxFzAVBgNVBAMMDkVTVEVJRC1TSyAyMDExMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCz6XxsZh6r/aXcNe3kSpNMOqmQoAXUpzzcr4ZSaGZh/7JHIiplvNi6tbW/lK7sAiRsb65KzMWROEauld66ggbDPga6kU97C+AXGu7+DROXstjUOv6VlrHZVAnLmIOkycpWaxjM+EfQPZuDxEbkw96B3/fG69Zbp3s9y6WEhwU5Y9IiQl8YTkGnNUxidQbON1BGQm+HVEsgTf22J6r6G3FsE07rnMNskNC3DjuLSCUKF4kH0rVGVK9BdiCdFaZjHEykjwjIGzqnyxyRKe4YbJ6B9ABm95eSFgMBHtZEYU+q0VUIQGhAGAurOTXjWi1TssA42mnLGQZEI5GXMXtabp51AgMBAAGjggGgMIIBnDASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1UdDwEB/wQEAwIBBjCB9gYDVR0gBIHuMIHrMIHoBgsrBgEEAc4fZAEBATCB2DCBsgYIKwYBBQUHAgIwgaUegaIASwBhAHMAdQB0AGEAdABhAGsAcwBlACAAaQBzAGkAawB1AHQAdAD1AGUAbgBkAGEAdgBhAGwAZQAgAGQAbwBrAHUAbQBlAG4AZABpAGwAZQAgAGsAYQBuAHQAYQB2AGEAdABlACAAcwBlAHIAdABpAGYAaQBrAGEAYQB0AGkAZABlACAAdgDkAGwAagBhAHMAdABhAG0AaQBzAGUAawBzAC4wIQYIKwYBBQUHAgEWFWh0dHBzOi8vd3d3LnNrLmVlL0NQUzAdBgNVHQ4EFgQUe2ryVVBcuNl6CIdBrvqiKz1bV3YwHwYDVR0jBBgwFoAUEvJaPupWHL/NBqzx8SXJqUvUFJkwPQYDVR0fBDYwNDAyoDCgLoYsaHR0cDovL3d3dy5zay5lZS9yZXBvc2l0b3J5L2NybHMvZWVjY3JjYS5jcmwwDQYJKoZIhvcNAQEFBQADggEBAKC4IN3FC2gVDIH05TNMgFrQOCGSnXhzoJclRLoQ81BCOXTZI4qn7N74FHEnrAy6uNG7SS5qANqSaPIL8dp63jg/L4qn4iWaB5q5GGJOV07SnTHS7gUrqChGClnUeHxiZbL13PkP37Lnc+TKl1SKfgtn5FbH5cqrhvbA/VF3Yzlimu+L7EVohW9HKxZ//z8kDn6ieiPFfZdTOov/0eXVLlxqklybUuS6LYRRDiqQupgBKQBTwNbC8x0UHX00HokW+dCVcQvsUbv4xLhRq/MvyTthE+RdbkrV0JuzbfZvADfj75nA3+ZAzFYS5ZpMOjZ9p4rQVKpzQTklrF0m6mkdcEo=",
                certificateContentsById("41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862", diagnosticData(httpBody)));

        assertEquals(
                "MIIEnTCCA4WgAwIBAgIQURtcmP07BjlUmR1RPIeGCTANBgkqhkiG9w0BAQUFADBkMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEXMBUGA1UEAwwORVNURUlELVNLIDIwMTExGDAWBgkqhkiG9w0BCQEWCXBraUBzay5lZTAeFw0xNDEyMjMwNzQ0MTdaFw0xOTEyMjIyMTU5NTlaMIGWMQswCQYDVQQGEwJFRTEPMA0GA1UECgwGRVNURUlEMRowGAYDVQQLDBFkaWdpdGFsIHNpZ25hdHVyZTEiMCAGA1UEAwwZU0lOSVZFRSxWRUlLTywzNjcwNjAyMDIxMDEQMA4GA1UEBAwHU0lOSVZFRTEOMAwGA1UEKgwFVkVJS08xFDASBgNVBAUTCzM2NzA2MDIwMjEwMIIBITANBgkqhkiG9w0BAQEFAAOCAQ4AMIIBCQKCAQCXenu4kxXVFaVpxUfs7PGBC37WgP1oCpFOujGBiWFQiPgSWX8BtcxUIQaGpXMG31BtotSUVhalNDNszjU+ANRMOfeHKZploOV5R+Pm09B/XwRF1D+mK1lG3q+hz0aSt0DWXxFw4UAieTd5tVCDM/WhPFUD7ZinQayejNdRDo4Q7WS0wqp4YBNm3VCg1YPp/1Y86T28nxGKSewquVs089VOU92O0UUmNYy8AHu7Sod+DCNO5eVz6uSpJBRJRyvMbMxxIDfwtQI5YuttKN26IYXtjgOZeNKTV9eW0neO+T5P351odNSKulWzaAYKaI+E/9lfnY6fhygXgd7tmBqBIrOhAgMBAAGjggEXMIIBEzAJBgNVHRMEAjAAMA4GA1UdDwEB/wQEAwIGQDBQBgNVHSAESTBHMEUGCisGAQQBzh8BAQQwNzASBggrBgEFBQcCAjAGGgRub25lMCEGCCsGAQUFBwIBFhVodHRwOi8vd3d3LnNrLmVlL2Nwcy8wHQYDVR0OBBYEFCcVGdX3X/moNIYusvO958F/gbj0MCIGCCsGAQUFBwEDBBYwFDAIBgYEAI5GAQEwCAYGBACORgEEMB8GA1UdIwQYMBaAFHtq8lVQXLjZegiHQa76ois9W1d2MEAGA1UdHwQ5MDcwNaAzoDGGL2h0dHA6Ly93d3cuc2suZWUvcmVwb3NpdG9yeS9jcmxzL2VzdGVpZDIwMTEuY3JsMA0GCSqGSIb3DQEBBQUAA4IBAQCJSoo6h+4Dgu2+0C2ehtqYYEvMBIyLldWP88uWKgxw6HujsF5HRRk/zWAU8jGDN/LNzDNYDz0jg2212mn+neVBgo+U8W1Urkw9zgTsSwqnP7CoGw0nG65gnybrT4K+eX1ykyVmj1RAzfShVgwuOrMCDmguq6jFRj9V1oOmiMDpmzQ7Qo22le7qkkKoQ+PTLRfi5vpN+CQOg6kleeXaVwtdlP0ETfJIrdDBKKBKi8bn5b60300V1dMmsQAxdwXsKcuKPtG1YKO5Rf+OIUdAuOayYboeShGTlXlAswoxcfGZajxF8MCe9B4y0Rse8X1Q9C+F2rgloa5W6+JXeGrY8sUL",
                certificateContentsById("8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842", diagnosticData(httpBody)));
    }

    @Ignore
    public void adesLtBaselineSha256EcdsaShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-ecdsa.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Ignore //Made with test certificate. Need Test tls.
    public void adesLtBaselineSha256Ec224ShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ec224.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Ignore //Made with test certificate. Need Test tls.
    public void adesLtBaselineSha256Ec256ShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ec256.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Ignore //Made with test certificate. Need Test tls.
    public void adesLtBaselineCertificateExpired5DaysAftrerSignShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa1024-5d.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Ignore //Made with test certificate. Need Test tls.
     public void adesLtBaselineSha256Rsa1024ShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-rsa1024.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }


    @Test
    public void adesLtaBaselineAndBBaselineSignatureShouldPass() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-b.pdf"))).
                andReturn().body().asString();

        assertEquals(1, validSignatures(simpleReport(httpBody)));
    }

    @Test
    public void adesLtBaselineSignedExpiredCertificateShouldFail() {
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-rsa1024-sha1-expired.pdf"))).
                andReturn().body().asString();

        assertEquals(0, validSignatures(simpleReport(httpBody)));
    }

    private byte[] readFile(String fileName) {
        return readFileFromTestResources("invalid_signature_documents/", fileName);
    }

    private String findErrorById(String errorId, Document detailedReport) {
        return XmlUtil.findElementByXPath(
                detailedReport,
                "//d:Error[@NameId='" + errorId + "']",
                Collections.singletonMap("d", "http://dss.markt.ec.europa.eu/validation/diagnostic")).getTextContent();
    }
    
    private String findWarningById(String errorId, Document detailedReport) {
        return XmlUtil.findElementByXPath(
                detailedReport,
                "//d:Warning[@NameId='" + errorId + "']",
                Collections.singletonMap("d", "http://dss.markt.ec.europa.eu/validation/diagnostic")).getTextContent();
    }

    private int validSignatures(Document simpleReport) {
        String stringResult = XmlUtil.findElementByXPath(
                simpleReport,
                "//d:SimpleReport/d:ValidSignaturesCount",
                Collections.singletonMap("d", "http://dss.markt.ec.europa.eu/validation/diagnostic")).getTextContent();

        return Integer.parseInt(stringResult);
    }

    private String certificateContentsById(String certificateId, Document diagnosticData) {
        return XmlUtil.findElementByXPath(
                diagnosticData,
                "//d:DiagnosticData/d:UsedCertificates/d:Certificate[@Id='" + certificateId + "']/d:X509Data",
                Collections.singletonMap("d", "http://dss.markt.ec.europa.eu/validation/diagnostic")).getTextContent();
    }

    private Document detailedReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlDetailedReport").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }

    private Document diagnosticData(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlDiagnosticData").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }

    private Document simpleReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlSimpleReport").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }

}