/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.signature.ocsp;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.spi.exception.DSSExternalResourceException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.BufferedHttpEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

public class SkOcspDataLoader extends OCSPDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkOcspDataLoader.class);

    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public byte[] post(final String url, final byte[] content) throws DSSException {
        LOGGER.info("Getting OCSP response from {}", url);

        HttpPost httpRequest = null;
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient client = null;
        try {
            final URI uri = URI.create(url.trim());
            httpRequest = new HttpPost(uri);

            // The length for the InputStreamEntity is needed, because some receivers (on the other side) need this information.
            // To determine the length, we cannot read the content-stream up to the end and re-use it afterwards.
            // This is because, it may not be possible to reset the stream (= go to position 0).
            // So, the solution is to cache temporarily the complete content data (as we do not expect much here) in a byte-array.
            try (
                    final ByteArrayInputStream bis = new ByteArrayInputStream(content);
                    final HttpEntity httpEntity = new InputStreamEntity(bis, content.length, null);
                    final HttpEntity requestEntity = new BufferedHttpEntity(httpEntity);
                    ) {
                httpRequest.setEntity(requestEntity);
                if (contentType != null) {
                    httpRequest.setHeader(CONTENT_TYPE, contentType);
                }

                client = getHttpClient(url);
                httpResponse = getHttpResponse(client, httpRequest);
            }

            return readHttpResponse(httpResponse);
        } catch (IOException e) {
            throw new DSSExternalResourceException(String.format("Unable to process POST call for url [%s]. Reason : [%s]", url, e.getMessage()) , e);
        } finally {
            closeQuietly(httpRequest, httpResponse, client);
        }
    }
}

