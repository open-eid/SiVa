package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationPass extends SiVaRestTests{

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

     /***
     * TestCaseID: PDF-ValidationPass-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with certificate that is expired after signing (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that expired after signing should pass.
     *
     * File: hellopades-lt-sha256-rsa1024-not-expired.pdf
     ***/
    @Test @Ignore //TODO: new test file is needed; the current one has issues with QC / SSCD
    public void validSignaturesRemainValidAfterSigningCertificateExpires() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa1024-not-expired.pdf"));
    }

    /***
     * TestCaseID: PDF-ValidationPass-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with certificate that will expire in 7 days after signing (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that expired after signing should pass.
     *
     * File: hellopades-lt-sha256-rsa2048-7d.pdf
     ***/
    @Test @Ignore //TODO: new test file is needed; the current one has issues with QC / SSCD
    public void certificateExpired7DaysAfterDocumentSigningShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa2048-7d.pdf"));
    }

    /***
     * TestCaseID: PDF-ValidationPass-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Certificate contents are include in response (PAdES Baseline LT)
     *
     * Expected Result: The PDF-file validation should pass
     *
     * File: hellopades-lt-sha256-ocsp-15min1s.pdf
     ***/
    @Test @Ignore
    public void certificateContentsAreIncludedInResponse() {
        /* TODO: We currently don't know how the certificates will be included in the report - fix the test when we do
        String httpBody = post(validationRequestFor(readFile("hellopades-lt-sha256-ocsp-15min1s.pdf"))).
                andReturn().body().asString();

        assertEquals(
                "MIIFBTCCA+2gAwIBAgIQKVKTqv2MxtRNgzCjwmRRDTANBgkqhkiG9w0BAQUFADB1MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEoMCYGA1UEAwwfRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMB4XDTExMDMxODEwMTQ1OVoXDTI0MDMxODEwMTQ1OVowZDELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxFzAVBgNVBAMMDkVTVEVJRC1TSyAyMDExMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCz6XxsZh6r/aXcNe3kSpNMOqmQoAXUpzzcr4ZSaGZh/7JHIiplvNi6tbW/lK7sAiRsb65KzMWROEauld66ggbDPga6kU97C+AXGu7+DROXstjUOv6VlrHZVAnLmIOkycpWaxjM+EfQPZuDxEbkw96B3/fG69Zbp3s9y6WEhwU5Y9IiQl8YTkGnNUxidQbON1BGQm+HVEsgTf22J6r6G3FsE07rnMNskNC3DjuLSCUKF4kH0rVGVK9BdiCdFaZjHEykjwjIGzqnyxyRKe4YbJ6B9ABm95eSFgMBHtZEYU+q0VUIQGhAGAurOTXjWi1TssA42mnLGQZEI5GXMXtabp51AgMBAAGjggGgMIIBnDASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1UdDwEB/wQEAwIBBjCB9gYDVR0gBIHuMIHrMIHoBgsrBgEEAc4fZAEBATCB2DCBsgYIKwYBBQUHAgIwgaUegaIASwBhAHMAdQB0AGEAdABhAGsAcwBlACAAaQBzAGkAawB1AHQAdAD1AGUAbgBkAGEAdgBhAGwAZQAgAGQAbwBrAHUAbQBlAG4AZABpAGwAZQAgAGsAYQBuAHQAYQB2AGEAdABlACAAcwBlAHIAdABpAGYAaQBrAGEAYQB0AGkAZABlACAAdgDkAGwAagBhAHMAdABhAG0AaQBzAGUAawBzAC4wIQYIKwYBBQUHAgEWFWh0dHBzOi8vd3d3LnNrLmVlL0NQUzAdBgNVHQ4EFgQUe2ryVVBcuNl6CIdBrvqiKz1bV3YwHwYDVR0jBBgwFoAUEvJaPupWHL/NBqzx8SXJqUvUFJkwPQYDVR0fBDYwNDAyoDCgLoYsaHR0cDovL3d3dy5zay5lZS9yZXBvc2l0b3J5L2NybHMvZWVjY3JjYS5jcmwwDQYJKoZIhvcNAQEFBQADggEBAKC4IN3FC2gVDIH05TNMgFrQOCGSnXhzoJclRLoQ81BCOXTZI4qn7N74FHEnrAy6uNG7SS5qANqSaPIL8dp63jg/L4qn4iWaB5q5GGJOV07SnTHS7gUrqChGClnUeHxiZbL13PkP37Lnc+TKl1SKfgtn5FbH5cqrhvbA/VF3Yzlimu+L7EVohW9HKxZ//z8kDn6ieiPFfZdTOov/0eXVLlxqklybUuS6LYRRDiqQupgBKQBTwNbC8x0UHX00HokW+dCVcQvsUbv4xLhRq/MvyTthE+RdbkrV0JuzbfZvADfj75nA3+ZAzFYS5ZpMOjZ9p4rQVKpzQTklrF0m6mkdcEo=",
                certificateContentsById("41ec808e33cca8659eaea81670d6c7dc01446636e1f227561b6307b80ba63862", diagnosticData(httpBody)));

        assertEquals(
                "MIIEnTCCA4WgAwIBAgIQURtcmP07BjlUmR1RPIeGCTANBgkqhkiG9w0BAQUFADBkMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEXMBUGA1UEAwwORVNURUlELVNLIDIwMTExGDAWBgkqhkiG9w0BCQEWCXBraUBzay5lZTAeFw0xNDEyMjMwNzQ0MTdaFw0xOTEyMjIyMTU5NTlaMIGWMQswCQYDVQQGEwJFRTEPMA0GA1UECgwGRVNURUlEMRowGAYDVQQLDBFkaWdpdGFsIHNpZ25hdHVyZTEiMCAGA1UEAwwZU0lOSVZFRSxWRUlLTywzNjcwNjAyMDIxMDEQMA4GA1UEBAwHU0lOSVZFRTEOMAwGA1UEKgwFVkVJS08xFDASBgNVBAUTCzM2NzA2MDIwMjEwMIIBITANBgkqhkiG9w0BAQEFAAOCAQ4AMIIBCQKCAQCXenu4kxXVFaVpxUfs7PGBC37WgP1oCpFOujGBiWFQiPgSWX8BtcxUIQaGpXMG31BtotSUVhalNDNszjU+ANRMOfeHKZploOV5R+Pm09B/XwRF1D+mK1lG3q+hz0aSt0DWXxFw4UAieTd5tVCDM/WhPFUD7ZinQayejNdRDo4Q7WS0wqp4YBNm3VCg1YPp/1Y86T28nxGKSewquVs089VOU92O0UUmNYy8AHu7Sod+DCNO5eVz6uSpJBRJRyvMbMxxIDfwtQI5YuttKN26IYXtjgOZeNKTV9eW0neO+T5P351odNSKulWzaAYKaI+E/9lfnY6fhygXgd7tmBqBIrOhAgMBAAGjggEXMIIBEzAJBgNVHRMEAjAAMA4GA1UdDwEB/wQEAwIGQDBQBgNVHSAESTBHMEUGCisGAQQBzh8BAQQwNzASBggrBgEFBQcCAjAGGgRub25lMCEGCCsGAQUFBwIBFhVodHRwOi8vd3d3LnNrLmVlL2Nwcy8wHQYDVR0OBBYEFCcVGdX3X/moNIYusvO958F/gbj0MCIGCCsGAQUFBwEDBBYwFDAIBgYEAI5GAQEwCAYGBACORgEEMB8GA1UdIwQYMBaAFHtq8lVQXLjZegiHQa76ois9W1d2MEAGA1UdHwQ5MDcwNaAzoDGGL2h0dHA6Ly93d3cuc2suZWUvcmVwb3NpdG9yeS9jcmxzL2VzdGVpZDIwMTEuY3JsMA0GCSqGSIb3DQEBBQUAA4IBAQCJSoo6h+4Dgu2+0C2ehtqYYEvMBIyLldWP88uWKgxw6HujsF5HRRk/zWAU8jGDN/LNzDNYDz0jg2212mn+neVBgo+U8W1Urkw9zgTsSwqnP7CoGw0nG65gnybrT4K+eX1ykyVmj1RAzfShVgwuOrMCDmguq6jFRj9V1oOmiMDpmzQ7Qo22le7qkkKoQ+PTLRfi5vpN+CQOg6kleeXaVwtdlP0ETfJIrdDBKKBKi8bn5b60300V1dMmsQAxdwXsKcuKPtG1YKO5Rf+OIUdAuOayYboeShGTlXlAswoxcfGZajxF8MCe9B4y0Rse8X1Q9C+F2rgloa5W6+JXeGrY8sUL",
                certificateContentsById("8835667315fdcf9681222d6b4aeaa69cd1ab5693ff3aa1a59a4c4288e4ac7842", diagnosticData(httpBody)));
        */
    }

    /***
     * TestCaseID: PDF-ValidationPass-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: Pdf with single valid signature
     *
     * Expected Result: Document should pass.
     *
     * File: PdfValidSingleSignature.pdf
     ***/
    @Test
    public void validSignature() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreValid(postForReport("PdfValidSingleSignature.pdf"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
