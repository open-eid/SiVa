package ee.openeid.siva.sample.siva;

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

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void givenAsiceValidationRequestWillReturnFileTypeBDOC() throws Exception {
        UploadedFile request = createXroadValidationRequest(
                Base64.encodeBase64String("random text".getBytes()),
                "random.asice"
        );

        assertThat(ValidationRequestUtils.getValidationServiceType(request)).isEqualTo(FileType.BDOC);
    }

    @Test
    public void givenXroadContainerValidationRequestWillReturnTrue() throws Exception {
        UploadedFile request = createXroadValidationRequest(
                loadBase64EncodedFile("/xroad-container.asice"),
                "xroad.asice"
        );

        assertThat(ValidationRequestUtils.isXroadAsiceContainer(request)).isTrue();
    }

    @Test
    public void givenXroadContainerWillReturnFileTypeXROAD() throws Exception {
        UploadedFile request = createXroadValidationRequest(
                loadBase64EncodedFile("/xroad-container.asice"),
                "xroad.asice"
        );

        assertThat(ValidationRequestUtils.getValidationServiceType(request)).isEqualTo(FileType.XROAD);
    }

    @Test
    public void givenInvalidXroadContainerWillReturnFalse() throws Exception {
        UploadedFile request = createXroadValidationRequest(null, "xroad.asice");
        assertThat(ValidationRequestUtils.isXroadAsiceContainer(request)).isFalse();
    }

    private static UploadedFile createXroadValidationRequest(String document, String filename) throws IOException {
        UploadedFile request = new UploadedFile();
        request.setEncodedFile(document);
        request.setFilename(filename);

        return request;
    }

    private static String loadBase64EncodedFile(String filePath) throws IOException {
        File testFile = TestFileUtils.loadTestFile(filePath);
        return Base64.encodeBase64String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
    }
}