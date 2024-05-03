package ee.openeid.validation.service.timemark.util;

import ee.openeid.siva.validation.document.report.SubjectDistinguishedName;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import ee.openeid.siva.validation.util.DistinguishedNameUtil;
import ee.openeid.siva.validation.util.SubjectDNParser;
import lombok.experimental.UtilityClass;
import org.digidoc4j.X509Cert;

import java.util.Optional;

import static org.digidoc4j.X509Cert.SubjectName.CN;

@UtilityClass
public class SigningCertificateParser {
  public static SubjectDistinguishedName parseSubjectDistinguishedName(X509Cert signingCertificate) {
    return SubjectDistinguishedName.builder()
      .serialNumber(getSubjectName(signingCertificate, X509Cert.SubjectName.SERIALNUMBER))
      .commonName(getSubjectName(signingCertificate, X509Cert.SubjectName.CN))
      .givenName(getSubjectName(signingCertificate, X509Cert.SubjectName.GIVENNAME))
      .surname(getSubjectName(signingCertificate, X509Cert.SubjectName.SURNAME))
      .build();
  }

  private static String getSubjectName(X509Cert signingCertificate, X509Cert.SubjectName subjectName) {
    return Optional.ofNullable(signingCertificate)
      .map(cert -> cert.getSubjectName(subjectName))
      .map(SubjectDNParser::removeQuotes)
      .orElse(null);
  }

  public static String parseSignedBy(X509Cert signingCertificate) {
    return Optional.ofNullable(signingCertificate)
      .flatMap(certificate -> Optional
        .ofNullable(certificate.getX509Certificate())
        .map(DistinguishedNameUtil::getSubjectSurnameAndGivenNameAndSerialNumber)
        .or(() -> Optional
          .ofNullable(certificate.getSubjectName(CN))
          .map(SubjectDNParser::removeQuotes))
      )
      .orElseGet(ReportBuilderUtils::valueNotPresent);
  }
}
