package ee.openeid.validation.service.generic;

import java.util.Base64;
import java.util.stream.Collectors;

import org.digidoc4j.Configuration;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerOpener;
import org.digidoc4j.DataFile;
import org.digidoc4j.DigestAlgorithm;
import org.digidoc4j.Signature;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestDocument;
import eu.europa.esig.dss.InMemoryDocument;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class DigestValidationServiceTest {

    @Autowired
    private DigestValidationService digestValidationService;

    @Test
    public void testReportIsNotNull() throws Exception {
        Container container = ContainerOpener.open(this.getClass().getResourceAsStream("/test-files/test.asice"), Configuration.of(Configuration.Mode.TEST));
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName("test.asice");
        validationDocument.setSignaturePolicy("");
        Reports reports = this.digestValidationService.validateDocuments(validationDocument, container.getDataFiles().stream().map(f -> this.createDigestDocument(f))
            .collect(Collectors.toList()), container.getSignatures().stream().map(s -> this.createDSSDocument(s)).findFirst().get());
        Assert.assertNotNull("No reports", reports);
    }

    private DSSDocument createDSSDocument(Signature signature) {
        return new InMemoryDocument(signature.getAdESSignature());
    }

    private DigestDocument createDigestDocument(DataFile file) {
        DigestDocument digestDocument = new DigestDocument();
        digestDocument.setName(file.getName());
        digestDocument.addDigest(eu.europa.esig.dss.DigestAlgorithm.valueOf("SHA256"), Base64.getEncoder()
            .encodeToString(file.calculateDigest(DigestAlgorithm.SHA256)));
        return digestDocument;
    }

}
