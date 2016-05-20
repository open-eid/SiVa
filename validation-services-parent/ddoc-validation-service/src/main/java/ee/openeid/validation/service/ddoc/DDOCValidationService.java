package ee.openeid.validation.service.ddoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.ValidationService;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

@Service
public class DDOCValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DDOCValidationService.class);

    private static final String JDIGIDOC_CONF_FILE = "/jdigidoc.cfg";

    private final Object lock = new Object();

    @PostConstruct
    private void initConfig() throws DigiDocException, IOException {
        InputStream inputStream = getClass().getResourceAsStream(JDIGIDOC_CONF_FILE);
        File file = File.createTempFile("jdigidoc", "cfg");
        file.deleteOnExit();
        OutputStream outputStream = new FileOutputStream(file);

        IOUtils.copy(inputStream, outputStream);
        outputStream.close();

//        LOGGER.info("JDigiDoc configuration file path: {}", resource);
        ConfigManager.init(file.getAbsolutePath());
    }

    @Override
    public QualifiedReport validateDocument(ValidationDocument validationDocument) {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        synchronized (lock) {
            SignedDoc signedDoc;
            List<DigiDocException> errors = new ArrayList<>();

            try {
                DigiDocFactory digiDocFactory = ConfigManager.instance().getDigiDocFactory();
                signedDoc = digiDocFactory.readSignedDocFromStreamOfType(new ByteArrayInputStream(validationDocument.getBytes()), false, errors);

                if (signedDoc == null) {
                    throw new RuntimeException(); // this should be replaced with something like "validationexception" in the future
                }

                return new DDOCValidationResult(signedDoc).getQualifiedReport();
            } catch (DigiDocException e) {
                LOGGER.warn("Unexpected exception when validating DDOC document: " + e.getMessage(), e);
                throw new RuntimeException(e); // this should be replaced with something like "validationexception" in the future
            }
        }
    }

}
