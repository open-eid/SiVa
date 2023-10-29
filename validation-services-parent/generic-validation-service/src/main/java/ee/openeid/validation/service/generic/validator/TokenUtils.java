/*
 * Copyright 2021 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.diagnostic.RevocationWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.TokenProxy;
import eu.europa.esig.dss.enumerations.CertificateStatus;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class TokenUtils {

    public static boolean isTokenSignatureIntactAndSignatureValidAndTrustedChain(TokenProxy token) {
        return token.isSignatureIntact() && token.isSignatureValid() && (token.isTrustedChain() || Optional
                .ofNullable(token.getSigningCertificate()).map(TokenProxy::isTrustedChain).orElse(false)
        );
    }

    public static boolean isRevocationTokenForCertificateAndCertificateStatusGood(RevocationWrapper revocationToken) {
        if (revocationToken instanceof CertificateRevocationWrapper) {
            CertificateRevocationWrapper certificateRevocation = (CertificateRevocationWrapper) revocationToken;
            return (certificateRevocation.getStatus() == CertificateStatus.GOOD);
        } else {
            return false;
        }
    }

    public static boolean isTimestampTokenMessageImprintDataFoundAndMessageImprintDataIntact(TimestampWrapper timestampToken) {
        return timestampToken.isMessageImprintDataFound() && timestampToken.isMessageImprintDataIntact();
    }

    public static boolean isTimestampTokenValid(TimestampWrapper timestampToken) {
        return isTimestampTokenMessageImprintDataFoundAndMessageImprintDataIntact(timestampToken)
                && isTokenSignatureIntactAndSignatureValidAndTrustedChain(timestampToken);
    }

}
