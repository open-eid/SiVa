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

package ee.openeid.tsl.configuration;

import ee.openeid.tsl.TSLRefresher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TSLRefreshConfigurationTest {

    private static final String SCHEDULER_CRON = "0 0 3 * * ?";

    @Mock
    private TSLRefresher refresher;
    @Mock
    private TSLLoaderConfigurationProperties properties;

    @InjectMocks
    private TSLRefreshConfiguration tslRefreshConfiguration;

    @Test
    void verifySchedulerCronIsQueriedFromPropertiesWhenCreatingTSLRefreshTask() {
        when(properties.getSchedulerCron()).thenReturn(SCHEDULER_CRON);
        tslRefreshConfiguration.tslRefreshTask();
        verify(properties, VerificationModeFactory.times(1)).getSchedulerCron();
    }
}
