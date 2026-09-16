package ee.openeid.siva.proxy.document;

import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ValidationLevel {

    BASIC_SIGNATURES("BasicSignatures"),
    TIMESTAMPS("Timestamps"),
    LONG_TERM_DATA("LongTermData"),
    ARCHIVAL_DATA("ArchivalData"),
    ;

    private final @NonNull String value;

    public static ValidationLevel validationLevelFromString(String level) {
        return Stream.of(ValidationLevel.values())
                .filter(l -> l.getValue().equalsIgnoreCase(level))
                .findFirst()
                .orElseThrow(() -> new UnsupportedTypeException("ValidationLevel '" + level + "' not supported"));
    }

}
