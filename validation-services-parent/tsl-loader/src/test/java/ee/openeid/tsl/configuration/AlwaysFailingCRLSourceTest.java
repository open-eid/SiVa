package ee.openeid.tsl.configuration;

import eu.europa.esig.dss.x509.crl.CRLSource;
import org.junit.Test;

import static org.junit.Assert.assertNull;


public class AlwaysFailingCRLSourceTest {

    @Test
    public void alwaysFailingCRLSourceShouldNotFindCRL() {
        CRLSource crlSource = new AlwaysFailingCRLSource();
        assertNull(crlSource.findCrl(null));
    }
}
