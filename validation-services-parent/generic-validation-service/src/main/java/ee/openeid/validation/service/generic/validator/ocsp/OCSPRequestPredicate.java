/*
 * Copyright 2023 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.validation.service.generic.validator.filter.CountryFilter;
import eu.europa.esig.dss.model.x509.CertificateToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;

import java.security.cert.X509Certificate;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@RequiredArgsConstructor
public class OCSPRequestPredicate implements BiPredicate<CertificateToken, CertificateToken> {

    @NonNull
    private final BiFunction<X509Certificate, ASN1ObjectIdentifier, String> subjectDnAttributeExtractor;
    @NonNull
    private CountryFilter countryFilter;

    @Override
    public boolean test(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        String country = subjectDnAttributeExtractor.apply(certificateToken.getCertificate(), BCStyle.C);
        return countryFilter.filter(country);
    }
}
