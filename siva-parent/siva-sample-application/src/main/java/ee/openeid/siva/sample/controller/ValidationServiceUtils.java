package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.siva.FileType;
import ee.openeid.siva.sample.siva.ValidationRequest;

import java.util.Arrays;

public final class ValidationServiceUtils {
    private static final String FILENAME_EXTENSION_SEPARATOR = ".";

    private ValidationServiceUtils() {
    }

    public static FileType getValidationServiceType(ValidationRequest validationRequest) {
        final String filename = validationRequest.getFilename();
        return parseFileExtension(filename.substring(filename.lastIndexOf(FILENAME_EXTENSION_SEPARATOR) + 1));
    }

    public static FileType parseFileExtension(final String fileExtension) {
        FileType foundMatch = Arrays.stream(FileType.values())
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);

        if (isAsiceFileExtension(foundMatch)) {
            foundMatch = FileType.BDOC;
        }

        return foundMatch;
    }

    private static boolean isAsiceFileExtension(final FileType foundMatch) {
        return foundMatch != null && foundMatch == FileType.ASICE;
    }
}
