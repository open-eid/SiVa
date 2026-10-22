/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark.configuration;

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.util.ResourceUtil;
import ee.openeid.tsl.TrustedListsCertificateSourceHolder;
import ee.openeid.tsl.annotation.LoadableTsl;
import ee.openeid.validation.service.timemark.TimemarkContainerValidationReportBuilderFactory;
import ee.openeid.validation.service.timemark.tsl.TimemarkTrustedListsCertificateSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.digidoc4j.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_POLICY_SERVICE_BEAN_NAME;
import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME;
import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_TSL_NAME;

@RequiredArgsConstructor
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties({
        BDOCSignaturePolicyProperties.class,
        BDOCValidationServiceProperties.class
})
public class TimemarkContainerValidationServiceConfiguration {

    private final @NonNull BDOCValidationServiceProperties bdocValidationServiceProperties;

    @Bean(name = TM_POLICY_SERVICE_BEAN_NAME)
    public ConstraintLoadingSignaturePolicyService timemarkSignaturePolicyService(BDOCSignaturePolicyProperties properties) {
        return new ConstraintLoadingSignaturePolicyService(properties);
    }

    @Bean
    public TimemarkContainerValidationReportBuilderFactory timemarkContainerValidationReportBuilderFactory() {
        return new TimemarkContainerValidationReportBuilderFactory();
    }

    @LoadableTsl(name = TM_TSL_NAME)
    @Bean(name = TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME)
    public TrustedListsCertificateSourceHolder timemarkTrustedListsCertificateSource() {
        return new TrustedListsCertificateSourceHolder();
    }

    @Bean
    public TimemarkTrustedListsCertificateSource timemarkTrustedListsCertificateSourceView(
            @Qualifier(TM_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME) TrustedListsCertificateSourceHolder trustedListsCertificateSource
    ) {
        return new TimemarkTrustedListsCertificateSource(trustedListsCertificateSource);
    }

    // digidoc4j validates a container's signatures in parallel on the configuration's executor (its own default
    // is a fixed pool of one thread per processor); this one carries the pinned trust source onto those threads.
    // The Configuration beans below call this method, which Spring routes to the one bean instance.
    @Bean(destroyMethod = "shutdown")
    public ExecutorService timemarkSignatureValidationExecutor(
            TimemarkTrustedListsCertificateSource trustedListsCertificateSource
    ) {
        AtomicInteger count = new AtomicInteger();
        ThreadFactory threads = task -> {
            Thread thread = new Thread(task, "timemark-signature-validation-" + count.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
        return trustedListsCertificateSource.pinning(
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threads));
    }

    @Bean
    @Profile("test")
    public Configuration testDigiDoc4JConfiguration(TimemarkTrustedListsCertificateSource trustedListsCertificateSource) {
        Configuration configuration = Configuration.of(Configuration.Mode.TEST);
        ResourceUtil.loadResourceTo(getResourceOrDefault("/siva-digidoc4j-test.yaml"), configuration::loadConfiguration);
        configuration.setTSL(trustedListsCertificateSource);
        configuration.setThreadExecutor(timemarkSignatureValidationExecutor(trustedListsCertificateSource));
        return configuration;
    }

    @Bean
    @Profile("!test")
    public Configuration prodDigiDoc4JConfiguration(TimemarkTrustedListsCertificateSource trustedListsCertificateSource) {
        Configuration configuration = Configuration.of(Configuration.Mode.PROD);
        ResourceUtil.loadResourceTo(getResourceOrDefault("/siva-digidoc4j.yaml"), configuration::loadConfiguration);
        configuration.setTSL(trustedListsCertificateSource);
        configuration.setThreadExecutor(timemarkSignatureValidationExecutor(trustedListsCertificateSource));
        return configuration;
    }

    private Resource getResourceOrDefault(String defaultPath) {
        return Optional
                .ofNullable(bdocValidationServiceProperties.getDigidoc4JConfigurationFile())
                .orElseGet(() -> new ClassPathResource(defaultPath, this.getClass().getClassLoader()));
    }

}
