package ee.openeid.validation.service.generic.validator.report;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.validation.SignatureQualification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GenericValidationReportBuilderTest {
    private GenericValidationReportBuilder builder;

    @Before
    public void setUp() {
        builder = getReportBuilder();
        ConstraintDefinedPolicy constraintDefinedPolicy = new ConstraintDefinedPolicy();
        constraintDefinedPolicy.setName("POLv4");
        builder.setValidationPolicy(constraintDefinedPolicy);
    }

    @Test
    public void indicationNotChangingQesSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.QES);
        builder.processSignatureIndications(validationConclusion);
        assertTotalPassed(validationConclusion);
    }

    @Test
    public void indicationNotChangingQesigSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.QESIG);
        builder.processSignatureIndications(validationConclusion);
        assertTotalPassed(validationConclusion);
    }

    @Test
    public void indicationNotChangingQesealSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.QESEAL);
        builder.processSignatureIndications(validationConclusion);
        assertTotalPassed(validationConclusion);
    }

    @Test
    public void indicationNotChangingAdesealQsSignatureLevel() {
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.ADESEAL_QC);
        builder.processSignatureIndications(validationConclusion);
        assertTotalPassed(validationConclusion);
    }

    @Test
    public void indicationToTotalFailedAdesQsSignatureLevel(){
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.ADES_QC);
        builder.processSignatureIndications(validationConclusion);
        assertTotalFailed(validationConclusion);
    }

    @Test
    public void indicationToTotalFailedAdesSignatureLevel(){
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.ADES);
        builder.processSignatureIndications(validationConclusion);
        assertTotalFailed(validationConclusion);
    }

    @Test
    public void indicationToPassedWithWarningFailedAdesigQsSignatureLevel(){
        ValidationConclusion validationConclusion = getDefaultValidationConclusion(SignatureQualification.ADESIG_QC);
        builder.processSignatureIndications(validationConclusion);
        Assert.assertEquals("TOTAL-PASSED", validationConclusion.getSignatures().get(0).getIndication());
        Assert.assertEquals(1, validationConclusion.getSignatures().get(0).getWarnings().size());
        Assert.assertTrue(validationConclusion.getSignatures().get(0).getErrors().isEmpty());
        Assert.assertEquals("The signature is not in the Qualified Electronic Signature level", validationConclusion.getSignatures().get(0).getWarnings().get(0).getContent());
    }

    private void assertTotalPassed(ValidationConclusion validationConclusion){
        SignatureValidationData signatureValidationData = validationConclusion.getSignatures().get(0);
        Assert.assertEquals("TOTAL-PASSED", signatureValidationData.getIndication());
        Assert.assertTrue(signatureValidationData.getWarnings().isEmpty());
        Assert.assertTrue(signatureValidationData.getErrors().isEmpty());
    }

    private void assertTotalFailed(ValidationConclusion validationConclusion){
        Assert.assertEquals("TOTAL-FAILED", validationConclusion.getSignatures().get(0).getIndication());
        Assert.assertTrue(validationConclusion.getSignatures().get(0).getWarnings().isEmpty());
        List<Error> errors = validationConclusion.getSignatures().get(0).getErrors();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Signature/seal level do not meet the minimal level required by applied policy", errors.get(0).getContent());
    }
    private GenericValidationReportBuilder getReportBuilder() {
        return new GenericValidationReportBuilder(null, null, null, null, null);
    }

    private ValidationConclusion getDefaultValidationConclusion(SignatureQualification signatureQualification) {
        ValidationConclusion validationConclusion = new ValidationConclusion();
        Policy policy = new Policy();
        policy.setPolicyName("POLv4");
        validationConclusion.setPolicy(policy);
        validationConclusion.setSignatures(getSignatures(signatureQualification));
        return validationConclusion;
    }

    private List<SignatureValidationData> getSignatures(SignatureQualification signatureQualification) {
        List<SignatureValidationData> signatures = new ArrayList<>();
        SignatureValidationData totalPassedSignature = new SignatureValidationData();
        totalPassedSignature.setIndication(SignatureValidationData.Indication.TOTAL_PASSED);
        totalPassedSignature.setSignatureLevel(signatureQualification.name());
        totalPassedSignature.setErrors(new ArrayList<>());
        totalPassedSignature.setWarnings(new ArrayList<>());
        signatures.add(totalPassedSignature);
        return signatures;

    }

}
