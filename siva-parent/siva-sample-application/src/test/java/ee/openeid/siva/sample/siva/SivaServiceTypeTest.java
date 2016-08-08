package ee.openeid.siva.sample.siva;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

public class SivaServiceTypeTest {

    @Test
    public void testConstructorIsPrivate() throws Exception {
        final Constructor<SivaServiceType> constructor = SivaServiceType.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void givenJSONServiceNameReturnsTrue() throws Exception {
        assertThat(SivaServiceType.JSON_SERVICE).isEqualTo("sivaJSON");
    }

    @Test
    public void givenSOAPServiceNameReturnsTrue() throws Exception {
        assertThat(SivaServiceType.SOAP_SERVICE).isEqualTo("sivaSOAP");
    }
}