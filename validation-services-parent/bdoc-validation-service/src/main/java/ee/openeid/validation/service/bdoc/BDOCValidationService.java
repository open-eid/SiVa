package ee.openeid.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.digidoc4j.Configuration;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.ValidationResult;
import org.digidoc4j.impl.bdoc.tsl.TSLCertificateSourceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BDOCValidationService implements ValidationService {

    private TrustedListsCertificateSource trustedListSource;

    private Configuration configuration;

    @Override
    public Map<String, String> validateDocument(ValidationDocument validationDocument) {

        initConfiguration();

        InputStream containerInputStream = new ByteArrayInputStream(validationDocument.getBytes());

        ValidationResult validationResult = ContainerBuilder.
                aContainer().
                withConfiguration(configuration).
                fromStream(containerInputStream).
                build().validate();

        Map<String, String> reportMap = new HashMap<>();
        reportMap.put("SIMPLE", validationResult.getReport().toString());
        reportMap.put("DETAILED", validationResult.getReport().toString());
        reportMap.put("DIAGNOSTICDATA", validationResult.getReport().toString());

        return reportMap;
    }

    public void initConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();

            TSLCertificateSourceImpl tslCertificateSource = new TSLCertificateSourceImpl();
            trustedListSource.getCertificates().stream().forEach(certToken -> tslCertificateSource.addTSLCertificate(certToken.getCertificate()));
            configuration.setTSL(tslCertificateSource);
        }
    }

    @Autowired
    public void setTrustedListSource(TrustedListsCertificateSource trustedListSource) {
        this.trustedListSource = trustedListSource;
    }


}
