package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.siva.FileType;
import ee.openeid.siva.sample.siva.ValidationRequest;
import ee.openeid.siva.sample.test.utils.TestFileUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        ValidationRequest request = createXroadValidationRequest(
                Base64.encodeBase64String("random text".getBytes()),
                "random.asice"
        );

        assertThat(ValidationServiceUtils.getValidationServiceType(request)).isEqualTo(FileType.BDOC);
    }

    @Test
    public void givenXroadContainerValidationRequestWillReturnTrue() throws Exception {
        ValidationRequest request = createXroadValidationRequest(
                loadBase64EncodedFile("/xroad-container.asice"),
                "xroad.asice"
        );

        assertThat(ValidationServiceUtils.isXroadAsiceContainer(request)).isTrue();
    }

    @Test
    public void givenXroadContainerWillReturnFileTypeXROAD() throws Exception {
        ValidationRequest request = createXroadValidationRequest(
                loadBase64EncodedFile("/xroad-container.asice"),
                "xroad.asice"
        );

        assertThat(ValidationServiceUtils.getValidationServiceType(request)).isEqualTo(FileType.XROAD);
    }

    @Test
    public void givenInvalidXroadContainerWillReturnFalse() throws Exception {
        ValidationRequest request = createXroadValidationRequest(null, "xroad.asice");
        assertThat(ValidationServiceUtils.isXroadAsiceContainer(request)).isFalse();
    }

    private ValidationRequest createXroadValidationRequest(String document, String filename) throws IOException {
        ValidationRequest request = new ValidationRequest();
        request.setDocument(document);
        request.setFilename(filename);
        return request;
    }

    private String loadBase64EncodedFile(String filePath) throws IOException {
        File testFile = TestFileUtils.loadTestFile(filePath);
        return Base64.encodeBase64String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
    }
}