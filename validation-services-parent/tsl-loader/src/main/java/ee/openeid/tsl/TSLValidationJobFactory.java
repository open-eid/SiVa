package ee.openeid.tsl;

import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import org.springframework.stereotype.Component;

@Component
public class TSLValidationJobFactory {

    public TSLValidationJob createValidationJob() {
        return new TSLValidationJob();
    }
}
