/*
 * Copyright 2016 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.tsl;

import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import lombok.NonNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The trust source of one trusted list as its loader last published it; readers resolve it per use.
 */
public class TrustedListsCertificateSourceHolder {

    // A refresh fills a fresh source and publishes it here in one step, so a reader that resolves the source per
    // use (every validation builds its certificate verifier per request) sees either the previous complete source
    // or the new one, never one being refilled. Empty until the first refresh, as the source itself used to be.
    private final AtomicReference<TrustedListsCertificateSource> current =
            new AtomicReference<>(new TrustedListsCertificateSource());

    public TrustedListsCertificateSource current() {
        return current.get();
    }

    public void publish(@NonNull TrustedListsCertificateSource source) {
        current.set(source);
    }

}
