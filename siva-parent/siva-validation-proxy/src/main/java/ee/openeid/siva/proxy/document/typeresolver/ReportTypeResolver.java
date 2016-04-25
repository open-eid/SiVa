package ee.openeid.siva.proxy.document.typeresolver;

import ee.openeid.siva.proxy.document.ReportType;

import java.util.Optional;

import static java.util.Arrays.stream;

public class ReportTypeResolver {

    public static ReportType reportTypeFromString(String reportTypeString) {
        Optional<ReportType> reportType = stream(ReportType.class.getEnumConstants())
                .filter(rt -> rt.name().equalsIgnoreCase(reportTypeString)).findAny();

        if (!reportType.isPresent()) {
            throw new UnsupportedTypeException("type = " + reportTypeString + " is unsupported");
        }
        return reportType.get();
    }

}
