package ee.openeid.validation.service.timemark.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.ContainerValidationResult;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;

public class DDOCContainerValidationReportBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_DDOC_WITH_2_SIGNATURES = "ddoc_valid_2_signatures.ddoc";

    @Test
    public void validDDOCReturnsSuccessfulResult() {
        ValidationDocument validationDocument = validationDocument();
        Container container = ContainerBuilder.aContainer()
                  .fromStream(new ByteArrayInputStream(validationDocument.getBytes()))
                  .build();

        ContainerValidationResult validationResult = container.validate();
        Reports reports = new DDOCContainerValidationReportBuilder(container, validationDocument, new ValidationPolicy(), validationResult.getErrors(), true).build();

        ValidationConclusion validationConclusion = reports.getSimpleReport().getValidationConclusion();
        assertSame(validationConclusion.getSignatures().size(), validationConclusion.getValidSignaturesCount());
    }

    @Test
    public void ifThereAreContainerErrorsThatAreNotPresentUnderSignaturesErrorsThenExceptionIsThrown() {
        expectedException.expect(DigiDoc4JException.class);
        expectedException.expectMessage("Container has validation error(s)");

        ValidationDocument validationDocument = validationDocument();
        Container container = ContainerBuilder.aContainer()
                  .fromStream(new ByteArrayInputStream(validationDocument.getBytes()))
                  .build();

        List<DigiDoc4JException> validationErrors = Collections.singletonList(new DigiDoc4JException("Container level error"));
        new DDOCContainerValidationReportBuilder(container, validationDocument, new ValidationPolicy(), validationErrors, true).build();
    }

    private ValidationDocument validationDocument() {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument("test-files/" + VALID_DDOC_WITH_2_SIGNATURES)
                .withName(VALID_DDOC_WITH_2_SIGNATURES)
                .build();
    }
}
