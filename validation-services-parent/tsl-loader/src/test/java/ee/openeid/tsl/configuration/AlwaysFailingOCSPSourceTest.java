package ee.openeid.tsl.configuration;

import eu.europa.esig.dss.x509.ocsp.OCSPSource;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class AlwaysFailingOCSPSourceTest {

    @Test
    public void alwaysFailingOcspSourceShouldReturnNullInsteadOfOcspToken() throws Exception {
        OCSPSource ocspSource = new AlwaysFailingOCSPSource();
        assertNull(ocspSource.getOCSPToken(null, null));
    }
}
