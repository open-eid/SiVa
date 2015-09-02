package eu.europa.esig.dss.xades.requirements;

import eu.europa.esig.dss.*;
import eu.europa.esig.dss.signature.SignaturePackaging;
import eu.europa.esig.dss.test.TestUtils;
import eu.europa.esig.dss.test.gen.CertificateService;
import eu.europa.esig.dss.test.mock.MockPrivateKeyEntry;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.junit.Ignore;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;

@Ignore
public class XAdESBaselineBTest extends AbstractRequirementChecks {

	@Override
	protected DSSDocument getSignedDocument() throws Exception {
		DSSDocument documentToSign = new FileDocument(new File("src/test/resources/sample.xml"));

		CertificateService certificateService = new CertificateService();
		MockPrivateKeyEntry privateKeyEntry = certificateService.generateCertificateChain(SignatureAlgorithm.RSA_SHA256);

		XAdESSignatureParameters signatureParameters = new XAdESSignatureParameters();
		signatureParameters.setSigningCertificate(privateKeyEntry.getCertificate());
		signatureParameters.setCertificateChain(privateKeyEntry.getCertificateChain());
		signatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
		signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);

		CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
		XAdESService service = new XAdESService(certificateVerifier);

		ToBeSigned dataToSign = service.getDataToSign(documentToSign, signatureParameters);
		SignatureValue signature = TestUtils.sign(SignatureAlgorithm.RSA_SHA256, privateKeyEntry, dataToSign);
		return service.signDocument(documentToSign, signatureParameters, signature);
	}

	@Override
	public void checkSignatureTimeStampPresent() throws XPathExpressionException {
		// No timestamp in Baseline Profile B
	}

	@Override
	public void checkArchiveTimeStampPresent() throws XPathExpressionException {
		// No ArchiveTimestamp in Baseline Profile B
	}

}
