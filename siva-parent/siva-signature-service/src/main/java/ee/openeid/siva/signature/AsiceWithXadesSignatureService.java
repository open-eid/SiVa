/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.signature;

import ee.openeid.siva.signature.configuration.Pkcs11Properties;
import ee.openeid.siva.signature.configuration.Pkcs12Properties;
import ee.openeid.siva.signature.configuration.SignatureServiceConfigurationProperties;
import ee.openeid.siva.signature.exception.SignatureServiceException;
import ee.openeid.siva.signature.ocsp.SkOcspSource;
import ee.openeid.siva.signature.tsp.SKTimestampDataLoader;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.token.AbstractSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
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
        parameters.setEncryptionAlgorithm(privateKeyEntry.getEncryptionAlgorithm());

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

    public void setProperties(SignatureServiceConfigurationProperties signatureServiceConfigurationProperties) {
        properties = signatureServiceConfigurationProperties;
    }

    private AbstractSignatureTokenConnection getSignatureToken(SignatureServiceConfigurationProperties configurationProperties) {
        Pkcs11Properties pkcs11Properties = configurationProperties.getPkcs11();
        Pkcs12Properties pkcs12Properties = configurationProperties.getPkcs12();
        if (pkcs11Properties != null) {
            return new Pkcs11SignatureToken(pkcs11Properties.getPath(), new KeyStore.PasswordProtection(pkcs11Properties.getPassword().toCharArray()), pkcs11Properties.getSlotIndex());
        } else if (pkcs12Properties != null) {
            InputStream p12InputStream = getKeystoreInputStream(pkcs12Properties.getPath());
            return new Pkcs12SignatureToken(p12InputStream, new KeyStore.PasswordProtection(pkcs12Properties.getPassword().toCharArray()));
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
