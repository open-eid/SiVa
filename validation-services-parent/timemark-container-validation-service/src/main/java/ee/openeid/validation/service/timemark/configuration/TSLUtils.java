/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.timemark.configuration;

import eu.europa.esig.dss.tsl.ServiceInfo;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.digidoc4j.TSLCertificateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TSLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSLUtils.class);

    public static TSLCertificateSource addCertificatesFromTrustedListSource(TSLCertificateSource tsl, TrustedListsCertificateSource trustedListSource) {
        trustedListSource.getCertificates().forEach(certToken -> {
            ServiceInfo serviceInfo = null;

            if (!trustedListSource.getTrustServices(certToken).isEmpty()) {
                serviceInfo = (ServiceInfo) trustedListSource.getTrustServices(certToken).toArray()[0];
            }
            tsl.addCertificate(certToken, Collections.singletonList(serviceInfo));
        });
        LOGGER.debug("{} certificates added to TSL certificate source", trustedListSource.getCertificates().size());
        return tsl;
    }
}
