package ee.openeid.siva.singature;

import eu.europa.esig.dss.*;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class XadesSignatureService implements SignatureService {

    @Override
    public byte[] getSignature(byte[] dataToSign) throws IOException {
        InputStream p12InputStream = this.getClass().getResourceAsStream("/test.p12");

        Pkcs12SignatureToken signatureToken = new Pkcs12SignatureToken(p12InputStream, "password");
        DSSPrivateKeyEntry privateKeyEntry = signatureToken.getKeys().get(0);

        XAdESSignatureParameters parameters = new XAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        parameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        parameters.setSigningCertificate(privateKeyEntry.getCertificate());

        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        XAdESService service = new XAdESService(commonCertificateVerifier);

        DSSDocument documentToBeSigned = new InMemoryDocument(dataToSign);
        MimeType mimeType = new MimeType();
        mimeType.setMimeTypeString("application/json");
        documentToBeSigned.setMimeType(mimeType);

        ToBeSigned toBeSigned = service.getDataToSign(documentToBeSigned, parameters);

        SignatureValue signatureValue = signatureToken.sign(toBeSigned, parameters.getDigestAlgorithm(), privateKeyEntry);
        DSSDocument signedDocument = service.signDocument(documentToBeSigned, parameters, signatureValue);

        return IOUtils.toByteArray(signedDocument.openStream());
    }

}
