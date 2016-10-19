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
package ee.openeid.siva.monitoring.indicator;


import com.jcabi.manifests.Manifests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Manifests.class})
public class ApplicationHealthIndicatorTest {

    public static final String TEST_WEBAPP = "TEST_WEBAPP";
    public static final String TEST_BUILD_TIME_IN_UTC = "2016-10-21T11:35:23Z";
    public static final String TEST_VERSION = "TEST_VERSION";
    public static final String NOT_AVAILABLE = "N/A";

    private static ApplicationHealthIndicator applicationHealthIndicator;

    @Before
    public void setUp() {
        mockStatic(Manifests.class);
    }

    @Test
    public void whenParametersMissingInManifestFile() {
        applicationHealthIndicator = new ApplicationHealthIndicator(mock(ServletContext.class));

        Health health = applicationHealthIndicator.health();

        assertEquals(health.getStatus(), Status.UP);
        assertEquals(
                Stream.of(
                        ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME,
                        ApplicationHealthIndicator.RESPONSE_PARAM_VERSION,
                        ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME,
                        ApplicationHealthIndicator.RESPONSE_PARAM_START_TIME,
                        ApplicationHealthIndicator.RESPONSE_PARAM_CURRENT_TIME
                ).collect(Collectors.toCollection(LinkedHashSet::new)), health.getDetails().keySet());
        assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
        verifyCorrectStartAndServerTime(health);
    }

    @Test
    public void whenParametersFoundInManifestFile() {
        when(Manifests.read(ApplicationHealthIndicator.MANIFEST_PARAM_NAME)).thenReturn(TEST_WEBAPP);
        when(Manifests.read(ApplicationHealthIndicator.MANIFEST_PARAM_BUILD_TIME)).thenReturn(TEST_BUILD_TIME_IN_UTC);
        when(Manifests.read(ApplicationHealthIndicator.MANIFEST_PARAM_VERSION)).thenReturn(TEST_VERSION);
        applicationHealthIndicator = new ApplicationHealthIndicator(mock(ServletContext.class));

        Health health = applicationHealthIndicator.health();

        assertEquals(health.getStatus(), Status.UP);
        assertEquals(TEST_WEBAPP, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        assertEquals(TEST_VERSION, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        assertEquals(ApplicationHealthIndicator.getFormattedTime(ApplicationHealthIndicator.convertUtcToLocal(TEST_BUILD_TIME_IN_UTC)), health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
    }

    @Test
    public void whenBuildTimeHasInvalidFormatInManifestFile() {
        when(Manifests.read(ApplicationHealthIndicator.MANIFEST_PARAM_NAME)).thenReturn(TEST_WEBAPP);
        when(Manifests.read(ApplicationHealthIndicator.MANIFEST_PARAM_BUILD_TIME)).thenReturn("ABCDEFG1234");
        when(Manifests.read(ApplicationHealthIndicator.MANIFEST_PARAM_VERSION)).thenReturn(TEST_VERSION);
        applicationHealthIndicator = new ApplicationHealthIndicator(mock(ServletContext.class));

        Health health = applicationHealthIndicator.health();

        assertEquals(health.getStatus(), Status.UP);
        assertEquals(TEST_WEBAPP, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_WEBAPP_NAME));
        assertEquals(TEST_VERSION, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_VERSION));
        assertEquals(NOT_AVAILABLE, health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_BUILD_TIME));
    }


    private void verifyCorrectStartAndServerTime(Health health) {
        LocalDateTime startTime = parseDate(String.valueOf(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_START_TIME)));
        assertTimeDifferenceWithNowInSecondsIsLessThan(startTime, 10);
        LocalDateTime currentTime = parseDate(String.valueOf(health.getDetails().get(ApplicationHealthIndicator.RESPONSE_PARAM_CURRENT_TIME)));
        assertTimeDifferenceWithNowInSecondsIsLessThan(currentTime, 10);
        Assert.assertTrue("Current time must be equal or greater to start time!", currentTime.compareTo(startTime) >= 0);
    }

    private LocalDateTime parseDate(String dateTimeText) {
        return LocalDateTime.parse(dateTimeText, DateTimeFormatter.ofPattern(ApplicationHealthIndicator.DEFAULT_DATE_TIME_FORMAT));
    }

    private void assertTimeDifferenceWithNowInSecondsIsLessThan(LocalDateTime startDate, int seconds) {
        Assert.assertTrue(startDate.until(LocalDateTime.now(), ChronoUnit.SECONDS) < seconds);
    }
}
