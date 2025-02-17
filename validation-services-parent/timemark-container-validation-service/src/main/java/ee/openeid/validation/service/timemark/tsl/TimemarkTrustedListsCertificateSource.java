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

package ee.openeid.validation.service.timemark.tsl;

import ee.openeid.tsl.annotation.LoadableTsl;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import org.digidoc4j.TSLCertificateSource;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;

import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME;
import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_TSL_NAME;

@LoadableTsl(name = TM_TSL_NAME)
@Component(TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME)
public class TimemarkTrustedListsCertificateSource extends TrustedListsCertificateSource implements TSLCertificateSource {

    @Override
    public void addTSLCertificate(X509Certificate certificate) {
        throw new UnsupportedOperationException("Adding TSL certificate not supported");
    }

    @Override
    public void invalidateCache() {
        throw new UnsupportedOperationException("Invalidating TSL cache not supported");
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Refreshing TSL not supported");
    }

}
