package ee.openeid.siva.signature;

import ee.openeid.siva.signature.configuration.Pkcs11Properties;
import ee.openeid.siva.signature.configuration.Pkcs12Properties;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import ee.openeid.siva.signature.exception.SignatureServiceException;
import ee.openeid.siva.signature.ocsp.SkOcspSource;
import ee.openeid.siva.signature.tsp.SKTimestampDataLoader;
import eu.europa.esig.dss.*;
import eu.europa.esig.dss.asic.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.client.tsp.OnlineTSPSource;
import eu.europa.esig.dss.token.AbstractSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.ocsp.OCSPSource;
import eu.europa.esig.dss.x509.tsp.TSPSource;
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

    @Autowired
    private TrustedListsCertificateSource trustedListSource;

    @Override
    public byte[] getSignature(byte[] dataToSign, String dataName, String mimeTypeString) throws IOException {
        if (properties == null) {
            throw new SignatureServiceException("Signature configuration properties not set!");
        }

        AbstractSignatureTokenConnection signatureToken = getSignatureToken(properties);

        DSSPrivateKeyEntry privateKeyEntry = signatureToken.getKeys().get(0);

        ASiCWithXAdESSignatureParameters parameters = new ASiCWithXAdESSignatureParameters();
        SignatureLevel signatureLevel = getSingatureLevel(properties.getSignatureLevel());
        parameters.setSignatureLevel(signatureLevel);
        parameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        parameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        parameters.aSiC().setMimeType(MimeType.ASICE.getMimeTypeString());
        parameters.setSigningCertificate(privateKeyEntry.getCertificate());

        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        OCSPSource ocspSource = getOcspSource(properties.getOcspUrl());
        commonCertificateVerifier.setOcspSource(ocspSource);
        commonCertificateVerifier.setTrustedCertSource(trustedListSource);

        ASiCWithXAdESService service = new ASiCWithXAdESService(commonCertificateVerifier);
        TSPSource tspSource = getTspSource(properties.getTspUrl());
        service.setTspSource(tspSource);

        DSSDocument documentToBeSigned = new InMemoryDocument(dataToSign, dataName);
        MimeType mimeType = new MimeType();
        mimeType.setMimeTypeString(mimeTypeString);
        documentToBeSigned.setMimeType(mimeType);

        ToBeSigned toBeSigned = service.getDataToSign(documentToBeSigned, parameters);
        SignatureValue signatureValue = signatureToken.sign(toBeSigned, parameters.getDigestAlgorithm(), privateKeyEntry);
        DSSDocument signedDocument = service.signDocument(documentToBeSigned, parameters, signatureValue);

        return IOUtils.toByteArray(signedDocument.openStream());
    }

    public SignatureServiceConfigurationProperties getProperties() {
        return properties;
    }

    public void setProperties(SignatureServiceConfigurationProperties properties) {
        this.properties = properties;
    }

    private AbstractSignatureTokenConnection getSignatureToken(SignatureServiceConfigurationProperties properties) {
        Pkcs11Properties pkcs11Properties = properties.getPkcs11();
        Pkcs12Properties pkcs12Properties = properties.getPkcs12();
        if (pkcs11Properties != null) {
            return new Pkcs11SignatureToken(pkcs11Properties.getPath(), pkcs11Properties.getPassword().toCharArray(), pkcs11Properties.getSlotIndex());
        } else if (pkcs12Properties != null) {
            InputStream p12InputStream = getKeystoreInputStream(pkcs12Properties.getPath());
            return new Pkcs12SignatureToken(p12InputStream, pkcs12Properties.getPassword());
        } else {
            throw new SignatureServiceException("Either Pkcs11 or Pkcs12 must be configured! Currently there is none configured..");
        }
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

    private OCSPSource getOcspSource(String ocspSourceUrl) {
        return new SkOcspSource(ocspSourceUrl);
    }

    private TSPSource getTspSource(String tspSourceUrl) {
        OnlineTSPSource tspSource = new OnlineTSPSource(tspSourceUrl);
        SKTimestampDataLoader dataLoader = new SKTimestampDataLoader();
        tspSource.setDataLoader(dataLoader);
        return tspSource;
    }
}
