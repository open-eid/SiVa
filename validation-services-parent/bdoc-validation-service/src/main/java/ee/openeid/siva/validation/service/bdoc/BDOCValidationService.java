package ee.openeid.siva.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.bdoc.report.qualified.builder.QualifiedReportBuilder;
import org.digidoc4j.Configuration;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.ValidationResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

public class BDOCValidationService implements ValidationService {

    @Override
    public BDOCValidationResult validateDocument(ValidationDocument validationDocument) {

        // TODO: Make configuration mode configurable
        Configuration configuration = new Configuration(Configuration.Mode.PROD);

        InputStream containerInputStream = new ByteArrayInputStream(validationDocument.getBytes());

        Container container = ContainerBuilder.
                aContainer().
                withConfiguration(configuration).
                fromStream(containerInputStream).
                build();

        ValidationResult validationResult = container.validate();
        Date validationTime = new Date();

        QualifiedReportBuilder reportBuilder = new QualifiedReportBuilder(container, validationDocument.getName(), validationTime);
        QualifiedReport qualifiedReport = reportBuilder.build();

        BDOCValidationResult bdocValidationResult = new BDOCValidationResult(validationResult);
        bdocValidationResult.setQualifiedReport(qualifiedReport);

        return bdocValidationResult;
    }
}
