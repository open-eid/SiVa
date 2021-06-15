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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator(manifestReader);

        Health health = applicationHealthIndicator.health();

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
        verifyCorrectStartAndServerTime(health);
    }

    @Test
    public void whenParametersFoundInManifestFile() {
        Mockito.doReturn(TEST_WEBAPP).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_NAME);
        Mockito.doReturn(TEST_BUILD_TIME_IN_UTC).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_BUILD_TIME);
        Mockito.doReturn(TEST_VERSION).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_VERSION);
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator(manifestReader);

        Health health = applicationHealthIndicator.health();

        Assertions.assertEquals(health.getStatus(), Status.UP);
        Assertions.assertEquals(TEST_WEBAPP, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        Assertions.assertEquals(TEST_VERSION, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        Assertions.assertEquals(ApplicationHealthIndicator.getFormattedTime(ApplicationHealthIndicator.convertUtcToLocal(TEST_BUILD_TIME_IN_UTC)), health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
    }

    @Test
    public void whenBuildTimeHasInvalidFormatInManifestFile() {
        Mockito.doReturn(TEST_WEBAPP).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_NAME);
        Mockito.doReturn("ABCDEFG1234").when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_BUILD_TIME);
        Mockito.doReturn(TEST_VERSION).when(manifestReader).readFromManifest(MANIFEST_PARAM_APP_VERSION);
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator(manifestReader);

        Health health = applicationHealthIndicator.health();

        Assertions.assertEquals(health.getStatus(), Status.UP);
        Assertions.assertEquals(TEST_WEBAPP, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        Assertions.assertEquals(TEST_VERSION, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        Assertions.assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
    }


    private void verifyCorrectStartAndServerTime(Health health) {
        LocalDateTime startTime = parseDate(String.valueOf(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_START_TIME)));
        assertTimeDifferenceWithNowInSecondsIsLessThan(startTime, 10);
        LocalDateTime currentTime = parseDate(String.valueOf(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_CURRENT_TIME)));
        assertTimeDifferenceWithNowInSecondsIsLessThan(currentTime, 10);
        Assertions.assertTrue(currentTime.compareTo(startTime) >= 0, "Current time must be equal or greater to start time!");
    }

    private LocalDateTime parseDate(String dateTimeText) {
        return LocalDateTime.parse(dateTimeText, DateTimeFormatter.ofPattern(ApplicationHealthIndicator.DEFAULT_DATE_TIME_FORMAT));
    }

    private void assertTimeDifferenceWithNowInSecondsIsLessThan(LocalDateTime startDate, int seconds) {
        Assertions.assertTrue(startDate.until(LocalDateTime.now(), ChronoUnit.SECONDS) < seconds);
    }
}
