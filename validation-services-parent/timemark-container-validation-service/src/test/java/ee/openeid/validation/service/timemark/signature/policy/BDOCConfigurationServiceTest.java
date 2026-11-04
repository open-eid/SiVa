/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.validator.AIASourceFactory;
import ee.openeid.validation.service.timemark.configuration.BDOCSignaturePolicyProperties;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import org.digidoc4j.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDOCConfigurationServiceTest {

    @Mock
    private BDOCSignaturePolicyService policyService;
    @Mock
    private BDOCSignaturePolicyProperties properties;
    @Mock
    private ConstraintDefinedPolicy policy;
    @Mock
    private Configuration configuration;
    @Mock
    private Configuration policyConfiguration;
    @Mock
    private AIASourceFactory aiaSourceFactory;
    @Mock
    private AIASource aiaSource;

    @Test
    void loadAllBDOCConfigurations_SetsSivaAiaSourceFactoryOnPolicyConfiguration() {
        when(policy.getName()).thenReturn("POLv4");
        when(properties.getAbstractPolicies()).thenReturn(List.of(policy));
        when(configuration.copy()).thenReturn(policyConfiguration);
        when(aiaSourceFactory.create()).thenReturn(aiaSource);

        new BDOCConfigurationService(properties, policyService, configuration, aiaSourceFactory).loadAllBDOCConfigurations();

        ArgumentCaptor<org.digidoc4j.AIASourceFactory> captor = ArgumentCaptor.forClass(org.digidoc4j.AIASourceFactory.class);
        verify(policyConfiguration).setAiaSourceFactory(captor.capture());
        assertThat(captor.getValue().create(), sameInstance(aiaSource));
        verify(configuration, never()).setAiaSourceFactory(any());
    }
}
