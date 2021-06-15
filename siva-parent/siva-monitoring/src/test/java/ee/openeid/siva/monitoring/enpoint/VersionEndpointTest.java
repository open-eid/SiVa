/*
 * Copyright 2021 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.monitoring.enpoint;

import ee.openeid.siva.monitoring.util.ApplicationInfoConstants;
import ee.openeid.siva.monitoring.util.ManifestReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.MANIFEST_PARAM_APP_VERSION;
import static ee.openeid.siva.monitoring.util.ApplicationInfoConstants.NOT_AVAILABLE;

@ExtendWith(MockitoExtension.class)
public class VersionEndpointTest {

    private static final String TEST_VERSION = "TEST_VERSION";

    @Mock
    private ManifestReader manifestReader;

    @Test
    public void testVersionMissingInManifestFile() {
        VersionEndpoint versionEndpoint = new VersionEndpoint(manifestReader);

        Map<String, Object> result = versionEndpoint.version();

        Assertions.assertEquals(
                Map.of(VersionEndpoint.RESPONSE_PARAM_VERSION, NOT_AVAILABLE),
                result
        );
        Mockito.verify(manifestReader).readFromManifest(MANIFEST_PARAM_APP_VERSION);
        Mockito.verifyNoMoreInteractions(manifestReader);
    }

    @Test
    public void testVersionFoundInManifestFile() {
        Mockito.doReturn(TEST_VERSION).when(manifestReader).readFromManifest(Mockito.anyString());
        VersionEndpoint versionEndpoint = new VersionEndpoint(manifestReader);

        Map<String, Object> result = versionEndpoint.version();

        Assertions.assertEquals(
                Map.of(VersionEndpoint.RESPONSE_PARAM_VERSION, TEST_VERSION),
                result
        );
        Mockito.verify(manifestReader).readFromManifest(MANIFEST_PARAM_APP_VERSION);
        Mockito.verifyNoMoreInteractions(manifestReader);
    }

}
