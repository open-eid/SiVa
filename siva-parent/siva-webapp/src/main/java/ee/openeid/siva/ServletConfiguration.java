/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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
import ee.openeid.siva.monitoring.configuration.MonitoringConfiguration;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.webapp.configuration.WsdlServiceConfigurationProperties;
import ee.openeid.siva.webapp.soap.DataFilesWebService;
import ee.openeid.siva.webapp.soap.HashcodeValidationWebService;
import ee.openeid.siva.webapp.soap.ValidationWebService;
import ee.openeid.siva.webapp.soap.impl.DataFilesWebServiceImpl;
import ee.openeid.siva.webapp.soap.impl.HashcodeValidationWebServiceImpl;
import ee.openeid.siva.webapp.soap.impl.ValidationWebServiceImpl;
import ee.openeid.siva.webapp.soap.interceptor.ReportSignatureInterceptor;
import ee.openeid.siva.webapp.soap.interceptor.SoapRequestHashcodeValidationInterceptor;
import ee.openeid.siva.webapp.soap.interceptor.SoapRequestValidationInterceptor;
import ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.DispatcherServlet;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
@EnableConfigurationProperties({ReportConfigurationProperties.class, WsdlServiceConfigurationProperties.class})
public class ServletConfiguration extends MonitoringConfiguration {

    private static final String VALIDATION_WEB_SERVICE_ENDPOINT = "/validationWebService";
    private static final String HASHCODE_VALIDATION_WEB_SERVICE_ENDPOINT = "/hashcodeValidationWebService";
    private static final String DATAFILES_ENDPOINT = "/dataFilesWebService";
    private static final String URL_MAPPING = "/soap/*";
    private static final String SIVA_SERVICE_NAMESPACE = "http://soap.webapp.siva.openeid.ee/";
    private static final String XROAD_ENDPOINT_PATH = "XRoad";

    private WsdlServiceConfigurationProperties wsdlConfProperties;

    @Autowired
    @Qualifier("SoapReportSignatureInterceptor")
    private ReportSignatureInterceptor reportSignatureInterceptor;

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public ServletRegistrationBean<CXFServlet> wsRegistrationBean() {
        return new ServletRegistrationBean<>(new CXFServlet(), URL_MAPPING);
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> rsRegistrationBean(ApplicationContext applicationContext) {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        return new ServletRegistrationBean<>(servlet);
    }

    @Bean
    public ValidationWebService validationWebService() {
        return new ValidationWebServiceImpl();
    }

    @Bean
    public HashcodeValidationWebService hashcodeValidationWebService() {
        return new HashcodeValidationWebServiceImpl();
    }

    @Bean
    public DataFilesWebService dataFilesWebService() {
        return new DataFilesWebServiceImpl();
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        return builder;
    }

    @Bean
    public Endpoint validationXRoadEndpoint(SpringBus springBus, ValidationWebService validationWebService) {
        return constructValidationEndpoint(springBus, validationWebService, "XRoadValidationWebService", XROAD_ENDPOINT_PATH);
    }

    @Bean
    public Endpoint validationEndpoint(SpringBus springBus, ValidationWebService validationWebService) {
        return constructValidationEndpoint(springBus, validationWebService, "ValidationWebServiceService", null);
    }

    @Bean
    public Endpoint hashcodeValidationEndpoint(SpringBus springBus, HashcodeValidationWebService validationWebService) {
        return constructHashcodeValidationEndpoint(springBus, validationWebService, "HashcodeValidationWebServiceService", null);
    }

    @Bean
    public Endpoint hashcodeValidationXRoadEndpoint(SpringBus springBus, HashcodeValidationWebService validationWebService) {
        return constructHashcodeValidationEndpoint(springBus, validationWebService, "XRoadHashcodeValidationWebService", XROAD_ENDPOINT_PATH);
    }

    @Bean
    public Endpoint dataFilesEndpoint(SpringBus springBus, DataFilesWebService dataFilesWebService) {
        return constructDataFilesEndpoint(springBus, dataFilesWebService, "DataFilesWebServiceService", null);
    }

    @Bean
    public Endpoint dataFilesXRoadEndpoint(SpringBus springBus, DataFilesWebService dataFilesWebService) {
        return constructDataFilesEndpoint(springBus, dataFilesWebService, "XRoadDataFilesWebService", XROAD_ENDPOINT_PATH);
    }

    @SuppressWarnings("squid:S2095") //False positive for AutoCloseable bean
    private EndpointImpl constructValidationEndpoint(SpringBus springBus, ValidationWebService validationWebService, String serviceName, String endpointPathExtra) {
        EndpointImpl endpoint = new EndpointImpl(springBus, validationWebService);
        endpoint.setWsdlLocation("wsdl/siva.wsdl");
        endpoint.setServiceName(new QName(SIVA_SERVICE_NAMESPACE, serviceName));
        endpoint.getOutInterceptors().addAll(commonEndpointOutInterceptors());
        endpoint.getInInterceptors().add(new SoapRequestValidationInterceptor());
        publishEndpoint(endpoint, VALIDATION_WEB_SERVICE_ENDPOINT, endpointPathExtra);
        return endpoint;
    }

    @SuppressWarnings("squid:S2095") //False positive for AutoCloseable bean
    private EndpointImpl constructHashcodeValidationEndpoint(SpringBus springBus, HashcodeValidationWebService validationWebService, String serviceName, String endpointPathExtra) {
        EndpointImpl endpoint = new EndpointImpl(springBus, validationWebService);
        endpoint.setWsdlLocation("wsdl/siva-hashcode-validation.wsdl");
        endpoint.setServiceName(new QName(SIVA_SERVICE_NAMESPACE, serviceName));
        endpoint.getOutInterceptors().addAll(commonEndpointOutInterceptors());
        endpoint.getInInterceptors().add(new SoapRequestHashcodeValidationInterceptor());
        publishEndpoint(endpoint, HASHCODE_VALIDATION_WEB_SERVICE_ENDPOINT, endpointPathExtra);
        return endpoint;
    }

    @SuppressWarnings("squid:S2095") //False positive for AutoCloseable bean
    private EndpointImpl constructDataFilesEndpoint(SpringBus springBus, DataFilesWebService validationWebService, String serviceName, String endpointPathExtra) {
        EndpointImpl endpoint = new EndpointImpl(springBus, validationWebService);
        endpoint.setWsdlLocation("wsdl/siva-datafiles.wsdl");
        endpoint.setServiceName(new QName(SIVA_SERVICE_NAMESPACE, serviceName));
        publishEndpoint(endpoint, DATAFILES_ENDPOINT, endpointPathExtra);
        return endpoint;
    }

    /**
     *  Solution for having multiple services defined in the same WSDL go against the same endpoint.
     *  For that multiple EndpointImpl instances must be constructed pointing to the specific service.
     *  To construct multiple EndpointImpl instances from the same WSDL the published endpoint name must be different and therefore extra is added.
     *  It does result with additional unnecessary endpoint for reaching WSDL and XSD.
     */
    private void publishEndpoint(EndpointImpl endpoint, String serviceEndpoint, String endpointPathExtra) {
        if (StringUtils.isNotBlank(wsdlConfProperties.getEndpointUrl())) {
            endpoint.setPublishedEndpointUrl(constructPublishedEndpointUrl(wsdlConfProperties.getEndpointUrl(), serviceEndpoint));
        }
        endpoint.publish(constructPublishingPath(serviceEndpoint, endpointPathExtra));
    }

    private String constructPublishedEndpointUrl(String url, String path) {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setPath(uriBuilder.getPath() + (uriBuilder.getPath().endsWith("/") ? "" : "/") + "soap" + path);
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Failed to construct SOAP publication endpoint url from " + url + " and path " + path, e);
        }
    }

    private String constructPublishingPath(String endpointPath, String endpointPathExtra) {
        return endpointPath + (StringUtils.isNotBlank(endpointPathExtra) ? endpointPathExtra : "");
    }

    private List<AbstractSoapInterceptor> commonEndpointOutInterceptors() {
        List<AbstractSoapInterceptor> outInterceptors = new ArrayList<>();
        outInterceptors.add(new SoapResponseHeaderInterceptor());
        outInterceptors.add(new SAAJOutInterceptor());
        outInterceptors.add(reportSignatureInterceptor);
        return outInterceptors;
    }

    @Autowired
    public void setWsdlConfProperties(WsdlServiceConfigurationProperties wsdlConfProperties) {
        this.wsdlConfProperties = wsdlConfProperties;
    }
}
