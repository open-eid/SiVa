/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
        return value != null ? value : valueNotPresent();
    }

    public static SimpleDateFormat getDateFormatterWithGMTZone() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(GREENWICH_MEAN_TIME));
        return sdf;
    }

    public static String valueNotPresent() {
        return StringUtils.EMPTY;
    }

    public static Policy createReportPolicy(ValidationPolicy validationPolicy) {
        Policy reportPolicy = new Policy();
        reportPolicy.setPolicyName(validationPolicy.getName());
        reportPolicy.setPolicyDescription(validationPolicy.getDescription());
        reportPolicy.setPolicyUrl(validationPolicy.getUrl());
        return reportPolicy;
    }

}
