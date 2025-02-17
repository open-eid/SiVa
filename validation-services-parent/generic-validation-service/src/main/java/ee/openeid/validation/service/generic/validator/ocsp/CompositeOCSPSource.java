/*
 * Copyright 2023 - 2025 Riigi Infosüsteemi Amet
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
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.BiPredicate;

@RequiredArgsConstructor
public class CompositeOCSPSource implements OCSPSource {
    @NonNull
    private final OCSPSource ocspSource;
    @NonNull
    private final BiPredicate<CertificateToken, CertificateToken> ocspRequestRequirementPredicate;

    @Override
    public OCSPToken getRevocationToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        if (ocspRequestRequirementPredicate.test(certificateToken, issuerCertificateToken)) {
            return ocspSource.getRevocationToken(certificateToken, issuerCertificateToken);
        } else {
            return null;
        }
    }
}
