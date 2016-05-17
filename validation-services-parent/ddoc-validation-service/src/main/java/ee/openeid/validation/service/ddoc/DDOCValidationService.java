package ee.openeid.validation.service.ddoc;

import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class DDOCValidationService implements ValidationService {

    private static final Logger logger = LoggerFactory.getLogger(DDOCValidationService.class);

    private static final String JDIGIDOC_CONF_FILE = "/jdigidoc.cfg";

    private final Object lock = new Object();

    @PostConstruct
    private void initConfig() throws DigiDocException, IOException {
        File confFile = new ClassPathResource(JDIGIDOC_CONF_FILE).getFile();
        ConfigManager.init(confFile.getAbsolutePath());
    }

    @Override
    public QualifiedValidationResult validateDocument(ValidationDocument validationDocument) {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        synchronized (lock) {
            SignedDoc signedDoc = null;
            List<DigiDocException> errors = new ArrayList<>();

            try {
                DigiDocFactory digiDocFactory = ConfigManager.instance().getDigiDocFactory();
                signedDoc = digiDocFactory.readSignedDocFromStreamOfType(new ByteArrayInputStream(validationDocument.getBytes()), false, errors);

                if (signedDoc == null) {
                    throw new RuntimeException(); // this should be replaced with something like "validationexception" in the future
                }

                return new DDOCValidationResult(signedDoc);
            } catch (DigiDocException e) {
                logger.warn("Unexpected exception when validating DDOC document: " + e.getMessage(), e);
                throw new RuntimeException(e); // this should be replaced with something like "validationexception" in the future
            }
        }
    }

}
