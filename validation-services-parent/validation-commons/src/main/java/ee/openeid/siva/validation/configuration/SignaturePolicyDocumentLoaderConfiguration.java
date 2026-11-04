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

package ee.openeid.siva.validation.configuration;

import ee.openeid.siva.validation.validator.ProtocolRestrictedHTTPDataLoader;
import ee.openeid.siva.validation.validator.SignaturePolicyProviderFactory;
import eu.europa.esig.dss.spi.policy.SignaturePolicyProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SignaturePolicyDocumentLoaderProperties.class)
public class SignaturePolicyDocumentLoaderConfiguration {

    @Bean
    public SignaturePolicyProviderFactory signaturePolicyProviderFactory(SignaturePolicyDocumentLoaderProperties properties) {
        return () -> {
            ProtocolRestrictedHTTPDataLoader dataLoader = new ProtocolRestrictedHTTPDataLoader(properties.getAcceptedProtocols());
            dataLoader.setConnectTimeout(properties.connectTimeoutMillis());
            dataLoader.setReadTimeout(properties.readTimeoutMillis());
            dataLoader.setMaxInputSize(properties.maxResponseSizeBytes());

            // Create and configure SignaturePolicyProvider explicitly in order to restore DSS 6.0.1 behavior where
            //  signature policy is reliably accessible for all signature validation stages requiring its presence.
            // This implementation is based on:
            //  https://github.com/open-eid/sd-dss/blob/dss-6.0.1.d4j.1/dss-document/src/main/java/eu/europa/esig/dss/validation/SignedDocumentValidator.java#L754-L755
            SignaturePolicyProvider signaturePolicyProvider = new SignaturePolicyProvider();
            signaturePolicyProvider.setDataLoader(dataLoader);
            return signaturePolicyProvider;
        };
    }
}
