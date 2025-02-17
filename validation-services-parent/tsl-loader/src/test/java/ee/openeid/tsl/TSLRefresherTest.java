/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.tsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TSLRefresherTest {

    @Mock
    private TSLLoader tslLoader1;
    @Mock
    private TSLLoader tslLoader2;

    private TSLRefresher tslRefresher;

    @BeforeEach
    void setUp() {
        tslRefresher = new TSLRefresher();
        tslRefresher.setLoaders(Set.of(tslLoader1, tslLoader2));
    }

    @Test
    void init_WhenTslLoadersSet_AllTslLoadersAreRefreshed() {
        tslRefresher.init();

        verify(tslLoader1).loadTSL();
        verify(tslLoader2).loadTSL();
        verifyNoMoreInteractions(tslLoader1, tslLoader2);
    }

    @Test
    void run_WhenTslLoadersSet_AllTslLoadersAreRefreshed() {
        tslRefresher.run();

        verify(tslLoader1).loadTSL();
        verify(tslLoader2).loadTSL();
        verifyNoMoreInteractions(tslLoader1, tslLoader2);
    }

}
