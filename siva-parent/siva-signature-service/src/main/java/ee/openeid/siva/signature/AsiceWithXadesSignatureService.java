package ee.openeid.siva.signature;

import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import ee.openeid.siva.signature.exception.SignatureServiceException;
import eu.europa.esig.dss.*;
import eu.europa.esig.dss.asic.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class AsiceWithXadesSignatureService implements SignatureService {

    private static final String CLASSPATH = "classpath:";

    @Autowired
    private SignatureServiceConfigurationProperties properties;

    @Override
    public byte[] getSignature(byte[] dataToSign, String dataName, String mimeTypeString) throws IOException {
        InputStream p12InputStream = getKeystoreInputStream(properties.getKeystorePath());
        Pkcs12SignatureToken signatureToken = new Pkcs12SignatureToken(p12InputStream, properties.getKeystorePassword());
        DSSPrivateKeyEntry privateKeyEntry = signatureToken.getKeys().get(0);

        ASiCWithXAdESSignatureParameters parameters = new ASiCWithXAdESSignatureParameters();
        SignatureLevel signatureLevel = getSingatureLevel(properties.getSignatureLevel());
        parameters.setSignatureLevel(signatureLevel);
        parameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        parameters.aSiC().setMimeType(MimeType.ASICE.getMimeTypeString());
        parameters.setSigningCertificate(privateKeyEntry.getCertificate());

        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        ASiCWithXAdESService service = new ASiCWithXAdESService(commonCertificateVerifier);

        DSSDocument documentToBeSigned = new InMemoryDocument(dataToSign, dataName);
        MimeType mimeType = new MimeType();
        mimeType.setMimeTypeString(mimeTypeString);
        documentToBeSigned.setMimeType(mimeType);

        ToBeSigned toBeSigned = service.getDataToSign(documentToBeSigned, parameters);
        SignatureValue signatureValue = signatureToken.sign(toBeSigned, parameters.getDigestAlgorithm(), privateKeyEntry);
        DSSDocument signedDocument = service.signDocument(documentToBeSigned, parameters, signatureValue);

        return IOUtils.toByteArray(signedDocument.openStream());
    }

    public void setProperties(SignatureServiceConfigurationProperties properties) {
        this.properties = properties;
    }

    private InputStream getKeystoreInputStream(String keystorePath) {
        try {
            if (keystorePath.startsWith(CLASSPATH)) {
                InputStream resourceInputStream = getClass().getClassLoader().getResourceAsStream(keystorePath.replace(CLASSPATH, ""));
                if (resourceInputStream == null) {
                    throw new FileNotFoundException("Resource not found on classpath!");
                }
                return resourceInputStream;
            } else {
                return new FileInputStream(keystorePath);
            }
        } catch (FileNotFoundException e) {
            throw new SignatureServiceException("Error reading keystore from path: " + keystorePath, e);
        }
    }

    private SignatureLevel getSingatureLevel(String signatureLevel) {
        try {
            return SignatureLevel.valueOf(signatureLevel);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SignatureServiceException("Invalid signature level - '" + signatureLevel + "'! Valid signature levels: " + Arrays.asList(SignatureLevel.values()));
        }
    }
}
