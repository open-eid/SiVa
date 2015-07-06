package eu.europa.esig.dss.validation.policy;

import eu.europa.esig.dss.XmlDom;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EtsiValidationPolicy.class)
public class EtsiValidationPolicyTest {
    private EtsiValidationPolicy etsiValidationPolicy;

    @Before
    public void setUp() throws Exception {
        PowerMockito.suppress(PowerMockito.constructor(ValidationPolicy.class));
        Document document = mock(Document.class);
        etsiValidationPolicy = spy(new EtsiValidationPolicy(document));
    }

    @Test
    public void testSignatureFormatConstraint() throws Exception {
        List<String> supportedTypes = new ArrayList<String>();
        supportedTypes.add("PAdES_BASELINE_LT");
        supportedTypes.add("PAdES_BASELINE_LTA");
        mockSupportedSignatureList(supportedTypes);

        String constraintLevel = "FAIL";
        getConstraintLevel(constraintLevel);
        mockElementValueList();

        SignatureFormatConstraint currentSignatureLevel = etsiValidationPolicy.getSignatureFormatConstraint();

        assertEquals(2, currentSignatureLevel.getIdentifiers().size());
        assertEquals("[PAdES_BASELINE_LT, PAdES_BASELINE_LTA]", currentSignatureLevel.getExpectedValue());

        parentMethodVerification();
        verify(etsiValidationPolicy).getElements(anyString());
    }

    @Test
    public void testOnlyOneSignatureValue() throws Exception {
        List<String> supportedTypes = new ArrayList<String>();
        supportedTypes.add("PAdES_BASELINE_LT");
        mockSupportedSignatureList(supportedTypes);

        String constraintLevel = "FAIL";
        getConstraintLevel(constraintLevel);
        mockElementValueList();

        SignatureFormatConstraint currentSignatureLevel = etsiValidationPolicy.getSignatureFormatConstraint();

        assertEquals(1, currentSignatureLevel.getIdentifiers().size());
        assertEquals("[PAdES_BASELINE_LT]", currentSignatureLevel.getExpectedValue());

        parentMethodVerification();
        verify(etsiValidationPolicy).getElements(anyString());
    }

    private static void mockSupportedSignatureList(List<String> supportedTypes) {
        mockStatic(XmlDom.class);
        PowerMockito.when(XmlDom.convertToStringList(anyListOf(XmlDom.class))).thenReturn(supportedTypes);
    }

    @Test
    public void testSignatureConstraintValueNotSet() throws Exception {
        String elementContentValue = "";
        getConstraintLevel(elementContentValue);

        assertEquals(null, etsiValidationPolicy.getSignatureFormatConstraint());
        parentMethodVerification();
    }

    private void mockElementValueList() {
        List<XmlDom> xmlDomList = new ArrayList<XmlDom>();
        // We do not need any value returned form this method
        doReturn(xmlDomList).when((ValidationPolicy) etsiValidationPolicy).getElements(anyString());
    }

    private void getConstraintLevel(String elementContentValue) {
        doReturn(elementContentValue)
                .when((ValidationPolicy) etsiValidationPolicy)
                .getValue(anyString());
    }

    private void parentMethodVerification() {
        verify(etsiValidationPolicy, times(1)).getValue(anyString());
    }

}