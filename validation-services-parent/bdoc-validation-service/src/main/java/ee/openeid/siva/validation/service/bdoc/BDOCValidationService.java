package ee.openeid.siva.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import org.digidoc4j.Configuration;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.ValidationResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BDOCValidationService implements ValidationService {

    @Override
    public Map<String, String> validateDocument(ValidationDocument validationDocument) {

        // TODO: Make configuration mode configurable
        Configuration configuration = new Configuration(Configuration.Mode.PROD);

        InputStream containerInputStream = new ByteArrayInputStream(validationDocument.getBytes());

        ValidationResult validationResult = ContainerBuilder.
                aContainer("BDOC").
                withConfiguration(configuration).
                fromStream(containerInputStream).
                build().validate();

        Map<String, String> reportMap = new HashMap<>();
        reportMap.put("SIMPLE", validationResult.getReport().toString());
        reportMap.put("DETAILED", validationResult.getReport().toString());
        reportMap.put("DIAGNOSTICDATA", validationResult.getReport().toString());

        return reportMap;
    }

}
