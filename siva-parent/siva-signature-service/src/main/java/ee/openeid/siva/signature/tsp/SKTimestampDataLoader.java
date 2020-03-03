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

package ee.openeid.siva.signature.tsp;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.client.http.NativeHTTPDataLoader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class SKTimestampDataLoader extends NativeHTTPDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SKTimestampDataLoader.class);

    @Override
    public byte[] post(String url, byte[] content) {
        LOGGER.info("Getting timestamp from " + url);
        try {
            URLConnection connection = new URL(url).openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty("Content-Type", "application/timestamp-query");
            connection.setRequestProperty("Content-Transfer-Encoding", "binary");

            try (OutputStream out = connection.getOutputStream()){
                IOUtils.write(content, out);
            }
            try (InputStream in = connection.getInputStream()){
                return IOUtils.toByteArray(in);
            }
        } catch (IOException e) {
            throw new DSSException("An error occured while HTTP POST for url '" + url + "' : " + e.getMessage(), e);
        }
    }
}
