package ee.openeid.tsl.configuration;

import ee.openeid.tsl.TSLRefresher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TSLRefreshConfigurationTest {

    private static final String SCHEDULER_CRON = "0 0 3 * * ?";

    @Mock
    private TSLRefresher refresher;
    @Mock
    private TSLLoaderConfigurationProperties properties;

    @InjectMocks
    private TSLRefreshConfiguration tslRefreshConfiguration;

    @Test
    public void verifySchedulerCronIsQueriedFromPropertiesWhenCreatingTSLRefreshTask() throws Exception {
        when(properties.getSchedulerCron()).thenReturn(SCHEDULER_CRON);
        tslRefreshConfiguration.tslRefreshTask();
        verify(properties, VerificationModeFactory.times(1)).getSchedulerCron();
    }
}
