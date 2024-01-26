/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.signature.ocsp;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSRevocationUtils;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.ocsp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

public class SkOcspSource implements OCSPSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkOcspSource.class);

    private static final int NONCE_LENGTH = 20;
    /**
     * The data loader used to retrieve the OCSP response.
     */
    private SkOcspDataLoader dataLoader;

    private String url;

    public SkOcspSource(String url) {
        dataLoader = new SkOcspDataLoader();
        this.url = url;
        LOGGER.debug("Initialized SK Online OCSP source");
    }

    private byte[] buildOCSPRequest(final CertificateToken signCert, final CertificateToken issuerCert, Extension nonceExtension) throws
            DSSException {
        try {
            LOGGER.debug("Building OCSP request");
            final CertificateID certId = DSSRevocationUtils.getOCSPCertificateID(signCert, issuerCert, DigestAlgorithm.SHA1);
            final OCSPReqBuilder ocspReqBuilder = new OCSPReqBuilder();
            ocspReqBuilder.addRequest(certId);
            ocspReqBuilder.setRequestExtensions(new Extensions(nonceExtension));

            return ocspReqBuilder.build().getEncoded();
        } catch (Exception e) {
            throw new DSSException(e);
        }
    }

    @Override
    public OCSPToken getRevocationToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        LOGGER.debug("Getting OCSP token");
        if (dataLoader == null) {
            throw new IllegalStateException("Data loader is null");
        }
        try {
            final String dssIdAsString = certificateToken.getDSSIdAsString();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("--> OnlineOCSPSource queried for {}", dssIdAsString);
            }

            final String ocspUri = url;
            LOGGER.debug("Getting OCSP token from URI: {}", ocspUri);
            if (ocspUri == null) {
                return null;
            }
            Extension nonceExtension = createNonce();
            final byte[] content = buildOCSPRequest(certificateToken, issuerCertificateToken, nonceExtension);

            final byte[] ocspRespBytes = dataLoader.post(ocspUri, content);

            final OCSPResp ocspResp = new OCSPResp(ocspRespBytes);
            BasicOCSPResp basicOCSPResp = (BasicOCSPResp) ocspResp.getResponseObject();
            if (basicOCSPResp == null) {
                LOGGER.error("OCSP response is empty");
                return null;
            }

            checkNonce(basicOCSPResp, nonceExtension);

            Date bestUpdate = null;
            SingleResp bestSingleResp = null;
            final CertificateID certId = DSSRevocationUtils.getOCSPCertificateID(certificateToken, issuerCertificateToken, DigestAlgorithm.SHA1);
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
                return constructOCSPToken(basicOCSPResp, certificateToken, issuerCertificateToken);
            }
        } catch (OCSPException e) {
            LOGGER.error("OCSP error: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DSSException(e);
        }
        return null;
    }

    private OCSPToken constructOCSPToken(BasicOCSPResp ocspResponse, CertificateToken subjectCert, CertificateToken issuerCert) {
        SingleResp latestSingleResponse = DSSRevocationUtils.getLatestSingleResponse(ocspResponse, subjectCert, issuerCert);
        OCSPToken token = new OCSPToken(ocspResponse, latestSingleResponse, subjectCert, issuerCert);
        token.setSourceURL(url);
        return token;
    }

    private void checkNonce(BasicOCSPResp basicOCSPResp, Extension expectedNonceExtension) {
        final Extension extension = basicOCSPResp.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        final DEROctetString expectedNonce = (DEROctetString) expectedNonceExtension.getExtnValue();
        final DEROctetString receivedNonce = (DEROctetString) extension.getExtnValue();
        if (!receivedNonce.equals(expectedNonce)) {
            throw new InvalidOcspNonceException("The OCSP request was the victim of replay attack: nonce[sent:" + expectedNonce + "," +
                    " received:" + receivedNonce);
        }
    }

    private Extension createNonce() {
        byte[] bytes = generateRandomNonce();
        DEROctetString nonce = new DEROctetString(bytes);
        return new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, nonce);
    }

    private byte[] generateRandomNonce() {
        SecureRandom random = new SecureRandom();
        byte[] nonceBytes = new byte[NONCE_LENGTH];
        random.nextBytes(nonceBytes);
        return nonceBytes;
    }

    void setDataLoader(SkOcspDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public static class InvalidOcspNonceException extends RuntimeException {
        public InvalidOcspNonceException(String message) {
            super(message);
        }
    }

}

