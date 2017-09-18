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

package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.cache.UploadedFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ValidationRequestUtils {
    private static final String FILENAME_EXTENSION_SEPARATOR = ".";
    private static final String XROAD_XSD = "http://x-road.eu/xsd/xroad.xsd";
    private static final String UNIQUE_XROAD_ASICE_FILE = "message.xml";

    static FileType getValidationServiceType(UploadedFile validationRequest) throws IOException {
        if (isXroadAsiceContainer(validationRequest)) {
            return FileType.XROAD;
        }
        return null;
    }

    static boolean isXroadAsiceContainer(UploadedFile validationRequest) throws IOException {
        String document = validationRequest.getEncodedFile();
        if (document == null) {
            return false;
        }

        try (InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(document.getBytes()))) {
            byte[] fileContents = ZipUtil.unpackEntry(stream, UNIQUE_XROAD_ASICE_FILE);
            if (fileContents == null) {
                return false;
            }

            String messageFile = new String(fileContents);
            return messageFile.contains(XROAD_XSD);
        }
    }

    static FileType parseFileExtension(String fileExtension) {
        return Arrays.stream(FileType.values())
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);
    }

    private static boolean isAsiceFileExtension(final FileType foundMatch) {
        return foundMatch != null && foundMatch == FileType.ASICE;
    }
}
