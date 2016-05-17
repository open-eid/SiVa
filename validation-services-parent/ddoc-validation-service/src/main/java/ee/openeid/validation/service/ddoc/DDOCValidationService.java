package ee.openeid.validation.service.ddoc;

import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class DDOCValidationService implements ValidationService {


    private static final String JDIGIDOC_CONF_FILE = "/jdigidoc.cfg";

    @Override
    public QualifiedValidationResult validateDocument(ValidationDocument validationDocument) {

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        try {
            initConfig();
        } catch (DigiDocException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SignedDoc signedDoc = null;
        List<DigiDocException> errorMessages = new ArrayList<>();

        try {
            DigiDocFactory digiDocFactory = ConfigManager.instance().getDigiDocFactory();
            signedDoc = digiDocFactory.readSignedDocFromStreamOfType(new ByteArrayInputStream(validationDocument.getBytes()), false, errorMessages);
        } catch (DigiDocException e) {
            e.printStackTrace();
        }

        return new DDOCValidationResult(signedDoc);
    }

    private void initConfig() throws DigiDocException, IOException {
        File confFile = new ClassPathResource(JDIGIDOC_CONF_FILE).getFile();
        ConfigManager.init(confFile.getAbsolutePath());
    }

}
