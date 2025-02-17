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

package ee.openeid.validation.service.generic.validator;

import ee.openeid.validation.service.generic.validator.filter.CountryFilter;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class TLevelSignatureOfNonListedCountryPredicate implements Predicate<SignatureWrapper> {

    @NonNull
    private final CountryFilter countryFilter;
    @NonNull
    private final List<String> tLevelSignatures;

    @Override
    public boolean test(SignatureWrapper signatureWrapper) {
        String countryName = Optional
                .ofNullable(signatureWrapper.getSigningCertificate())
                .map(CertificateWrapper::getCountryName)
                .orElse(null);

        return countryFilter.filter(countryName) && Optional
                .ofNullable(signatureWrapper.getSignatureFormat())
                .map(SignatureLevel::toString)
                .map(tLevelSignatures::contains)
                .orElse(false);
    }
}
