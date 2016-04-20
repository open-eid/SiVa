package ee.openeid.siva.proxy.document.typeresolver;

import ee.openeid.siva.proxy.document.ReportType;

public class ReportTypeResolver {

    public static ReportType reportTypeFromString(String reportTypeString) {
        for (ReportType reportType : ReportType.class.getEnumConstants()) {
            if (reportType.name().compareToIgnoreCase(reportTypeString) == 0) {
                return reportType;
            }
        }
        throw new UnsupportedTypeException("type = " + reportTypeString + " is unsupported");
    }

}
