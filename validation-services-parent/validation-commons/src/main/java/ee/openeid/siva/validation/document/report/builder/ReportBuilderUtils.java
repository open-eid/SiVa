package ee.openeid.siva.validation.document.report.builder;

import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReportBuilderUtils {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String GREENWICH_MEAN_TIME = "Etc/GMT";

    public static String emptyWhenNull(String value) {
        return value != null ? value : StringUtils.EMPTY;
    }

    public static SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(GREENWICH_MEAN_TIME));
        return sdf;
    }

    public static Policy createReportPolicy(ValidationPolicy validationPolicy) {
        Policy reportPolicy = new Policy();
        reportPolicy.setPolicyName(validationPolicy.getName());
        reportPolicy.setPolicyDescription(validationPolicy.getDescription());
        reportPolicy.setPolicyUrl(validationPolicy.getUrl());
        return reportPolicy;
    }

}
