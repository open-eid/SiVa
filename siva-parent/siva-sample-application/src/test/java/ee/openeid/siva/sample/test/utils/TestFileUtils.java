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

package ee.openeid.siva.sample.test.utils;

import ee.openeid.siva.sample.cache.UploadedFile;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TestFileUtils {
    private static final long TIMESTAMP = System.currentTimeMillis() / 1000L;

    private TestFileUtils() {
    }

    public static UploadedFile generateUploadFile(TemporaryFolder testingFolder, String filename, String fileContents) throws IOException {
        final File inputFile = testingFolder.newFile(filename);
        FileUtils.writeStringToFile(inputFile, fileContents, StandardCharsets.UTF_8);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFilename(inputFile.getName());
        uploadedFile.setTimestamp(TIMESTAMP);
        uploadedFile.setEncodedFile(Base64.encodeBase64String(FileUtils.readFileToByteArray(inputFile)));

        return uploadedFile;
    }

    public static File loadTestFile(String filePath) {
        final String infoFilePath = java.lang.invoke.MethodHandles.lookup().lookupClass().getResource(filePath).getFile();
        return new File(infoFilePath);
    }
}
