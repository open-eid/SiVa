package ee.sk.pdf.validator.monitoring.configuration;

import ee.sk.pdf.validator.monitoring.message.MessageService;
import ee.sk.pdf.validator.monitoring.request.MonitoringRequestConfiguration;
import ee.sk.pdf.validator.monitoring.request.TslStatusRequestMaker;
import ee.sk.pdf.validator.monitoring.response.ResponseValidator;
import ee.sk.pdf.validator.monitoring.template.PDFLoader;
import ee.sk.pdf.validator.monitoring.template.RequestTemplateLoader;
import ee.sk.pdf.validator.monitoring.template.TemplateLoader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
@EnableConfigurationProperties
public class RequestMakerConfiguration {
    private static final String PDF_FILE_LOCATION = "/pdf/hellopades-lt-sha512.pdf";
    private static final String REQUEST_TEMPLATE_LOCATION = "/templates/request.xml";

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    public TemplateLoader templateLoader() {
        RequestTemplateLoader requestTemplateLoader = new RequestTemplateLoader();
        requestTemplateLoader.setTemplateLocation(REQUEST_TEMPLATE_LOCATION);

        return requestTemplateLoader;
    }

    @Bean
    public PDFLoader pdfLoader() {
        PDFLoader pdfLoader = new PDFLoader();
        pdfLoader.setPdfFileLocation(PDF_FILE_LOCATION);

        return pdfLoader;
    }

    @Bean
    public MessageService loggingService() {
        return new MessageService();
    }

    @Bean
    public MonitoringRequestConfiguration monitoringConfiguration() {
        return new MonitoringRequestConfiguration();
    }

    @Bean
    public ResponseValidator responseValidator() {
        return new ResponseValidator();
    }

    @Bean
    public Client webServiceClient() {
        return ClientBuilder.newClient();
    }

    @Bean
    public TslStatusRequestMaker tslStatusRequestMaker() {
        return new TslStatusRequestMaker();
    }
}
