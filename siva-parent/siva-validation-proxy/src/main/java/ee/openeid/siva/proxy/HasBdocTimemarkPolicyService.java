package ee.openeid.siva.proxy;

import ee.openeid.siva.validation.document.ValidationDocument;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.xml.common.definition.DSSNamespace;
import eu.europa.esig.dss.xml.utils.DomUtils;
import eu.europa.esig.xades.definition.xades132.XAdES132Element;
import org.digidoc4j.dss.xades.BDocTmSupport;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.util.Optional;

import static eu.europa.esig.dss.xml.common.definition.AbstractPath.allFromCurrentPosition;

@Service
public class HasBdocTimemarkPolicyService {
  boolean hasBdocTimemarkPolicy(ValidationDocument validationDocument) {
    return extractSigPolicyIdElement(validationDocument)
      .map(this::extractSigPolicyIdValue)
      .map(this::matchesBdocTimemarkPolicyId)
      .orElse(false);
  }

  private Optional<Element> extractSigPolicyIdElement(ValidationDocument validationDocument) {
    DomUtils.registerNamespace(new DSSNamespace("http://uri.etsi.org/01903/v1.3.2#", "xades132"));
    return Optional.of(validationDocument.getBytes())
      .filter(DomUtils::startsWithXmlPreamble)
      .map(DomUtils::buildDOM)
      .map(dom -> DomUtils.getElement(dom, allFromCurrentPosition(XAdES132Element.SIG_POLICY_ID)));
  }

  private String extractSigPolicyIdValue(Element sigPolicyId) {
    return Utils.trim(DomUtils.getValue(sigPolicyId, allFromCurrentPosition(XAdES132Element.IDENTIFIER)));
  }

  private boolean matchesBdocTimemarkPolicyId(String sigPolicyIdValue) {
    return Utils.areStringsEqualIgnoreCase(BDocTmSupport.BDOC_TM_POLICY_ID, sigPolicyIdValue);
  }
}
