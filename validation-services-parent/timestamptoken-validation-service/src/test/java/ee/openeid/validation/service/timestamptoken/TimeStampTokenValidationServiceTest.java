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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.timestamptoken.configuration.TimeStampTokenSignaturePolicyProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TimeStampTokenValidationServiceTest {

    private static final String TEST_FILES_LOCATION = "test-files/";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TimeStampTokenValidationService validationService;
    private TimeStampTokenSignaturePolicyProperties policyProperties = new TimeStampTokenSignaturePolicyProperties();


    @Before
    public void setUp() {
        validationService = new TimeStampTokenValidationService();
        policyProperties.initPolicySettings();
        SignaturePolicyService<ValidationPolicy> signaturePolicyService = new SignaturePolicyService<>(policyProperties);
        validationService.setSignaturePolicyService(signaturePolicyService);
        ReportConfigurationProperties reportConfigurationProperties = new ReportConfigurationProperties(true);
        validationService.setReportConfigurationProperties(reportConfigurationProperties);
    }

    @Test
    public void validTimeStampToken() throws Exception {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-ddoc.asics")).getSimpleReport();
        TimeStampTokenValidationData validationData = simpleReport.getValidationConclusion().getTimeStampTokens().get(0);
        Assert.assertEquals(TimeStampTokenValidationData.Indication.TOTAL_PASSED, validationData.getIndication());
        Assert.assertEquals("SK TIMESTAMPING AUTHORITY", validationData.getSignedBy());
        Assert.assertNull(validationData.getError());

    }

    @Test
    public void multipleDataFile() throws Exception {
        expectedException.expect(DocumentRequirementsException.class);
        validationService.validateDocument(buildValidationDocument("timestamptoken-two-data-files.asics"));
    }

    @Test
    public void notValidTstFile() throws Exception {
        expectedException.expect(MalformedDocumentException.class);
        validationService.validateDocument(buildValidationDocument("timestamptoken-invalid.asics"));
    }

    @Test
    public void dataFiledChanged() throws Exception {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-datafile-changed.asics")).getSimpleReport();
        Assert.assertEquals("Signature not intact", simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getError().get(0).getContent());
    }

    @Test
    public void signatureNotIntact() throws Exception {
        SimpleReport simpleReport = validationService.validateDocument(buildValidationDocument("timestamptoken-signature-modified.asics")).getSimpleReport();
        Assert.assertEquals("Signature not intact", simpleReport.getValidationConclusion().getTimeStampTokens().get(0).getError().get(0).getContent());
    }


    private ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }

}
