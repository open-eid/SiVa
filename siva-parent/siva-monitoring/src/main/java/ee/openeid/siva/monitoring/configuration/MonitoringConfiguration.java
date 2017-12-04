/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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
package ee.openeid.siva.monitoring.configuration;

import ee.openeid.siva.monitoring.indicator.ApplicationHealthIndicator;
import ee.openeid.siva.monitoring.indicator.UrlHealthIndicator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.List;

@EnableConfigurationProperties({MonitoringProperties.class})
public abstract class MonitoringConfiguration implements BeanFactoryAware {

    public static final String DEFAULT_MONITORING_ENDPOINT = "/monitoring/health";
    public static final int DEFAULT_TIMEOUT = 10000;

    private BeanFactory beanFactory;

    private MonitoringProperties healthProperties;

    @PostConstruct
    public void setUpLinkIndicators() throws MalformedURLException {
        setUpExternalLinkIndicators(beanFactory, healthProperties);
    }

    @Bean
    public ApplicationHealthIndicator health(ServletContext context) {
        return new ApplicationHealthIndicator(context);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Autowired
    public void setHealthProperties(final MonitoringProperties healthProperties) {
        this.healthProperties = healthProperties;
    }

    public void setUpExternalLinkIndicators(BeanFactory beanFactory, MonitoringProperties healthProperties) throws MalformedURLException {
        Assert.state(beanFactory instanceof ConfigurableBeanFactory, "wrong bean factory type");
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

        if (healthProperties == null || healthProperties.getLinks().size() == 0) {
            setUpBeans(configurableBeanFactory, getDefaultExternalLinks());
        } else {
            setUpBeans(configurableBeanFactory, healthProperties.getLinks());
        }
    }

    private void setUpBeans(ConfigurableBeanFactory configurableBeanFactory, List<UrlHealthIndicator.ExternalLink> list) throws MalformedURLException {
        int linkIndex = 0;
        for (UrlHealthIndicator.ExternalLink link : list) {
            addNewUrlHealthIndicator(("link" + (++linkIndex)), configurableBeanFactory, link);
        }
    }

    protected void addNewUrlHealthIndicator(String beanName, ConfigurableBeanFactory configurableBeanFactory, UrlHealthIndicator.ExternalLink link) throws MalformedURLException {
        UrlHealthIndicator indicator = new UrlHealthIndicator();
        indicator.setExternalLink(link);
        RestTemplate restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(link.getTimeout());
        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setReadTimeout(link.getTimeout());
        indicator.setRestTemplate(restTemplate);
        configurableBeanFactory.registerSingleton(beanName, indicator);
    }

    public abstract List<UrlHealthIndicator.ExternalLink> getDefaultExternalLinks();
}
