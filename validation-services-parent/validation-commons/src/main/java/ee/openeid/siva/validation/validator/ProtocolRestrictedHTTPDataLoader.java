/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.validator;

import eu.europa.esig.dss.spi.client.http.NativeHTTPDataLoader;
import eu.europa.esig.dss.spi.client.http.Protocol;
import eu.europa.esig.dss.spi.exception.DSSExternalResourceException;

import java.util.List;
import java.util.concurrent.Callable;

public class ProtocolRestrictedHTTPDataLoader extends NativeHTTPDataLoader {
    private final List<Protocol> acceptedProtocols;

    public ProtocolRestrictedHTTPDataLoader(List<Protocol> acceptedProtocols) {
        this.acceptedProtocols = List.copyOf(acceptedProtocols);
    }

    @Override
    protected Callable<byte[]> createNativeDataLoaderCall(
            String url,
            HttpMethod httpMethod,
            byte[] content,
            boolean refresh
    ) {
        if (acceptedProtocols.stream().noneMatch(protocol -> protocol.isTheSame(url))) {
            throw new DSSExternalResourceException("The requested url protocol is not supported: " + url);
        }
        return super.createNativeDataLoaderCall(url, httpMethod, content, refresh);
    }
}
