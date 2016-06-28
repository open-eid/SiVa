package ee.openeid.siva.validation.document.report.builder;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportBuilderUtilsTest {

    @Test
    public void utilClassConstructorMustBePrivate() throws Exception {
        final Constructor<ReportBuilderUtils> constructor = ReportBuilderUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void givenNullValueWillReturnEmptyString() throws Exception {
        assertThat(ReportBuilderUtils.emptyWhenNull(null)).isEqualTo("");
    }

    @Test
    public void givenNotEmptyStringWillReturnItUnchanged() throws Exception {
        assertThat(ReportBuilderUtils.emptyWhenNull("random")).isEqualTo("random");
    }
}