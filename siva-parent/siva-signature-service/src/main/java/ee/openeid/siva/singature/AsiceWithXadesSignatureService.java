package ee.openeid.siva.singature;

import eu.europa.esig.dss.*;
import eu.europa.esig.dss.asic.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class AsiceWithXadesSignatureService implements SignatureService {

    @Override
    public byte[] getSignature(byte[] dataToSign, String dataName, String mimeTypeString) throws IOException {
        InputStream p12InputStream = this.getClass().getResourceAsStream("/test.p12");

        Pkcs12SignatureToken signatureToken = new Pkcs12SignatureToken(p12InputStream, "password");
        DSSPrivateKeyEntry privateKeyEntry = signatureToken.getKeys().get(0);

        ASiCWithXAdESSignatureParameters parameters = new ASiCWithXAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
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

}
