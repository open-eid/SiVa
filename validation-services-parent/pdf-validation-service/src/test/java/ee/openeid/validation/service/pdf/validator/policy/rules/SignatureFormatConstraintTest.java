package ee.openeid.validation.service.pdf.validator.policy.rules;

import ee.openeid.validation.service.pdf.validator.policy.EstonianConstraint;
import eu.europa.esig.dss.validation.policy.Constraint;
import eu.europa.esig.dss.validation.policy.XmlNode;
import eu.europa.esig.dss.validation.report.Conclusion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static eu.europa.esig.dss.validation.policy.Constraint.Level;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignatureFormatConstraintTest {

    @Test
    public void signatureLevelCheckGivenIgnoreReturnsTrue() throws Exception {
        final SignatureFormatConstraint constraint = getSignatureFormatConstraint(Level.IGNORE);
        getMockNode(constraint);

        assertTrue(constraint.check());
        verify((Constraint)constraint).ignore();
    }

    @Test
    public void signatureLevelCheckGivenInfoReturnsTrue() throws Exception {
        SignatureFormatConstraint constraint = getSignatureFormatConstraint(Level.INFORM);
        getMockNode(constraint);

        assertTrue(constraint.check());
        verify((Constraint)constraint).ignore();
        verify((Constraint)constraint).inform();
    }

    @Test
    public void signatureLevelCheckGivenWarnReturnsTrue() throws Exception {
        SignatureFormatConstraint constraint = getSignatureFormatConstraint(Level.WARN);
        constraint.setCurrentSignatureLevel("PAdES_BASELINE_LT");
        getMockNode(constraint);

        doReturn(new Conclusion()).when((EstonianConstraint)constraint).getConclusion();
        doReturn(new ArrayList<String>()).when((EstonianConstraint)constraint).getIdentifiers();

        assertTrue(constraint.check());
        verify((Constraint)constraint).warn();
    }

    @Test
    public void signatureLevelCheckGivenFailReturnsFalse() throws Exception {
        SignatureFormatConstraint constraint = getSignatureFormatConstraint(Level.FAIL);
        constraint.setCurrentSignatureLevel("PAdES_BASELINE_LT");
        getMockNode(constraint);
//        doReturn(Arrays.asList("PAdES_BASELINE_LT")).when((EstonianConstraint)constraint).getIdentifiers();

        doReturn(new Conclusion()).when((EstonianConstraint)constraint).getConclusion();
        doReturn(new ArrayList<String>()).when((EstonianConstraint)constraint).getIdentifiers();

        assertFalse(constraint.check());
    }

    @Test
    public void signatureLevelCheckGivenOkReturnsTrue() throws Exception {
        SignatureFormatConstraint constraint = getSignatureFormatConstraint(Level.FAIL);
        constraint.setCurrentSignatureLevel("PAdES_BASELINE_LT");
        getMockNode(constraint);
        doReturn(Arrays.asList("PAdES_BASELINE_LT")).when((EstonianConstraint)constraint).getIdentifiers();

        assertTrue(constraint.check());
    }

    @Test
    public void checkValueOfSignatureLevel() throws Exception {
        SignatureFormatConstraint constraint = getSignatureFormatConstraint(Level.IGNORE);
        constraint.setCurrentSignatureLevel("PAdES_BASELINE_LT");
        getMockNode(constraint);

        assertEquals("PAdES_BASELINE_LT", constraint.getCurrentSignatureLevel());
    }

    private static SignatureFormatConstraint getSignatureFormatConstraint(Level testLevel) {
        return spy(new SignatureFormatConstraint(getLevel(testLevel)));
    }

    private static XmlNode getMockNode(EstonianConstraint constraint) {
        return doReturn(new XmlNode("random")).when(constraint).getNode();
    }

    private static String getLevel(final Level level) {
        return level.name().toLowerCase();
    }
}