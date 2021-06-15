/*
 * Copyright 2016 - 2021 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.monitoring.indicator;

import ee.openeid.siva.monitoring.util.ManifestReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.MANIFEST_PARAM_APP_BUILD_TIME;
import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.MANIFEST_PARAM_APP_NAME;
import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.MANIFEST_PARAM_APP_VERSION;
import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.NOT_AVAILABLE;

@Slf4j
public class ApplicationHealthIndicator implements HealthIndicator {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    public static final String RESPONSE_PARAM_WEBAPP_NAME = "webappName";
    public static final String RESPONSE_PARAM_VERSION = "version";
    public static final String RESPONSE_PARAM_BUILD_TIME = "buildTime";
    public static final String RESPONSE_PARAM_START_TIME = "startTime";
    public static final String RESPONSE_PARAM_CURRENT_TIME = "currentTime";

    private final ZonedDateTime instanceStarted;
    private final ZonedDateTime built;
    private final String name;
    private final String version;

    public ApplicationHealthIndicator(ManifestReader manifestReader) {
        instanceStarted = ZonedDateTime.now();
        built = getInstanceBuilt(manifestReader);
        name = manifestReader.readFromManifest(MANIFEST_PARAM_APP_NAME);
        version = manifestReader.readFromManifest(MANIFEST_PARAM_APP_VERSION);
    }

    protected static ZonedDateTime convertUtcToLocal(final String buildTime) {
        if (buildTime == null)
            return null;

        LocalDateTime date = null;
        try {
            date = LocalDateTime.parse(buildTime, DEFAULT_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Could not parse the build time! ", e);
            return null;
        }
        ZonedDateTime dateTime = date.atZone(ZoneId.of("UTC"));
        return dateTime.withZoneSameInstant(ZoneId.systemDefault());
    }

    protected static String getFormattedTime(final ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null)
            return null;
        return zonedDateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail(RESPONSE_PARAM_WEBAPP_NAME, formatValue(name))
                .withDetail(RESPONSE_PARAM_VERSION, formatValue(version))
                .withDetail(RESPONSE_PARAM_BUILD_TIME, formatValue(getFormattedTime(built)))
                .withDetail(RESPONSE_PARAM_START_TIME, formatValue(getFormattedTime(instanceStarted)))
                .withDetail(RESPONSE_PARAM_CURRENT_TIME, formatValue(getFormattedTime(ZonedDateTime.now())))
                .build();
    }

    private static ZonedDateTime getInstanceBuilt(ManifestReader manifestReader) {
        String buildTime = manifestReader.readFromManifest(MANIFEST_PARAM_APP_BUILD_TIME);
        return convertUtcToLocal(buildTime);
    }

    private static Object formatValue(final String value) {
        return value == null ? NOT_AVAILABLE : value;
    }

}
