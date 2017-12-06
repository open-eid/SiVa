/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

import com.jcabi.manifests.Manifests;
import com.jcabi.manifests.ServletMfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApplicationHealthIndicator implements HealthIndicator {

    public static final String MANIFEST_PARAM_NAME = "SiVa-Webapp-Name";
    public static final String MANIFEST_PARAM_VERSION = "SiVa-Webapp-Version";
    public static final String MANIFEST_PARAM_BUILD_TIME = "SiVa-Webapp-Build-Time";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String NOT_AVAILABLE = "N/A";
    public static final String RESPONSE_PARAM_WEBAPP_NAME = "webappName";
    public static final String RESPONSE_PARAM_VERSION = "version";
    public static final String RESPONSE_PARAM_BUILD_TIME = "buildTime";
    public static final String RESPONSE_PARAM_START_TIME = "startTime";
    public static final String RESPONSE_PARAM_CURRENT_TIME = "currentTime";
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationHealthIndicator.class);
    private ZonedDateTime instanceStarted = null;
    private ZonedDateTime built = null;
    private String name = null;
    private String version;

    public ApplicationHealthIndicator(final ServletContext servletContext) {
        registerManifests(servletContext);
        setIndicators();
    }

    protected static ZonedDateTime convertUtcToLocal(final String buildTime) {
        if (buildTime == null)
            return null;

        LocalDateTime date = null;
        try {
            date = LocalDateTime.parse(buildTime, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
        } catch (DateTimeParseException e) {
            LOGGER.error("Could not parse the build time! ", e);
            return null;
        }
        ZonedDateTime dateTime = date.atZone(ZoneId.of("UTC"));
        dateTime.toInstant();
        return dateTime.withZoneSameInstant(ZoneId.systemDefault());
    }

    protected static String getFormattedTime(final ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return zonedDateTime.format(formatter);
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

    private void registerManifests(final ServletContext servletContext) {
        try {
            Manifests.DEFAULT.append(new ServletMfs(servletContext));
        } catch (Exception e) {
            LOGGER.error("Failed to set up " + e.getMessage(), e);
        }
    }

    private void setIndicators() {
        instanceStarted = ZonedDateTime.now();
        built = getInstanceBuilt(MANIFEST_PARAM_BUILD_TIME);
        name = getFromManifestFile(MANIFEST_PARAM_NAME);
        version = getFromManifestFile(MANIFEST_PARAM_VERSION);
    }

    private ZonedDateTime getInstanceBuilt(final String parameterName) {
        String buildTime = getFromManifestFile(parameterName);
        return convertUtcToLocal(buildTime);
    }

    private Object formatValue(final String value) {
        return value == null ? NOT_AVAILABLE : value;
    }

    private String getFromManifestFile(final String parameterName) {
        try {
            return Manifests.read(parameterName);
        } catch (Exception e) {
            LOGGER.warn("Failed to fetch parameter '" + parameterName + "' from manifest file! Either you are not running the application as a jar/war package or there is a problem with the build configuration. " + e.getMessage());
            return null;
        }
    }
}
