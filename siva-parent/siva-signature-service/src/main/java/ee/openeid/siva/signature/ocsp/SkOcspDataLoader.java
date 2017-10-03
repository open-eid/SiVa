package ee.openeid.siva.signature.ocsp;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.client.http.commons.OCSPDataLoader;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

public class SkOcspDataLoader extends OCSPDataLoader {

    private static final Logger log = LoggerFactory.getLogger(SkOcspDataLoader.class);

    @Override
    public byte[] post(final String url, final byte[] content) throws DSSException {
        log.info("Getting OCSP response from " + url);

        HttpPost httpRequest = null;
        HttpResponse httpResponse = null;
        CloseableHttpClient client = null;
        try {
            final URI uri = URI.create(url.trim());
            httpRequest = new HttpPost(uri);

            // The length for the InputStreamEntity is needed, because some receivers (on the other side) need this information.
            // To determine the length, we cannot read the content-stream up to the end and re-use it afterwards.
            // This is because, it may not be possible to reset the stream (= go to position 0).
            // So, the solution is to cache temporarily the complete content data (as we do not expect much here) in a byte-array.
            final ByteArrayInputStream bis = new ByteArrayInputStream(content);

            final HttpEntity httpEntity = new InputStreamEntity(bis, content.length);
            final HttpEntity requestEntity = new BufferedHttpEntity(httpEntity);
            httpRequest.setEntity(requestEntity);
            if (contentType != null) {
                httpRequest.setHeader(CONTENT_TYPE, contentType);
            }

            client = getHttpClient(url);
            httpResponse = getHttpResponse(client, httpRequest, url);

            final byte[] returnedBytes = readHttpResponse(url, httpResponse);
            return returnedBytes;
        } catch (IOException e) {
            throw new DSSException(e);
        } finally {
            try {
                if (httpRequest != null) {
                    httpRequest.releaseConnection();
                }
                if (httpResponse != null) {
                    EntityUtils.consumeQuietly(httpResponse.getEntity());
                }
            } finally {
                IOUtils.closeQuietly(client);
            }
        }
    }
}

