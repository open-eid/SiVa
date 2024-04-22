package ee.openeid.validation.service.timemark.util;

import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.util.CertUtil;
import lombok.experimental.UtilityClass;
import org.digidoc4j.Signature;
import org.digidoc4j.X509Cert;
import org.digidoc4j.exceptions.CertificateNotFoundException;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class SignatureCertificateParser {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SignatureCertificateParser.class);

  public static List<Certificate> getCertificateList(Signature signature) {
    List<Certificate> certificateList = new ArrayList<>();

    X509Cert ocspCertificate;
    try {
      ocspCertificate = signature.getOCSPCertificate();
    } catch (CertificateNotFoundException e) {
      LOGGER.warn("Failed to acquire OCSP certificate from signature", e);
      ocspCertificate = null;
    }
    if (ocspCertificate != null) {
      X509Certificate x509Certificate = ocspCertificate.getX509Certificate();
      certificateList.add(getCertificate(x509Certificate, CertificateType.REVOCATION));
    }

    X509Cert signingCertificate = signature.getSigningCertificate();
    if (signingCertificate != null) {
      X509Certificate x509Certificate = signingCertificate.getX509Certificate();
      certificateList.add(getCertificate(x509Certificate, CertificateType.SIGNING));
    }

    return certificateList;
  }

  public static Certificate getCertificate(X509Certificate x509Certificate, CertificateType type) {
    Certificate certificate = new Certificate();
    certificate.setContent(CertUtil.encodeCertificateToBase64(x509Certificate));
    certificate.setCommonName(CertUtil.getCommonName(x509Certificate));
    certificate.setType(type);
    return certificate;
  }
}
