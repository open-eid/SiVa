package ee.openeid.siva.sample.test.utils;

import java.io.File;
import java.lang.invoke.MethodHandles;

public final class TestFileUtils {
    private TestFileUtils() {
    }

    public static File loadTestFile(String filePath) {
        final String infoFilePath = MethodHandles.lookup().lookupClass().getResource(filePath).getFile();
        return new File(infoFilePath);
    }
}
