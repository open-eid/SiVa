/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

public final class SubjectDNParser {

    private SubjectDNParser() {}

    /**
     * Parsing specific relative distinguished name (RDN) from subject distinguished name string where values are comma separated.
     * Sometimes RDN keys are displayed as attribute name abbreviations and sometimes as object identifiers (OID).
     * OID based key-value pairs have values displayed in hex, which will be converted accordingly.
     *
     * @param subjectDistinguishedName
     * @param relativeDistinguishedName
     * @return
     */
    public static String parse(String subjectDistinguishedName, RDN relativeDistinguishedName) {
        for (String RDN : subjectDistinguishedName.split(",")) {
            if (RDN.trim().startsWith(relativeDistinguishedName.getOID())) {
                String RDNValue = RDN.split("=")[1];
                String RDNValueWithOutPrefix = RDNValue.substring(5);
                return removeQuotes(
                        new String(Hex.decode(RDNValueWithOutPrefix), StandardCharsets.UTF_8));
            } else if (RDN.trim().toUpperCase().startsWith(relativeDistinguishedName.name())) {
                return removeQuotes(RDN.split("=")[1]);
            }
        }

        return null;
    }

    private static String removeQuotes(String value) {
        return value.replaceAll("(^\")|(\"$)", "");
    }

    public enum RDN {
        CN("2.5.4.3"),
        SERIALNUMBER("2.5.4.5")
        ;

        private String OID;

        RDN(String OID) {
            this.OID = OID;
        }

        public String getOID() {
            return OID;
        }
    }
}
