package eu.europa.esig.dss.xades.validation;

import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.report.DiagnosticData;
import eu.europa.esig.dss.validation.report.Reports;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Unit test added to fix issue : https://esig-dss.atlassian.net/browse/DSS-678
 */
@Ignore("Turned off by SK PDF Validator")
public class DTDInjectionTest {

	@Test
	public void test() {
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(new FileDocument(new File("src/test/resources/validation/xades-with-dtd-injection.xml")));
		validator.setCertificateVerifier(new CommonCertificateVerifier());

		Reports reports = validator.validateDocument();

		DiagnosticData diagnosticData = reports.getDiagnosticData();
		String value = diagnosticData.getValue("/DiagnosticData/Signature[@Id='Signature']/ClaimedRoles/ClaimedRole/text()");
		assertTrue(StringUtils.equals("&test1;", value) || StringUtils.isEmpty(value));
	}

}
