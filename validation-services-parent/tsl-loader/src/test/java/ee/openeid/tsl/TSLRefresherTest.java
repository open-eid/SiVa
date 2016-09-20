package ee.openeid.tsl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TSLRefresherTest {

    @Mock
    private TSLLoader tslLoader;

    @InjectMocks
    private TSLRefresher tslRefresher;

    @Test
    public void runningTslRefresherShouldLoadTSL() throws Exception {
        tslRefresher.run();
        verify(tslLoader).loadTSL();
        verifyNoMoreInteractions(tslLoader);
    }
}
