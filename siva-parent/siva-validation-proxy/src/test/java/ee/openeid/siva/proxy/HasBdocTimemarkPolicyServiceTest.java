package ee.openeid.siva.proxy;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HasBdocTimemarkPolicyServiceTest {
  private final HasBdocTimemarkPolicyService hasBdocTimemarkPolicyService = new HasBdocTimemarkPolicyService();

  @Test
  void hasBdocTimemarkPolicy_whenInputIsNotXmlFile_shouldReturnFalse() {
    assertFalse(
      hasBdocTimemarkPolicyService.hasBdocTimemarkPolicy(createValidationDocument("timestamptoken-ddoc.asics"))
    );
  }

  @Test
  void hasBdocTimemarkPolicy_whenSignatureDoesNotHaveBdocTimemark_shouldReturnFalse() {
    assertFalse(
      hasBdocTimemarkPolicyService.hasBdocTimemarkPolicy(createValidationDocument("no_timemark_signature.xml"))
    );
  }

  @Test
  void hasBdocTimemarkPolicy_whenSignatureHasBdocTimemark_shouldReturnTrue() {
    assertTrue(
      hasBdocTimemarkPolicyService.hasBdocTimemarkPolicy(createValidationDocument("timemark_signature.xml"))
    );
  }

  @SneakyThrows
  private ValidationDocument createValidationDocument(String file) {
    return DummyValidationDocumentBuilder
      .aValidationDocument()
      .withDocument("test-files/" + file)
      .withName(file)
      .build();
  }
}
