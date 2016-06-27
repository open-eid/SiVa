package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.siva.FileType;
import ee.openeid.siva.sample.siva.ValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationServiceUtilsTest {

    @Test
    public void testConstructorIsPrivate() throws Exception {
        final Constructor<ValidationServiceUtils> constructor = ValidationServiceUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void givenInvalidFileExtensionWillReturnNull() throws Exception {
        assertThat(ValidationServiceUtils.parseFileExtension("random")).isNull();
    }

    @Test
    public void givenBdocFileExtensionWillReturnFileTypeBDOC() throws Exception {
        assertThat(ValidationServiceUtils.parseFileExtension("bdoc")).isEqualTo(FileType.BDOC);
    }

    @Test
    public void givenAsiceValidationRequestWillReturnFileTypeBDOC() throws Exception {
        ValidationRequest request = new ValidationRequest();
        request.setDocument(Base64.encodeBase64String("random text".getBytes()));
        request.setFilename("random.asice");

        assertThat(ValidationServiceUtils.getValidationServiceType(request)).isEqualTo(FileType.BDOC);
    }

}