package ee.openeid.siva.signature.ocsp;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSRevocationUtils;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.ocsp.OCSPRespStatus;
import eu.europa.esig.dss.x509.ocsp.OCSPSource;
import eu.europa.esig.dss.x509.ocsp.OCSPToken;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

public class SkOcspSource implements OCSPSource {

    private static final Logger log = LoggerFactory.getLogger(SkOcspSource.class);

    /**
     * The data loader used to retrieve the OCSP response.
     */
    private SkOcspDataLoader dataLoader;

    private String url;

    public SkOcspSource(String url) {
        dataLoader = new SkOcspDataLoader();
        this.url = url;
        log.debug("Initialized SK Online OCSP source");
    }

    private byte[] buildOCSPRequest(final CertificateToken signCert, final CertificateToken issuerCert, Extension nonceExtension) throws
            DSSException {
        try {
            log.debug("Building OCSP request");
            final CertificateID certId = DSSRevocationUtils.getOCSPCertificateID(signCert, issuerCert);
            final OCSPReqBuilder ocspReqBuilder = new OCSPReqBuilder();
            ocspReqBuilder.addRequest(certId);
            ocspReqBuilder.setRequestExtensions(new Extensions(nonceExtension));

            return ocspReqBuilder.build().getEncoded();
        } catch (Exception e) {
            throw new DSSException(e);
        }
    }

    @Override
    public OCSPToken getOCSPToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        log.debug("Getting OCSP token");
        if (dataLoader == null) {
            throw new RuntimeException("Data loader is null");
        }
        try {
            final String dssIdAsString = certificateToken.getDSSIdAsString();
            if (log.isTraceEnabled()) {
                log.trace("--> OnlineOCSPSource queried for " + dssIdAsString);
            }

            final String ocspUri = url;
            log.debug("Getting OCSP token from URI: " + ocspUri);
            if (ocspUri == null) {
                return null;
            }
            Extension nonceExtension = createNonce();
            final byte[] content = buildOCSPRequest(certificateToken, issuerCertificateToken, nonceExtension);

            final byte[] ocspRespBytes = dataLoader.post(ocspUri, content);

            final OCSPResp ocspResp = new OCSPResp(ocspRespBytes);
            BasicOCSPResp basicOCSPResp = (BasicOCSPResp) ocspResp.getResponseObject();
            if(basicOCSPResp == null) {
                log.error("OCSP response is empty");
                return null;
            }

            checkNonce(basicOCSPResp, nonceExtension);

            Date bestUpdate = null;
            SingleResp bestSingleResp = null;
            final CertificateID certId = DSSRevocationUtils.getOCSPCertificateID(certificateToken, issuerCertificateToken);
            for (final SingleResp singleResp : basicOCSPResp.getResponses()) {

                if (DSSRevocationUtils.matches(certId, singleResp)) {

                    final Date thisUpdate = singleResp.getThisUpdate();
                    if (bestUpdate == null || thisUpdate.after(bestUpdate)) {

                        bestSingleResp = singleResp;
                        bestUpdate = thisUpdate;
                    }
                }
            }
            if (bestSingleResp != null) {

                final OCSPToken ocspToken = new OCSPToken();
                OCSPRespStatus status = OCSPRespStatus.fromInt(ocspResp.getStatus());
                ocspToken.setResponseStatus(status);
                ocspToken.setCertId(certId);
                ocspToken.setAvailable(true);
                ocspToken.setBasicOCSPResp(basicOCSPResp);
                return ocspToken;
            }
        } catch (OCSPException e) {
            log.error("OCSP error: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DSSException(e);
        }
        return null;
    }

    protected void checkNonce(BasicOCSPResp basicOCSPResp, Extension expectedNonceExtension) {
        final Extension extension = basicOCSPResp.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        final DEROctetString expectedNonce = (DEROctetString) expectedNonceExtension.getExtnValue();
        final DEROctetString receivedNonce = (DEROctetString) extension.getExtnValue();
        if (!receivedNonce.equals(expectedNonce)) {
            throw new InvalidOcspNonceException("The OCSP request was the victim of replay attack: nonce[sent:" + expectedNonce + "," +
                    " received:" + receivedNonce);
        }
    }

    Extension createNonce() {
        byte[] bytes = generateRandomNonce();
        DEROctetString nonce = new DEROctetString(bytes);
        boolean critical = false;
        return new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, critical, nonce);
    }

    private byte[] generateRandomNonce() {
        SecureRandom random = new SecureRandom();
        byte[] nonceBytes = new byte[20];
        random.nextBytes(nonceBytes);
        return nonceBytes;
    }

    void setDataLoader(SkOcspDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public static class InvalidOcspNonceException extends RuntimeException {
        public InvalidOcspNonceException(String message) { super(message); }
    }

}

