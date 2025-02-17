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

package ee.openeid.tsl.configuration;

import ee.openeid.tsl.TSLRefresher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
public class TSLRefreshConfiguration {
    private TSLRefresher refresher;
    private TSLLoaderConfigurationProperties loaderConfigurationProperties;

    @Bean
    public TaskScheduler tslRefreshTask() {
        final TaskScheduler scheduler = new ConcurrentTaskScheduler();
        scheduler.schedule(refresher, new CronTrigger(loaderConfigurationProperties.getSchedulerCron()));

        return scheduler;
    }

    @Autowired
    public void setRefresher(TSLRefresher refresher) {
        this.refresher = refresher;
    }

    @Autowired
    public void setLoaderConfigurationProperties(TSLLoaderConfigurationProperties loaderConfigurationProperties) {
        this.loaderConfigurationProperties = loaderConfigurationProperties;
    }
}
