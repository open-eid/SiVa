/*
 * Copyright 2020 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.helper;

import eu.europa.esig.dss.crl.CRLBinary;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.MaskGenerationFunction;
import eu.europa.esig.dss.enumerations.RevocationOrigin;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.SignaturePolicyStore;
import eu.europa.esig.dss.model.identifier.EncapsulatedRevocationTokenIdentifier;
import eu.europa.esig.dss.model.scope.SignatureScope;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.revocation.crl.CRL;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.spi.DSSRevocationUtils;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.revocation.OfflineRevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.crl.OfflineCRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPResponseBinary;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OfflineOCSPSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.BaselineRequirementsChecker;
import eu.europa.esig.dss.validation.CommitmentTypeIndication;
import eu.europa.esig.dss.validation.DefaultAdvancedSignature;
import eu.europa.esig.dss.model.ReferenceValidation;
import eu.europa.esig.dss.spi.SignatureCertificateSource;
import eu.europa.esig.dss.validation.SignatureDigestReference;
import eu.europa.esig.dss.validation.SignatureIdentifierBuilder;
import eu.europa.esig.dss.validation.SignaturePolicy;
import eu.europa.esig.dss.validation.SignatureProductionPlace;
import eu.europa.esig.dss.validation.SignerRole;
import eu.europa.esig.dss.validation.timestamp.TimestampSource;
import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;
import lombok.SneakyThrows;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MockSignature extends DefaultAdvancedSignature {

    private final OfflineCRLSource offlineCRLSource;
    private final OfflineOCSPSource offlineOCSPSource;

    public MockSignature(OfflineCRLSource offlineCRLSource, OfflineOCSPSource offlineOCSPSource) {
        super();
        this.offlineCRLSource = offlineCRLSource;
        this.offlineOCSPSource = offlineOCSPSource;
    }

    @Override
    public String getId() {
        return "S-12345";
    }

    @Override
    public List<CertificateToken> getCertificates() {
        return Collections.singletonList(new MockCertificateToken());
    }

    @Override
    protected List<SignatureScope> findSignatureScopes() {
        return null;
    }

    @Override
    public OfflineRevocationSource<CRL> getCRLSource() {
        return offlineCRLSource;
    }

    @Override
    public OfflineRevocationSource<OCSP> getOCSPSource() {
        return offlineOCSPSource;
    }

    @Override
    public TimestampSource getTimestampSource() {
        return null;
    }

    @Override
    protected SignatureIdentifierBuilder getSignatureIdentifierBuilder() {
        return null;
    }

    @Override
    public SignatureForm getSignatureForm() {
        return null;
    }

    @Override
    public SignatureAlgorithm getSignatureAlgorithm() {
        return null;
    }

    @Override
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return null;
    }

    @Override
    public DigestAlgorithm getDigestAlgorithm() {
        return null;
    }

    @Override
    public MaskGenerationFunction getMaskGenerationFunction() {
        return null;
    }

    @Override
    public Date getSigningTime() {
        return null;
    }

    @Override
    public SignatureCertificateSource getCertificateSource() {
        return null;
    }

    @Override
    public void checkSignatureIntegrity() {
    }

    @Override
    public SignatureProductionPlace getSignatureProductionPlace() {
        return null;
    }

    @Override
    public List<CommitmentTypeIndication> getCommitmentTypeIndications() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public List<SignerRole> getSignedAssertions() {
        return null;
    }

    @Override
    public List<SignerRole> getClaimedSignerRoles() {
        return null;
    }

    @Override
    public List<SignerRole> getCertifiedSignerRoles() {
        return null;
    }

    @Override
    public List<AdvancedSignature> getCounterSignatures() {
        return null;
    }

    @Override
    public String getDAIdentifier() {
        return null;
    }

    @Override
    public SignatureLevel getDataFoundUpToLevel() {
        return null;
    }

    @Override
    public byte[] getSignatureValue() {
        return new byte[0];
    }

    @Override
    public List<ReferenceValidation> getReferenceValidations() {
        return null;
    }

    @Override
    public SignatureDigestReference getSignatureDigestReference(DigestAlgorithm digestAlgorithm) {
        return null;
    }

    public void addExternalTimestamp(TimestampToken timestamp) {}

    @Override
    public SignaturePolicy getSignaturePolicy() {
        return null;
    }

    @Override
    public SignaturePolicyStore getSignaturePolicyStore() {
        return null;
    }

    @Override
    public Digest getDataToBeSignedRepresentation() {
        return null;
    }

    @Override
    protected SignaturePolicy buildSignaturePolicy() {
        return null;
    }

    @Override
    protected BaselineRequirementsChecker<MockSignature> createBaselineRequirementsChecker() {
        return null;
    }

    private static class MockCRLBinary extends CRLBinary {

        protected MockCRLBinary(byte[] derEncoded) {
            super(derEncoded);
        }

    }

    public static class MockOfflineCRLSource extends OfflineCRLSource {

        private static final long serialVersionUID = 9049063242913822305L;

        @SneakyThrows
        @Override
        public Set<EncapsulatedRevocationTokenIdentifier<CRL>> getAllRevocationBinaries() {
            ClassLoader classLoader = MockSignature.class.getClassLoader();
            File file = new File(classLoader.getResource("test-files/crl.crl").getFile());
            MockCRLBinary crlBinary = new MockCRLBinary(Files.readAllBytes(file.toPath()));
            return Collections.singleton(crlBinary);
        }

        public Set<RevocationOrigin> getRevocationOrigins(CRLBinary crlBinary) {
            return Collections.singleton(RevocationOrigin.EXTERNAL);
        }
    }

    public static class MockOfflineOCSPSource extends OfflineOCSPSource {

        private static final long serialVersionUID = -6500951542081739907L;

        public MockOfflineOCSPSource() throws IOException {
            BasicOCSPResp basicOCSPResp = DSSRevocationUtils.loadOCSPBase64Encoded(
                    "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");

            CertificateToken userUniversign = DSSUtils.loadCertificateFromBase64EncodedString(
                    "MIIEjjCCA3agAwIBAgIQfCokxd5pp8RnZZOUHNbq0zANBgkqhkiG9w0BAQsFADBuMQswCQYDVQQGEwJGUjEgMB4GA1UEChMXQ3J5cHRvbG9nIEludGVybmF0aW9uYWwxHDAaBgNVBAsTEzAwMDIgNDM5MTI5MTY0MDAwMjYxHzAdBgNVBAMTFlVuaXZlcnNpZ24gQ0EgaGFyZHdhcmUwHhcNMTIwNzA2MTkzNzI0WhcNMTcwNzA2MTkzNzI0WjBrMQswCQYDVQQGEwJGUjERMA8GA1UEBxMITkFOVEVSUkUxFzAVBgNVBAoTDkFYQSBGUkFOQ0UgVklFMRcwFQYDVQQLEw4wMDAyIDMxMDQ5OTk1OTEXMBUGA1UEAxMOQVhBIEZSQU5DRSBWSUUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDPbheD3Lk34Rv+SBwS3duDqzKNEHoNen48pzVbt6wFQf9bqpE9YPZh+jb+bnRUXLbv77vaWBfCZGOfrybDv/mqpmTkdYk6F+D7ddwQuF7OVmE+VqeOgoBc+Vo1LDzjowDPOmyH938DB1ZUzI8r2mw60rR9laqLI7kO6OT/9fodJMz1eE2G6LEzXvOj+V0PhDg3FX+vF+pLFUmI53x3L+zM8AZofUCY0oaUpY1hgWR4on5K1Oy0me63UHkslEjFS5jYff4EE+z/eZY7+e3FMqwzFm5al60w1djzsHUVod+CkIASs71VgAYSYOQyckwmKaFs6325JHAu5DH5a62WVqLnAgMBAAGjggEpMIIBJTA7BggrBgEFBQcBAQQvMC0wKwYIKwYBBQUHMAGGH2h0dHA6Ly9zY2FoLm9jc3AudW5pdmVyc2lnbi5ldS8wQgYDVR0gBDswOTA3BgsrBgEEAftLBQEDAjAoMCYGCCsGAQUFBwIBFhpodHRwOi8vZG9jcy51bml2ZXJzaWduLmV1LzBEBgNVHR8EPTA7MDmgN6A1hjNodHRwOi8vY3JsLnVuaXZlcnNpZ24uZXUvdW5pdmVyc2lnbl9jYV9oYXJkd2FyZS5jcmwwDAYDVR0TAQH/BAIwADAOBgNVHQ8BAf8EBAMCBkAwHQYDVR0OBBYEFK13mc0NTbq7YVWmGWisLz2JJu57MB8GA1UdIwQYMBaAFGDkMN3uetTQfl0l2f07eyFkT927MA0GCSqGSIb3DQEBCwUAA4IBAQA7DWIeC3qTgW+OOsoYnzuwZxdu+F9eiqiVhq2UXx1vxjJQ6hthfq1Thzj5050fn5GQ/HeSNl05+hfoDpAK0JVWLssq1rrvBAx2lfgNWTG+LF581DVtH1I3NLi+A9YslvCPt51NVGAERhye6BZyugDlQCVhy6dRFhqSDSbi+S7RRqpIl/QDR/pBOwnBePO6qSpwSrDsCJT+N9nFBHmXpRsyFJyPEFZMIAVoluuJq4mCEGLVtqiC4DzvAwCsFBKlnwQ7pSFHO9ztXMYHpYhD/0wDSegwcvAVm7p8/N0PQDaAZQlWXs7McCBHeQPjxVD2xAkk7s9joicJ6kKttfxfEc6w");
            CertificateToken caUniversign = DSSUtils.loadCertificateFromBase64EncodedString(
                    "MIIEYTCCA0mgAwIBAgIQIVWN4tmvyrr2CIjMBjGC1zANBgkqhkiG9w0BAQsFADB2MQswCQYDVQQGEwJGUjEgMB4GA1UEChMXQ3J5cHRvbG9nIEludGVybmF0aW9uYWwxHDAaBgNVBAsTEzAwMDIgNDM5MTI5MTY0MDAwMjYxJzAlBgNVBAMTHlVuaXZlcnNpZ24gUHJpbWFyeSBDQSBoYXJkd2FyZTAeFw0xMjA2MTUxMjU2MjVaFw0yMjA2MTUxMjU2MjVaMG4xCzAJBgNVBAYTAkZSMSAwHgYDVQQKExdDcnlwdG9sb2cgSW50ZXJuYXRpb25hbDEcMBoGA1UECxMTMDAwMiA0MzkxMjkxNjQwMDAyNjEfMB0GA1UEAxMWVW5pdmVyc2lnbiBDQSBoYXJkd2FyZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKHfv76grtwK9PRFTBq4BDtmmLaaHDAqkj1wd0lHIN2QH1jM6hKWeS4U/wy8QuYtvw0aFxYxiMWzS/vrj0Sczv5hAt8reE1Eg1uQcx6+aSUBqJ6a+1TbM7PtkHg1ozgbmVXGuiOLyviLhhUo8XmeeGEhux+cyRNiYCs37VPN5OVrKks29dspMkvllkexfrxiPfc+gB58EU+iNEcip/YrZLu4ZqErlCePIVBLyX9skb7QwKXDW8XBIgAzpoBj0U9/4Vxaiyj209xT1Uuz2vKsuT8Hq7I1vt7miYviHg/ovsWXH6yGcNomxX55l0qWQ4z+mGlVhCLDPMKmspY5D+1kSqsCAwEAAaOB8jCB7zA7BgNVHSAENDAyMDAGBFUdIAAwKDAmBggrBgEFBQcCARYaaHR0cDovL2RvY3MudW5pdmVyc2lnbi5ldS8wEgYDVR0TAQH/BAgwBgEB/wIBADBMBgNVHR8ERTBDMEGgP6A9hjtodHRwOi8vY3JsLnVuaXZlcnNpZ24uZXUvdW5pdmVyc2lnbl9wcmltYXJ5X2NhX2hhcmR3YXJlLmNybDAOBgNVHQ8BAf8EBAMCAQYwHQYDVR0OBBYEFGDkMN3uetTQfl0l2f07eyFkT927MB8GA1UdIwQYMBaAFE3Z/Kgtx8hapK1fSa5opNyeihIiMA0GCSqGSIb3DQEBCwUAA4IBAQAQrQJxwn+DBwN+KTt75IuOkaPOnZ+FfmF/1V7zDt3YNz7n1hRXlflbx9wBJn30TwyuTuZ/Cq1gEiA+TJMrsdZPKvagY8a/q7oCm6jYw6cBhopErwV/sZ9R3Ic+fphKSxoEnygpZs4uKMU2bB7nbul+sdJkP/OrIHKfHydMk3ayeAxnnieOj8EU+Z5w3fpekOGOtb4VUTESWU/xQfDZcNaauNRU2DYFi1eDypfVnyD8tORDoFVaAqzdIJuMJ06jJB5fnmNBXU7GOQFLcdK7hy3ZDmPNh5FNGnaQRrlY2st7lXfV3mu9AgHmjPjxrbAwgo1GzLRY0byI9bfitN0sLT+d");

            SingleResp latestSingleResponse = DSSRevocationUtils.getLatestSingleResponse(basicOCSPResp, userUniversign, caUniversign);

            OCSPToken ocspToken = new OCSPToken(basicOCSPResp, latestSingleResponse, userUniversign, caUniversign);
            addBinary(OCSPResponseBinary.build(basicOCSPResp), RevocationOrigin.EXTERNAL);
            addResponse(ocspToken, RevocationOrigin.EXTERNAL);
        }

        private void addResponse(OCSPToken ocspToken, RevocationOrigin revocationOrigin) {
            addRevocation(ocspToken, revocationOrigin);
        }

    }

    private static class MockCertificateToken extends CertificateToken {

        public MockCertificateToken() {
            super(openX509Certificate(Paths.get("src/test/resources/TESTofEECertificationCentreRootCA.crt")));
        }

        protected static X509Certificate openX509Certificate(Path path) {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                try (FileInputStream stream = new FileInputStream(path.toFile())) {
                    return (X509Certificate) certificateFactory.generateCertificate(stream);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getDSSIdAsString() {
            return "C-12345";
        }
    }

}
