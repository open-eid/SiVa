package ee.openeid.validation.service.generic.helper;

import eu.europa.esig.dss.crl.CRLBinary;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSRevocationUtils;
import eu.europa.esig.dss.spi.x509.revocation.crl.OfflineCRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPResponseBinary;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OfflineOCSPSource;
import eu.europa.esig.dss.validation.CommitmentType;
import eu.europa.esig.dss.validation.*;
import eu.europa.esig.dss.validation.timestamp.SignatureTimestampSource;
import lombok.SneakyThrows;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

public class MockSignature extends DefaultAdvancedSignature {

    private OfflineCRLSource offlineCRLSource;
    private OfflineOCSPSource offlineOCSPSource;

    public MockSignature(OfflineCRLSource offlineCRLSource, OfflineOCSPSource offlineOCSPSource) {
        super(null);
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
    public SignatureCRLSource getCRLSource() {
        if (offlineCRLSource == null) {
            return null;
        }
        return new ListCRLSource(offlineCRLSource);
    }

    @SneakyThrows
    @Override
    public SignatureOCSPSource getOCSPSource() {
        if (offlineOCSPSource == null) {
            return null;
        }
        return new ListOCSPSource(offlineOCSPSource);
    }

    @Override
    protected SignatureIdentifier buildSignatureIdentifier() {
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
    public SignatureTimestampSource getTimestampSource() {
        return null;
    }

    @Override
    public CandidatesForSigningCertificate getCandidatesForSigningCertificate() {
        return null;
    }

    @Override
    public void checkSignatureIntegrity() {
    }

    @Override
    public void checkSigningCertificate() {
    }

    @Override
    public SignatureProductionPlace getSignatureProductionPlace() {
        return null;
    }

    @Override
    public CommitmentType getCommitmentTypeIndication() {
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
    public String getContentIdentifier() {
        return null;
    }

    @Override
    public String getContentHints() {
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
    public List<CertificateRef> getCertificateRefs() {
        return null;
    }

    @Override
    public String getDAIdentifier() {
        return null;
    }

    @Override
    public boolean isDataForSignatureLevelPresent(SignatureLevel signatureLevel) {
        return false;
    }

    @Override
    public SignatureLevel[] getSignatureLevels() {
        return new SignatureLevel[0];
    }

    @Override
    public void checkSignaturePolicy(SignaturePolicyProvider signaturePolicyDetector) {

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

    public static class MockOfflineCRLSource extends OfflineCRLSource {

        private static final long serialVersionUID = 9049063242913822305L;

        @SneakyThrows
        @Override
        public Collection<CRLBinary> getCRLBinaryList() {
            ClassLoader classLoader = MockSignature.class.getClassLoader();
            File file = new File(classLoader.getResource("test-files/crl.crl").getFile());
            CRLBinary crlBinary = new CRLBinary(Files.readAllBytes(file.toPath()));
            return Collections.singletonList(crlBinary);
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
            OCSPResponseBinary ocspResponseBinary = OCSPResponseBinary.build(basicOCSPResp);
            addResponse(ocspResponseBinary, RevocationOrigin.EXTERNAL);
        }


        @Override
        public void appendContainedOCSPResponses() {
            // TODO Auto-generated method stub
        }

        private void addResponse(OCSPResponseBinary responseBinary, RevocationOrigin revocationOrigin) {
            addOCSPResponse(responseBinary, revocationOrigin);
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
