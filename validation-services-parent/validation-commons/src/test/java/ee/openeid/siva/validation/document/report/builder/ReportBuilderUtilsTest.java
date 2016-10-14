/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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