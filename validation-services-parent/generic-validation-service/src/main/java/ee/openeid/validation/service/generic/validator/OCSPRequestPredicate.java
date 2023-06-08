package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.model.x509.CertificateToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;

import java.security.cert.X509Certificate;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@RequiredArgsConstructor
public class OCSPRequestPredicate implements BiPredicate<CertificateToken, CertificateToken> {

    @NonNull
    private final BiFunction<X509Certificate, ASN1ObjectIdentifier, String> subjectDnAttributeExtractor;

    @Override
    public boolean test(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        String country = subjectDnAttributeExtractor.apply(certificateToken.getCertificate(), BCStyle.C);
        return !StringUtils.equals(country, "EE");
    }
}
