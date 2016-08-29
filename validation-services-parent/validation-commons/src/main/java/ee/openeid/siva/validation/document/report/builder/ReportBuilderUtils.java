package ee.openeid.siva.validation.document.report.builder;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class ReportBuilderUtils {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String GREENWICH_MEAN_TIME = "Etc/GMT";

    private ReportBuilderUtils() {
    }

    public static String emptyWhenNull(String value) {
        return value != null ? value : StringUtils.EMPTY;
    }

    public static SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(GREENWICH_MEAN_TIME));
        return sdf;
    }

}
