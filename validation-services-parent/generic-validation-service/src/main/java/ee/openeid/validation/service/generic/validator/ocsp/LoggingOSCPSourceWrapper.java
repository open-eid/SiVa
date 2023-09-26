/*
 * Copyright 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.ocsp;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.spi.x509.revocation.OnlineRevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.RevocationToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoggingOSCPSourceWrapper implements OCSPSource {
    @NonNull
    private final OnlineRevocationSource<OCSP> ocspOnlineRevocationSource;

    @Override
    public OCSPToken getRevocationToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {

        OnlineRevocationSource.RevocationTokenAndUrl<OCSP> ocspTokenAndUrl;

        try {
            ocspTokenAndUrl = ocspOnlineRevocationSource.getRevocationTokenAndUrl(certificateToken, issuerCertificateToken);
        } catch (Exception e) {
            log.warn(
                    "Failed to perform OCSP request for certificate token '{}': {}",
                    certificateToken.getDSSIdAsString(),
                    e.getMessage()
            );
            return null;
        }

        if (ocspTokenAndUrl != null) {
            return processOcspToken(ocspTokenAndUrl, certificateToken);
        } else {
            return handleMissingOcspToken(certificateToken);
        }
    }

    private OCSPToken processOcspToken(OnlineRevocationSource.RevocationTokenAndUrl<OCSP> ocspTokenAndUrl, CertificateToken certificateToken) {
        RevocationToken<OCSP> revocationToken = ocspTokenAndUrl.getRevocationToken();

        log.info(
                "Performed OCSP request for certificate token '{}' from '{}', status: '{}'",
                certificateToken.getDSSIdAsString(),
                ocspTokenAndUrl.getUrlString(),
                revocationToken.getStatus()
        );

        return (OCSPToken) revocationToken;
    }

    private OCSPToken handleMissingOcspToken(CertificateToken certificateToken) {
        log.warn("No OCSP token found for certificate token '{}'", certificateToken.getDSSIdAsString());
        return null;
    }
}
