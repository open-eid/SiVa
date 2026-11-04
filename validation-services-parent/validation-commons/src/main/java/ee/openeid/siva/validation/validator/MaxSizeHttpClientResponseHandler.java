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

import eu.europa.esig.dss.service.http.commons.CommonsHttpClientResponseHandler;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.MaxSizeInputStream;
import org.apache.hc.core5.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;

public class MaxSizeHttpClientResponseHandler extends CommonsHttpClientResponseHandler {
    private final int maxSize;

    public MaxSizeHttpClientResponseHandler(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected byte[] getContent(HttpEntity responseEntity) throws IOException {
        try (InputStream content = new MaxSizeInputStream(responseEntity.getContent(), maxSize, null)) {
            return DSSUtils.toByteArray(content);
        }
    }
}
