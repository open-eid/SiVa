package ee.openeid.siva.proxy.document;

import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;

import java.util.Optional;

import static java.util.Arrays.stream;

public enum ReportType {

    SIMPLE("Simple"),
    DETAILED("Detailed");

    private String value;

    ReportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static ReportType reportTypeFromString(String type) {
        Optional<ReportType> reportType = stream(ReportType.class.getEnumConstants())
                .filter(dt -> dt.name().equalsIgnoreCase(type))
                .findAny();

        if (!reportType.isPresent()) {
            throw new UnsupportedTypeException("type = " + type + " is unsupported");
        }
        return reportType.get();
    }
}
