package ee.openeid.siva.sample.test.utils;

import ee.openeid.siva.sample.cache.UploadedFile;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

public final class TestFileUtils {
    private static final long TIMESTAMP = System.currentTimeMillis() / 1000L;

    private TestFileUtils() {
    }

    public static UploadedFile generateUploadFile(TemporaryFolder testingFolder, String fileName, String fileContents) throws IOException {
        final File inputFile = testingFolder.newFile(fileName);
        FileUtils.writeStringToFile(inputFile, fileContents);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFilename(inputFile.getName());
        uploadedFile.setTimestamp(TIMESTAMP);
        uploadedFile.setEncodedFile(Base64.encodeBase64String(FileUtils.readFileToByteArray(inputFile)));

        return uploadedFile;
    }

    public static File loadTestFile(String filePath) {
        final String infoFilePath = MethodHandles.lookup().lookupClass().getResource(filePath).getFile();
        return new File(infoFilePath);
    }
}
