package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.siva.FileType;
import ee.openeid.siva.sample.siva.ValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class ValidationServiceUtils {
    private static final String FILENAME_EXTENSION_SEPARATOR = ".";
    private static final String XROAD_XSD = "http://x-road.eu/xsd/xroad.xsd";

    private ValidationServiceUtils() {
    }

    public static FileType getValidationServiceType(ValidationRequest validationRequest) throws IOException {
        final String filename = validationRequest.getFilename();
        FileType parsedFileType = parseFileExtension(filename.substring(filename.lastIndexOf(FILENAME_EXTENSION_SEPARATOR) + 1));

        if (isAsiceFileExtension(parsedFileType)) {
            parsedFileType = FileType.BDOC;
        }

        if (isXroadAsiceContainer(validationRequest)) {
            parsedFileType = FileType.XROAD;
        }

        return parsedFileType;
    }

    public static boolean isXroadAsiceContainer(ValidationRequest validationRequest) throws IOException {
        String document = validationRequest.getDocument();
        if (document == null) {
            return false;
        }

        try (InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(document.getBytes()))) {
            byte[] fileContents = ZipUtil.unpackEntry(stream, "message.xml");
            if (fileContents == null) {
                return false;
            }

            String messageFile = new String(fileContents);
            return messageFile.contains(XROAD_XSD);
        }
    }

    public static FileType parseFileExtension(String fileExtension) {
        return Arrays.stream(FileType.values())
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);
    }

    private static boolean isAsiceFileExtension(final FileType foundMatch) {
        return foundMatch != null && foundMatch == FileType.ASICE;
    }
}
