package ee.openeid.siva.validation.document.report.builder;

public class ReportBuilderUtils {

    public static String emptyWhenNull(String value) {
        return value != null ? value : "";
    }
}
