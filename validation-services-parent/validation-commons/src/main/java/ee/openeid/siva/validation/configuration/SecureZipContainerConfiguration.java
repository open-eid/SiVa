package ee.openeid.siva.validation.configuration;

import eu.europa.esig.dss.asic.common.SecureContainerHandler;
import eu.europa.esig.dss.asic.common.ZipContainerHandler;
import eu.europa.esig.dss.model.DSSDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class SecureZipContainerConfiguration {

    /**
     * Returns an instance of {@link ZipContainerHandler} whose
     * {@link ZipContainerHandler#extractContainerContent(DSSDocument)} method can be used to prevent
     * denial of service attacks, such as zip-bombing.
     *
     * @see SecureContainerHandler
     *
     * @return an instance of {@link SecureContainerHandler}
     */
    @Bean
    public Supplier<ZipContainerHandler> zipContainerHandlerFactory() {
        // TODO (SIVA-287): Configure SecureContainerHandler here as appropriate for securing against ZIP bombing
        return SecureContainerHandler::new;
    }

}
