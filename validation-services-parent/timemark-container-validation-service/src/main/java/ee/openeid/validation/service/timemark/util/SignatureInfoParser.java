package ee.openeid.validation.service.timemark.util;

import ee.openeid.siva.validation.document.report.Info;
import ee.openeid.siva.validation.document.report.SignatureProductionPlace;
import ee.openeid.siva.validation.document.report.SignerRole;
import ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.TimestampType;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureProfile;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.digidoc4j.impl.asic.asice.AsicESignature;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class SignatureInfoParser {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SignatureInfoParser.class);

  public static Info getInfo(Signature signature) {
    Info info = new Info();
    info.setBestSignatureTime(getBestSignatureTime(signature));
    if (signature.getProfile() == SignatureProfile.LT) {
      info.setTimestampCreationTime(getTimestampTime(signature));
    }
    info.setOcspResponseCreationTime(getOcspTime(signature));
    info.setTimeAssertionMessageImprint(getTimeAssertionMessageImprint(signature));
    info.setSignerRole(getSignerRole(signature));
    info.setSignatureProductionPlace(getSignatureProductionPlace(signature));
    return info;
  }

  private static String getTimeAssertionMessageImprint(Signature signature) {
    if (signature.getProfile() != SignatureProfile.LT_TM) {
      TimestampWrapper timestamp = getBestTimestampWrapper(signature);
      try {
        return ReportBuilderUtils.parseTimeAssertionMessageImprint(timestamp);
      } catch (Exception e) {
        LOGGER.warn("Unable to parse time assertion message imprint from timestamp: ", e);
        return ""; //parse errors due to corrupted timestamp data should be present in validation errors already
      }
    }

    try {
      return StringUtils.defaultString(Base64.encodeBase64String(signature.getOCSPNonce()));
    } catch (DigiDoc4JException e) {
      LOGGER.warn("Unable to parse time assertion message imprint from OCSP nonce: ", e);
      return ""; //parse errors due to corrupted OCSP data should be present in validation errors already
    }
  }

  private static TimestampWrapper getBestTimestampWrapper(Signature signature) {
    DiagnosticData diagnosticData = ((AsicESignature) signature).getDssValidationReport().getReports().getDiagnosticData();
    SignatureWrapper signatureWrapper = diagnosticData.getSignatureById(signature.getUniqueId());
    List<TimestampWrapper> timestamps = signatureWrapper.getTimestampListByType(TimestampType.SIGNATURE_TIMESTAMP);
    return timestamps.isEmpty() ? null : Collections.min(timestamps, Comparator.comparing(TimestampWrapper::getProductionTime));
  }

  private static SignatureProductionPlace getSignatureProductionPlace(Signature signature) {
    if (isSignatureProductionPlaceEmpty(signature)) {
      return null;
    }

    SignatureProductionPlace signatureProductionPlace = new SignatureProductionPlace();
    signatureProductionPlace.setCountryName(StringUtils.defaultString(signature.getCountryName()));
    signatureProductionPlace.setStateOrProvince(StringUtils.defaultString(signature.getStateOrProvince()));
    signatureProductionPlace.setCity(StringUtils.defaultString(signature.getCity()));
    signatureProductionPlace.setPostalCode(StringUtils.defaultString(signature.getPostalCode()));
    return signatureProductionPlace;
  }

  private static boolean isSignatureProductionPlaceEmpty(Signature signature) {
    return StringUtils.isAllEmpty(
      signature.getCountryName(),
      signature.getStateOrProvince(),
      signature.getCity(),
      signature.getPostalCode()
    );
  }

  private static String getBestSignatureTime(Signature signature) {
    return formatTime(signature.getTrustedSigningTime());
  }

  private static String getOcspTime(Signature signature) {
    return formatTime(signature.getOCSPResponseCreationTime());
  }

  private static String getTimestampTime(Signature signature) {
    return formatTime(signature.getTimeStampCreationTime());
  }

  private static String formatTime(Date date) {
    return Optional.ofNullable(date)
      .map(d -> ReportBuilderUtils.getDateFormatterWithGMTZone().format(d))
      .orElse(null);
  }

  private static List<SignerRole> getSignerRole(Signature signature) {
    return signature.getSignerRoles().stream()
      .filter(StringUtils::isNotEmpty)
      .map(SignatureInfoParser::mapSignerRole)
      .collect(Collectors.toList());
  }

  private static SignerRole mapSignerRole(String claimedRole) {
    SignerRole signerRole = new SignerRole();
    signerRole.setClaimedRole(claimedRole);
    return signerRole;
  }
}
