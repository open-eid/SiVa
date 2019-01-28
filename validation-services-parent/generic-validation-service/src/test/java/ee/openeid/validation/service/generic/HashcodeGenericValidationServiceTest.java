package ee.openeid.validation.service.generic;


import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SignatureScope;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.validation.service.generic.configuration.GenericSignaturePolicyProperties;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest(classes = {PDFValidationServiceTest.TestConfiguration.class})
@RunWith(SpringRunner.class)
public class HashcodeGenericValidationServiceTest {


    private HashcodeGenericValidationService validationService;
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    @Autowired
    private
    TrustedListsCertificateSource trustedListsCertificateSource;
    @Autowired
    private
    GenericSignaturePolicyProperties policySettings;

    @Before
    public void setUp() {
        validationService = new HashcodeGenericValidationService();
        validationService.setTrustedListsCertificateSource(trustedListsCertificateSource);

        signaturePolicyService = new ConstraintLoadingSignaturePolicyService(policySettings);
        validationService.setSignaturePolicyService(signaturePolicyService);
        validationService.setReportConfigurationProperties(new ReportConfigurationProperties(true));
    }

    @Test
    public void validHashcodeRequest() throws Exception {
        Reports response = validationService.validate(getValidationDocumentSingletonList());
        SignatureScope signatureScope = response.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureScopes().get(0);
        Assert.assertEquals("LvhnsrgBZBK9kTQ8asbPtcsjuEhBo9s3QDdCcIxlMmo=", signatureScope.getHash());
        Assert.assertEquals("SHA256", signatureScope.getHashAlgo());
        Assert.assertEquals("test.pdf", signatureScope.getName());
        Assert.assertEquals((Integer) 1, response.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals(1L, response.getSimpleReport().getValidationConclusion().getSignatures().size());
    }

    @Test
    public void validMultipleSignatures() throws Exception {
        List<ValidationDocument> validationDocuments = getValidationDocumentSingletonList();
        validationDocuments.addAll(getValidationDocumentSingletonList());
        Reports response = validationService.validate(validationDocuments);
        Assert.assertEquals((Integer) 2, response.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals((Integer) 2, response.getSimpleReport().getValidationConclusion().getSignaturesCount());
        Assert.assertEquals(2L, response.getSimpleReport().getValidationConclusion().getSignatures().size());
    }

    @Test
    public void validDataFromSignatureFile() throws Exception {
        List<ValidationDocument> validationDocuments = getValidationDocumentSingletonList();
        validationDocuments.get(0).setDatafiles(null);
        Reports response = validationService.validate(validationDocuments);
        SignatureScope signatureScope = response.getSimpleReport().getValidationConclusion().getSignatures().get(0).getSignatureScopes().get(0);
        Assert.assertEquals("LvhnsrgBZBK9kTQ8asbPtcsjuEhBo9s3QDdCcIxlMmo=", signatureScope.getHash());
        Assert.assertEquals("SHA256", signatureScope.getHashAlgo());
        Assert.assertEquals("test.pdf", signatureScope.getName());
    }

    private List<ValidationDocument> getValidationDocumentSingletonList() throws URISyntaxException, IOException {
        List<ValidationDocument> validationDocuments = new ArrayList<>();
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setDatafiles(Collections.singletonList(getDataFile()));
        Path documentPath = Paths.get(getClass().getClassLoader().getResource("test-files/signatures.xml").toURI());
        validationDocument.setBytes(Files.readAllBytes(documentPath));
        validationDocument.setSignaturePolicy("POLv3");
        validationDocuments.add(validationDocument);
        return validationDocuments;
    }

    private Datafile getDataFile() {
        Datafile datafile = new Datafile();
        datafile.setFilename("test.pdf");
        datafile.setHash("LvhnsrgBZBK9kTQ8asbPtcsjuEhBo9s3QDdCcIxlMmo=");
        datafile.setHashAlgo("SHA256");
        return datafile;
    }
}
