package ee.openeid.siva.validation.util;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class CertUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertUtil.class);

    public static String getCountryCode(X509Certificate cert) {
        try {
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
            RDN c = x500name.getRDNs(BCStyle.C)[0];
            return IETFUtils.valueToString(c.getFirst().getValue());
        } catch (CertificateEncodingException e) {
            LOGGER.error("Error extracting country from certificate", e.getMessage(), e);
            return null;
        }
    }

}
