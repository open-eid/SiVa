package ee.openeid.validation.service.timemark.util;

import ee.openeid.siva.validation.document.report.SignatureScope;
import lombok.experimental.UtilityClass;
import org.digidoc4j.impl.asic.AsicSignature;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SignatureScopeParser {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SignatureScopeParser.class);
  private static final String FULL_SIGNATURE_SCOPE = "FullSignatureScope";
  private static final String FULL_DOCUMENT = "Digest of the document content";

  public static List<SignatureScope> getAsicSignatureScopes(AsicSignature signature, List<String> dataFilenames) {
    return signature.getOrigin().getReferences()
      .stream()
      .map(r -> decodeUriIfPossible(r.getURI()))
      .filter(dataFilenames::contains) //filters out Signed Properties
      .map(SignatureScopeParser::createFullSignatureScopeForDataFile)
      .collect(Collectors.toList());
  }

  private static String decodeUriIfPossible(String uri) {
    try {
      return URLDecoder.decode(uri, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOGGER.warn("datafile " + uri + " has unsupported encoding", e);
      return uri;
    }
  }

  public static SignatureScope createFullSignatureScopeForDataFile(String filename) {
    SignatureScope signatureScope = new SignatureScope();
    signatureScope.setName(filename);
    signatureScope.setScope(FULL_SIGNATURE_SCOPE);
    signatureScope.setContent(FULL_DOCUMENT);
    return signatureScope;
  }
}
