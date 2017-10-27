/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.siva;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.openeid.siva.webapp.configuration.SivaWebApplicationConfigurationProperties;
import ee.openeid.siva.webapp.soap.DataFilesWebService;
import ee.openeid.siva.webapp.soap.ValidationWebService;
import ee.openeid.siva.webapp.soap.impl.DataFilesWebServiceImpl;
import ee.openeid.siva.webapp.soap.impl.ValidationWebServiceImpl;
import ee.openeid.siva.webapp.soap.interceptor.ReportSignatureInterceptor;
import ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.DispatcherServlet;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
@EnableConfigurationProperties({SivaWebApplicationConfigurationProperties.class})
public class ServletConfiguration {
    private static final String ENDPOINT = "/validationWebService";
    private static final String DATAFILES_ENDPOINT = "/dataFilesWebService";
    private static final String URL_MAPPING = "/soap/*";

    @Autowired
    @Qualifier("SoapReportSignatureInterceptor")
    ReportSignatureInterceptor reportSignatureInterceptor;

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public ServletRegistrationBean wsRegistrationBean() {
        return new ServletRegistrationBean(new CXFServlet(), URL_MAPPING);
    }

    @Bean
    public ServletRegistrationBean rsRegistrationBean(ApplicationContext applicationContext) {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        return new ServletRegistrationBean(servlet);
    }

    @Bean
    public ValidationWebService validationWebService() {
        return new ValidationWebServiceImpl();
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        return builder;
    }

    @Bean
    public Endpoint endpoint(SpringBus springBus, ValidationWebService validationWebService) {
        EndpointImpl endpoint = new EndpointImpl(springBus, validationWebService);
        endpoint.setWsdlLocation("wsdl/siva.wsdl");

        List<AbstractSoapInterceptor> outInterceptors = new ArrayList<>();
        outInterceptors.add(new SoapResponseHeaderInterceptor());
        outInterceptors.add(new SAAJOutInterceptor());
        outInterceptors.add(reportSignatureInterceptor);
        endpoint.getOutInterceptors().addAll(outInterceptors);

        endpoint.publish(ENDPOINT);
        return endpoint;
    }

    @Bean
    public DataFilesWebService dataFilesWebService() {
        return new DataFilesWebServiceImpl();
    }

    @Bean
    public Endpoint dataFilesEndpoint(SpringBus springBus, DataFilesWebService dataFilesWebService) {
        EndpointImpl endpoint = new EndpointImpl(springBus, dataFilesWebService);
        endpoint.setWsdlLocation("wsdl/siva-datafiles.wsdl");
        endpoint.publish(DATAFILES_ENDPOINT);
        return endpoint;
    }

}
