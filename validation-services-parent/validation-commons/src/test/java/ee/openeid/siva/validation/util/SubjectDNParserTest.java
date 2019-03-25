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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SubjectDNParserTest {

    @Test
    public void parseSerialNumberAsOID() {
        String subjectDN = "2.5.4.5=#130b3437313031303130303333,2.5.4.42=#0c05504552454e494d49,2.5.4.4=#0c074545534e494d49,CN=PERENIMI\\,EESNIMI\\,47101010033,OU=digital signature,O=ESTEID,C=EE";
        String parsedSerialNumber = SubjectDNParser.parse(subjectDN, SubjectDNParser.RDN.SERIALNUMBER);
        assertEquals("47101010033", parsedSerialNumber);
    }

    @Test
    public void parseSerialNumberAsName() {
        String subjectDN = "SERIALNUMBER=47101010033,SN=PERENIMI,GN=EESNIMI,CN=PERENIMI\\,EESNIMI\\,47101010033,OU=digital signature,O=ESTEID,C=EE";
        String parsedSerialNumber = SubjectDNParser.parse(subjectDN, SubjectDNParser.RDN.SERIALNUMBER);
        assertEquals("47101010033", parsedSerialNumber);
    }

    @Test
    public void parsedRDNValueQuotesRemovedIfPresent() {
        String subjectDN = "CN=\"47101010033\",OU=digital signature,O=ESTEID,C=EE";
        String parsedSerialNumber = SubjectDNParser.parse(subjectDN, SubjectDNParser.RDN.CN);
        assertEquals("47101010033", parsedSerialNumber);
    }

    @Test
    public void searchedSubjectNameNotPresent() {
        String subjectDN = "SN=PERENIMI,GN=EESNIMI,CN=PERENIMI\\,EESNIMI\\,47101010033,OU=digital signature,O=ESTEID,C=EE";
        String parsedSerialNumber = SubjectDNParser.parse(subjectDN, SubjectDNParser.RDN.SERIALNUMBER);
        assertNull(parsedSerialNumber);
    }
}
