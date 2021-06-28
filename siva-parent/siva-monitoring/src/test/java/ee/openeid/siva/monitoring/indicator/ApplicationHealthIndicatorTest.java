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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.MANIFEST_PARAM_APP_BUILD_TIME;
import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.MANIFEST_PARAM_APP_NAME;
import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.MANIFEST_PARAM_APP_VERSION;
import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.NOT_AVAILABLE;

@ExtendWith(MockitoExtension.class)
public class ApplicationHealthIndicatorTest {

    public static final String TEST_WEBAPP = "TEST_WEBAPP";
    public static final String TEST_BUILD_TIME_IN_UTC = "2016-10-21T11:35:23Z";
    public static final String TEST_VERSION = "TEST_VERSION";

    @Mock
    private ManifestReader manifestReader;

    @Test
    public void whenParametersMissingInManifestFile() {
        Instant earliestStartTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator(manifestReader);
        Instant latestStartTime = Instant.now();

        Instant earliestCurrentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Health health = applicationHealthIndicator.health();
        Instant latestCurrentTime = Instant.now();

        Assertions.assertEquals(health.getStatus(), Status.UP);
        Assertions.assertEquals(
                Stream.of(
                        ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME,
                        ApplicationHealthIndicator.RESPONSE_PARAM_VERSION,
                        ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME,
                        ApplicationHealthIndicator.RESPONSE_PARAM_START_TIME,
                        ApplicationHealthIndicator.RESPONSE_PARAM_CURRENT_TIME
                ).collect(Collectors.toCollection(LinkedHashSet::new)), health.getDetails().keySet());
        Assertions.assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        Assertions.assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        Assertions.assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
        verifyTime(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_START_TIME), earliestStartTime, latestStartTime);
        verifyTime(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_CURRENT_TIME), earliestCurrentTime, latestCurrentTime);
    }

    @Test
    public void whenParametersFoundInManifestFile() {
        Mockito.doReturn(TEST_WEBAPP).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_NAME);
        Mockito.doReturn(TEST_BUILD_TIME_IN_UTC).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_BUILD_TIME);
        Mockito.doReturn(TEST_VERSION).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_VERSION);
        Instant earliestStartTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator(manifestReader);
        Instant latestStartTime = Instant.now();

        Instant earliestCurrentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Health health = applicationHealthIndicator.health();
        Instant latestCurrentTime = Instant.now();

        Assertions.assertEquals(health.getStatus(), Status.UP);
        Assertions.assertEquals(TEST_WEBAPP, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        Assertions.assertEquals(TEST_VERSION, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        Assertions.assertEquals(TEST_BUILD_TIME_IN_UTC, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
        verifyTime(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_START_TIME), earliestStartTime, latestStartTime);
        verifyTime(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_CURRENT_TIME), earliestCurrentTime, latestCurrentTime);
    }

    @Test
    public void whenBuildTimeHasInvalidFormatInManifestFile() {
        Mockito.doReturn(TEST_WEBAPP).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_NAME);
        Mockito.doReturn("ABCDEFG1234").when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_BUILD_TIME);
        Mockito.doReturn(TEST_VERSION).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_VERSION);
        Instant earliestStartTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator(manifestReader);
        Instant latestStartTime = Instant.now();

        Instant earliestCurrentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Health health = applicationHealthIndicator.health();
        Instant latestCurrentTime = Instant.now();

        Assertions.assertEquals(health.getStatus(), Status.UP);
        Assertions.assertEquals(TEST_WEBAPP, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        Assertions.assertEquals(TEST_VERSION, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        Assertions.assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
        verifyTime(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_START_TIME), earliestStartTime, latestStartTime);
        verifyTime(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_CURRENT_TIME), earliestCurrentTime, latestCurrentTime);
    }

    private static void verifyTime(Object time, Instant earliest, Instant latest) {
        Assertions.assertNotNull(time);
        Instant parsedTime = null;
        try {
            parsedTime = Instant.parse(time.toString());
        } catch (DateTimeParseException e) {
            Assertions.fail("Failed to parse time", e);
        }
        Assertions.assertFalse(parsedTime.isBefore(earliest), String.format("Time (%s) must not be before %s", parsedTime, earliest));
        Assertions.assertFalse(parsedTime.isAfter(latest), String.format("Time (%s) must not be after %s", parsedTime, latest));
    }

}
