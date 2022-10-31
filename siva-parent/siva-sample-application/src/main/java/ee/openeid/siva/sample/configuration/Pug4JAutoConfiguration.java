package ee.openeid.siva.sample.configuration;

import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.spring.template.SpringTemplateLoader;
import de.neuland.pug4j.spring.view.PugViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;

@Configuration
public class Pug4JAutoConfiguration {

    @Bean
    public SpringTemplateLoader templateLoader() {
        SpringTemplateLoader templateLoader = new SpringTemplateLoader();
        templateLoader.setTemplateLoaderPath("classpath:/templates");
        templateLoader.setEncoding("UTF-8");
        templateLoader.setSuffix(".jade");
        return templateLoader;
    }

    @Bean
    public PugConfiguration pugConfiguration() {
        PugConfiguration configuration = new PugConfiguration();
        configuration.setCaching(false);
        configuration.setTemplateLoader(templateLoader());
        return configuration;
    }

    @Bean
    public ViewResolver viewResolver() {
        PugViewResolver viewResolver = new PugViewResolver();
        viewResolver.setConfiguration(pugConfiguration());
        return viewResolver;
    }
}
