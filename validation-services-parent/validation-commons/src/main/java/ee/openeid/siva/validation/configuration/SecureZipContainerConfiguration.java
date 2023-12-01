/*
 * Copyright 2021 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.validation.security.ThreadSafeSecureContainerHandler;
import eu.europa.esig.dss.asic.common.SecureContainerHandler;
import eu.europa.esig.dss.asic.common.ZipContainerHandler;
import eu.europa.esig.dss.asic.common.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.function.Supplier;

@Configuration
public class SecureZipContainerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureZipContainerConfiguration.class);

    /**
     * Set up a custom thread-safe secure container handler for global {@link ZipUtils}.
     *
     * In DSS version 5.9, {@link SecureContainerHandler} is not thread-safe, but {@link ZipUtils}
     * is used as a global singleton which uses a single instance of {@link SecureContainerHandler}.
     *
     * TODO: Remove this when SecureContainerHandler becomes thread-safe.
     */
    @PostConstruct
    public void setUpGlobalThreadSafeSecureContainerHandler() {
        LOGGER.info("Setting up global secure container handler");
        ZipUtils.getInstance().setZipContainerHandler(createThreadSafeSecureContainerHandler());
    }

    private static ZipContainerHandler createThreadSafeSecureContainerHandler() {
        Supplier<ZipContainerHandler> containerHandlerFactory = SecureZipContainerConfiguration::createSecureContainerHandler;
        return new ThreadSafeSecureContainerHandler(containerHandlerFactory);
    }

    private static ZipContainerHandler createSecureContainerHandler() {
        return new SecureContainerHandler();
    }

}
