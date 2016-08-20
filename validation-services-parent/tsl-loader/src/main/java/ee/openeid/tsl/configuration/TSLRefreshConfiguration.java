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
