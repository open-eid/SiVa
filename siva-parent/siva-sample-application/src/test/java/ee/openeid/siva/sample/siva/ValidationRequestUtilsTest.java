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

package ee.openeid.siva.sample.siva;

import static org.assertj.core.api.Assertions.assertThat;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.test.utils.TestFileUtils;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ValidationRequestUtilsTest {

    @Test
    public void testConstructorIsPrivate() throws Exception {
        final Constructor<ValidationRequestUtils> constructor = ValidationRequestUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void givenInvalidFileExtensionWillReturnNull() throws Exception {
        assertThat(ValidationRequestUtils.parseFileExtension("random")).isNull();
    }

    @Test
    public void givenBdocFileExtensionWillReturnFileTypeBDOC() throws Exception {
        assertThat(ValidationRequestUtils.parseFileExtension("bdoc")).isEqualTo(FileType.BDOC);
    }
}
