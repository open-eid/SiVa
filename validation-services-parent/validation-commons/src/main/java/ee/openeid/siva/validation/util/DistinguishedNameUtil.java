/*
 * Copyright 2022 - 2023 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.validation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DistinguishedNameUtil {

    /**
     * Extracts subject distinguished name from X-509 certificate.
     *
     * @param certificate the certificate to extract subject distinguished name from
     *
     * @return subject distinguished name
     *
     * @throws IllegalArgumentException if parsing the certificate fails
     */
    public static X500Name getSubjectDistinguishedName(X509Certificate certificate) {
        try {
            return new JcaX509CertificateHolder(certificate).getSubject();
        } catch (CertificateEncodingException e) {
            throw new IllegalArgumentException("Certificate encoding error", e);
        }
    }

    /**
     * Extracts the value of the first field from the certificate's subject distinguished name
     * that matches the specified object identifier, or {@code null} if no such field is found.
     *
     * @param certificate the certificate to extract the subject distinguished name field from
     * @param oid the object identifier of the field to extract
     *
     * @return the specified subject distinguished name field or {@code null}
     *
     * @see #getSubjectDistinguishedName(X509Certificate)
     * @see #getDistinguishedNameValueByOid(X500Name, ASN1ObjectIdentifier)
     */
    public static String getSubjectDistinguishedNameValueByOid(X509Certificate certificate, ASN1ObjectIdentifier oid) {
        return getDistinguishedNameValueByOid(getSubjectDistinguishedName(certificate), oid);
    }

    /**
     * Extracts the value of the first field from the specified distinguished name
     * that matches the specified object identifier, or {@code null} if no such field is found.
     *
     * @param distinguishedName the distinguished name to extract the field from
     * @param oid the object identifier of the field to extract
     *
     * @return the specified distinguished name field or {@code null}
     */
    public static String getDistinguishedNameValueByOid(X500Name distinguishedName, ASN1ObjectIdentifier oid) {
        for (RDN rdn : distinguishedName.getRDNs(oid)) {
            for (AttributeTypeAndValue typeAndValue : rdn.getTypesAndValues()) {
                if (oid.equals(typeAndValue.getType()) && Objects.nonNull(typeAndValue.getValue())) {
                    return typeAndValue.getValue().toString();
                }
            }
        }

        return null;
    }

    /**
     * Returns a comma-separated list of subject distinguished name fields surName, givenName and serialNumber
     * without natural person semantics identifier, in the form of "{@code JÕEORG,JAAK-KRISTJAN,38001085718}",
     * or {@code null} if any of the fields is missing.
     *
     * @param certificate the certificate to extract subject's surName, givenName and serialNumber from
     *
     * @return a comma-separated list of subject's surName, givenName and serialNumber or {@code null}
     *
     * @see #getSubjectDistinguishedName(X509Certificate)
     * @see #getSurnameAndGivenNameAndSerialNumber(X500Name)
     */
    public static String getSubjectSurnameAndGivenNameAndSerialNumber(X509Certificate certificate) {
        return getSurnameAndGivenNameAndSerialNumber(getSubjectDistinguishedName(certificate));
    }

    /**
     * Returns a comma-separated list of distinguished name fields surName, givenName and serialNumber
     * without natural person semantics identifier, in the form of "{@code JÕEORG,JAAK-KRISTJAN,38001085718}",
     * or {@code null} if any of the fields is missing.
     *
     * @param distinguishedName the distinguished name to extract surName, givenName and serialNumber from
     *
     * @return a comma-separated list of surName, givenName and serialNumber or {@code null}
     *
     * @see #withoutNaturalPersonSemanticsIdentifier(String)
     */
    public static String getSurnameAndGivenNameAndSerialNumber(X500Name distinguishedName) {
        String surname = getDistinguishedNameValueByOid(distinguishedName, BCStyle.SURNAME);
        if (surname == null) {
            return null;
        }

        String givenName = getDistinguishedNameValueByOid(distinguishedName, BCStyle.GIVENNAME);
        if (givenName == null) {
            return null;
        }

        String serialNumber = getDistinguishedNameValueByOid(distinguishedName, BCStyle.SERIALNUMBER);
        if (serialNumber == null) {
            return null;
        }

        return getSurnameAndGivenNameAndSerialNumber(surname, givenName, serialNumber);
    }

    /**
     * Returns a comma-separated list of surName, givenName and serialNumber
     * without natural person semantics identifier, in the form of "{@code JÕEORG,JAAK-KRISTJAN,38001085718}",
     * or {@code null} if any of the fields is missing.
     *
     * @param surname the surName field
     * @param givenName the givenName field
     * @param serialNumber the serialNumber field, either with or without natural person semantics identifier
     *
     * @return a comma-separated list of surName, givenName and serialNumber or {@code null}
     *
     * @see #withoutNaturalPersonSemanticsIdentifier(String)
     */
    public static String getSurnameAndGivenNameAndSerialNumber(String surname, String givenName, String serialNumber) {
        if (surname != null && givenName != null && serialNumber != null) {
            return surname + ',' + givenName + ',' + withoutNaturalPersonSemanticsIdentifier(serialNumber);
        } else {
            return null;
        }
    }

    private static final List<String> NATURAL_IDENTITY_TYPE_REFERENCES = List.of(
            "PAS", "IDC", "PNO", "TAX", "TIN"
    );

    /**
     * Strips natural person semantics identifier from the serialNumber, if present.
     * More information about the natural person semantics identifier can be found
     * <a href="https://www.etsi.org/deliver/etsi_en/319400_319499/31941201/01.01.01_60/en_31941201v010101p.pdf#page=10">here</a>.
     *
     * @param serialNumber the serialNumber to strip from natural person semantics identifier
     *
     * @return the serialNumber without natural person semantics identifier
     */
    public static String withoutNaturalPersonSemanticsIdentifier(String serialNumber) {
        if (StringUtils.length(serialNumber) > 6 && serialNumber.charAt(5) == '-') {
            if (serialNumber.charAt(2) == ':' || NATURAL_IDENTITY_TYPE_REFERENCES.contains(serialNumber.substring(0, 3))) {
                return serialNumber.substring(6);
            }
        }

        return serialNumber;
    }

}
