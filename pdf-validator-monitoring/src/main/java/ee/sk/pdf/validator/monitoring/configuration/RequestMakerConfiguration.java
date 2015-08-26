package ee.sk.pdf.validator.monitoring.configuration;

import ee.sk.pdf.validator.monitoring.logging.LoggingService;
import ee.sk.pdf.validator.monitoring.request.MonitoringRequestConfiguration;
import ee.sk.pdf.validator.monitoring.response.ResponseValidator;
import ee.sk.pdf.validator.monitoring.template.PDFLoader;
import ee.sk.pdf.validator.monitoring.template.RequestTemplateLoader;
import ee.sk.pdf.validator.monitoring.template.TemplateLoader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
@EnableConfigurationProperties
public class RequestMakerConfiguration {
    private final static String PDF_FILE_LOCATION = "/pdf/hellopades-lt-sha512.pdf";
    private final static String REQUEST_TEMPLATE_LOCATION = "/templates/request.xml";

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
    public LoggingService loggingService() {
        return new LoggingService();
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
}
