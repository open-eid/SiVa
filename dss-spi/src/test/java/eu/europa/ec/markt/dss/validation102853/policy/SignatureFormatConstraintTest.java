package eu.europa.ec.markt.dss.validation102853.policy;

import eu.europa.ec.markt.dss.exception.DSSException;
import eu.europa.ec.markt.dss.validation102853.report.Conclusion;
import eu.europa.ec.markt.dss.validation102853.rules.MessageTag;
import eu.europa.ec.markt.dss.validation102853.xml.XmlNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SignatureFormatConstraintTest {
    private SignatureFormatConstraint signatureFormatConstraint;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNoSignatureFormatLevelSet() throws Exception {
        expectedException.expect(DSSException.class);
        signatureFormatConstraint = new SignatureFormatConstraint("NO_VALUE");
        signatureFormatConstraint.check();
    }

    @Test
    public void testSignatureFormatConstraintSetToFail() throws Exception {
        createConstraint(createIdentifiers(), "FAIL");
        assertFalse(signatureFormatConstraint.check());
    }

    @Test
    public void testSignatureFormatConstraintSetToWarn() throws Exception {
        createConstraint(createIdentifiers(), "WARN");
        assertTrue(signatureFormatConstraint.check());
    }

    @Test
    public void testSignatureFormatConstraintCurrentLevelIsSet() throws Exception {
        createConstraint(createIdentifiers(), "FAIL");
        signatureFormatConstraint.setCurrentSignatureLevel("PAdES_BASELINE_LT");
        assertTrue(signatureFormatConstraint.check());
    }

    @Test
    public void testSignatureFormatConstraintCurrentLevelSetToInvalid() throws Exception {
        createConstraint(createIdentifiers(), "FAIL");
        signatureFormatConstraint.setCurrentSignatureLevel("RANDOM_LEVEL_VALUE");
        assertFalse(signatureFormatConstraint.check());
    }

    private void createConstraint(List<String> identifiers, String level) {
        signatureFormatConstraint = new SignatureFormatConstraint(level);
        signatureFormatConstraint.setIdentifiers(identifiers);
        signatureFormatConstraint.create(new XmlNode("AcceptableSignatureFormats"), MessageTag.BBB_VCI_ISFC);
        signatureFormatConstraint.setConclusionReceiver(new Conclusion());

        signatureFormatConstraint.setExpectedValue(identifiers.toString());
    }

    private static List<String> createIdentifiers() {
        return Arrays.asList("PAdES_BASELINE_LT", "PAdES_BASELINE_LTA");
    }
}