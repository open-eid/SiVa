/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CertUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertUtil.class);

    public static String getCountryCode(X509Certificate cert) {
        try {
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
            RDN c = x500name.getRDNs(BCStyle.C)[0];
            return IETFUtils.valueToString(c.getFirst().getValue());
        } catch (CertificateEncodingException e) {
            LOGGER.error("Error extracting country from certificate", e.getMessage(), e);
            return null;
        }
    }

}
