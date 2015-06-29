package eu.europa.ec.markt.dss.validation102853.processes.subprocesses;

import eu.europa.ec.markt.dss.validation102853.policy.ProcessParameters;
import eu.europa.ec.markt.dss.validation102853.policy.SignatureFormatConstraint;
import eu.europa.ec.markt.dss.validation102853.policy.SignaturePolicyConstraint;
import eu.europa.ec.markt.dss.validation102853.policy.ValidationPolicy;
import eu.europa.ec.markt.dss.validation102853.report.Conclusion;
import eu.europa.ec.markt.dss.validation102853.report.DiagnosticData;
import eu.europa.ec.markt.dss.validation102853.rules.Indication;
import eu.europa.ec.markt.dss.validation102853.xml.XmlDom;
import eu.europa.ec.markt.dss.validation102853.xml.XmlNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ValidationContextInitialisation.class)
public class ValidationContextInitialisationTest {
    private ValidationContextInitialisation validationContextInitialisation;

    @Before
    public void setUp() throws Exception {
        validationContextInitialisation = spy(new ValidationContextInitialisation());
    }

    @Test
    public void testValidateSignatureFormatPolicyConstraintNotSet() throws Exception {
        ValidationPolicy validationPolicy = mock(ValidationPolicy.class);
        ProcessParameters params = prepareProcessParameters(validationPolicy, null);
        Conclusion conclusion = validationContextInitialisation.run(params, new XmlNode("RandomNode"));

        Assert.assertEquals(Indication.VALID, conclusion.getIndication());
    }

    @Test
    public void testSignatureFormatPolicyConstraintSetToLT() throws Exception {
        ValidationPolicy validationPolicy = getValidationPolicy();
        ProcessParameters params = prepareProcessParameters(validationPolicy,"PAdES_BASELINE_LT");

        Conclusion conclusion = validationContextInitialisation.run(params, new XmlNode("RandomNode"));
        Assert.assertEquals(Indication.VALID, conclusion.getIndication());
    }

    @Test
    public void testSignatureFormatPolicyConstraintSetToLTA() throws Exception {
        ValidationPolicy validationPolicy = getValidationPolicy();
        ProcessParameters params = prepareProcessParameters(validationPolicy,"PAdES_BASELINE_LTA");

        Conclusion conclusion = validationContextInitialisation.run(params, new XmlNode("RandomNode"));
        Assert.assertEquals(Indication.VALID, conclusion.getIndication());
    }

    @Test
    public void testSignatureFormatPolicyConstraintSetToInvalidPolicy() throws Exception {
        ValidationPolicy validationPolicy = getValidationPolicy();
        ProcessParameters params = prepareProcessParameters(validationPolicy,"INVALID_POLICY");

        Conclusion conclusion = validationContextInitialisation.run(params, new XmlNode("RandomNode"));
        Assert.assertEquals(Indication.INVALID, conclusion.getIndication());
    }

    @Test
    public void testSignatureFormatPolicyConstraintSetToInvalidFormatB() throws Exception {
        ValidationPolicy validationPolicy = getValidationPolicy();
        ProcessParameters params = prepareProcessParameters(validationPolicy, "PAdES_BASELINE_B");

        Conclusion conclusion = validationContextInitialisation.run(params, new XmlNode("RandomNode"));
        Assert.assertEquals(Indication.INVALID, conclusion.getIndication());
    }

    @Test
    public void testSignatureFormatPolicyConstraintSetToInvalidFormatT() throws Exception {
        ValidationPolicy validationPolicy = getValidationPolicy();
        ProcessParameters params = prepareProcessParameters(validationPolicy, "PAdES_BASELINE_T");

        Conclusion conclusion = validationContextInitialisation.run(params, new XmlNode("RandomNode"));
        Assert.assertEquals(Indication.INVALID, conclusion.getIndication());
    }

    private static ValidationPolicy getValidationPolicy() {
        ValidationPolicy validationPolicy = mock(ValidationPolicy.class);

        SignaturePolicyConstraint signaturePolicyConstraint = getSignaturePolicyConstraint("IGNORE");
        when(validationPolicy.getSignaturePolicyConstraint()).thenReturn(signaturePolicyConstraint);

        SignatureFormatConstraint signatureFormatConstraint = new SignatureFormatConstraint("FAIL");
        signatureFormatConstraint.setIdentifiers(getValidSignatureFormatConstraintIdentifiers());
        when(validationPolicy.getSignatureFormatConstraint()).thenReturn(signatureFormatConstraint);

        return validationPolicy;
    }

    private static List<String> getValidSignatureFormatConstraintIdentifiers() {
        return Arrays.asList("PAdES_BASELINE_LT", "PAdES_BASELINE_LTA");
    }

    private static SignaturePolicyConstraint getSignaturePolicyConstraint(String errorLevel) {
        SignaturePolicyConstraint signaturePolicyConstraint = new SignaturePolicyConstraint(errorLevel);
        signaturePolicyConstraint.setIdentifiers(Collections.singletonList("NO_POLICY"));
        return signaturePolicyConstraint;
    }

    private static ProcessParameters prepareProcessParameters(ValidationPolicy validationPolicy, String signatureFormatValue) {
        Document document = createDomDocument();

        ProcessParameters params = new ProcessParameters();
        params.setDiagnosticData(new DiagnosticData(document));
        XmlDom signatureContext = mock(XmlDom.class);
        when(signatureContext.getValue("./SignatureFormat/text()")).thenReturn(signatureFormatValue);

        params.setSignatureContext(signatureContext);
        params.setCurrentValidationPolicy(validationPolicy);
        return params;
    }

    private static Document createDomDocument() {
        Document document = mock(Document.class);
        Element rootElement = mock(Element.class);
        when(rootElement.getNamespaceURI()).thenReturn("http://google.com");
        when(document.getDocumentElement()).thenReturn(rootElement);

        return document;
    }
}