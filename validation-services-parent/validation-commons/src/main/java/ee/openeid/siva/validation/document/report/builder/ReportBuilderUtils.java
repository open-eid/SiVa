package ee.openeid.siva.validation.document.report.builder;

import org.apache.commons.lang.StringUtils;

public final class ReportBuilderUtils {

    private ReportBuilderUtils() {
    }

    public static String emptyWhenNull(String value) {
        return value != null ? value : StringUtils.EMPTY;
    }
}
