package ee.openeid.siva.signature.tsp;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.client.http.NativeHTTPDataLoader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class SKTimestampDataLoader extends NativeHTTPDataLoader {

    private static final Logger log = LoggerFactory.getLogger(SKTimestampDataLoader.class);

    @Override
    public byte[] post(String url, byte[] content) {
        log.info("Getting timestamp from " + url);
        OutputStream out = null;
        InputStream inputStream = null;
        byte[] result;
        try {
            URLConnection connection = new URL(url).openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty("Content-Type", "application/timestamp-query");
            connection.setRequestProperty("Content-Transfer-Encoding", "binary");

            out = connection.getOutputStream();
            IOUtils.write(content, out);
            inputStream = connection.getInputStream();
            result = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new DSSException("An error occured while HTTP POST for url '" + url + "' : " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(inputStream);
        }
        return result;
    }
}
