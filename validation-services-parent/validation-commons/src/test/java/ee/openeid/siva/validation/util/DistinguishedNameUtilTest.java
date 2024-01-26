/*
 * Copyright 2022 - 2024 Riigi Infosüsteemi Amet
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

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

class DistinguishedNameUtilTest {

    @ParameterizedTest(name = "[{index}] \"{0}\" -> \"{1}\"")
    @MethodSource("prefixedSerialNumberAndExpectedResultPairs")
    void testWithoutNaturalPersonSemanticsIdentifier(String serialNumber, String expectedResult) {
        String result = DistinguishedNameUtil.withoutNaturalPersonSemanticsIdentifier(serialNumber);
        Assertions.assertEquals(expectedResult, result);
    }

    static Stream<Arguments> prefixedSerialNumberAndExpectedResultPairs() {
        return Stream.of(
                Arguments.arguments("PASSK-P3000180", "P3000180"),
                Arguments.arguments("IDCBE-590082394654", "590082394654"),
                Arguments.arguments("PNOEE-38001085718", "38001085718"),
                Arguments.arguments("TAXSW-18492018423", "18492018423"),
                Arguments.arguments("TINSB-3849282371", "3849282371"),
                Arguments.arguments("EI:SE-200007292386", "200007292386"),
                Arguments.arguments("38001085718", "38001085718"),
                Arguments.arguments("12345-6789", "12345-6789"),
                Arguments.arguments(StringUtils.SPACE, StringUtils.SPACE),
                Arguments.arguments(StringUtils.EMPTY, StringUtils.EMPTY)
        );
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" -> \"{1}\"")
    @MethodSource("surnameAndGivenNameAndSerialNumberInX500NameAndExpectedResultPairs")
    void testGetSurnameAndGivenNameAndSerialNumberFromX500NameSucceeds(X500Name x500Name, String expectedResult) {
        String result = DistinguishedNameUtil.getSurnameAndGivenNameAndSerialNumber(x500Name);
        Assertions.assertEquals(expectedResult, result);
    }

    static Stream<Arguments> surnameAndGivenNameAndSerialNumberInX500NameAndExpectedResultPairs() {
        return Stream.of(
                Arguments.arguments(
                        new X500Name("SERIALNUMBER=38001085718,GIVENNAME=JAAK-KRISTJAN,SN=JÕEORG"),
                        "JÕEORG,JAAK-KRISTJAN,38001085718"
                ),
                Arguments.arguments(
                        new X500Name("SERIALNUMBER=PNOEE-38001085718,GIVENNAME=JAAK-KRISTJAN,SN=JÕEORG"),
                        "JÕEORG,JAAK-KRISTJAN,38001085718"
                ),
                Arguments.arguments(
                        new X500Name("CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\",SN=JÕEORG,GIVENNAME=JAAK-KRISTJAN,SERIALNUMBER=38001085718"),
                        "JÕEORG,JAAK-KRISTJAN,38001085718"
                )
        );
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" -> null")
    @MethodSource("incompleteX500NameAndExpectedResultPairs")
    void testGetSurnameAndGivenNameAndSerialNumberFromX500NameFails(X500Name x500Name) {
        String result = DistinguishedNameUtil.getSurnameAndGivenNameAndSerialNumber(x500Name);
        Assertions.assertNull(result);
    }

    static Stream<X500Name> incompleteX500NameAndExpectedResultPairs() {
        return Stream.of(
                new X500Name("SN=JÕEORG"),
                new X500Name("GIVENNAME=JAAK-KRISTJAN"),
                new X500Name("SERIALNUMBER=38001085718"),
                new X500Name("SERIALNUMBER=PNOEE-38001085718"),
                new X500Name("SN=JÕEORG,GIVENNAME=JAAK-KRISTJAN"),
                new X500Name("SN=JÕEORG,SERIALNUMBER=38001085718"),
                new X500Name("SN=JÕEORG,SERIALNUMBER=PNOEE-38001085718"),
                new X500Name("GIVENNAME=JAAK-KRISTJAN,SERIALNUMBER=38001085718"),
                new X500Name("GIVENNAME=JAAK-KRISTJAN,SERIALNUMBER=PNOEE-38001085718"),
                new X500Name("CN=\"JÕEORG,JAAK-KRISTJAN,38001085718\"")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} -> \"{1}\"")
    @MethodSource("certificateAndExpectedSurnameGivenNameSerialNumberResultPairs")
    void testGetSurnameAndGivenNameAndSerialNumberFromCertificateSucceeds(X509Certificate certificate, String expectedResult) {
        String result = DistinguishedNameUtil.getSubjectSurnameAndGivenNameAndSerialNumber(certificate);
        Assertions.assertEquals(expectedResult, result);
    }

    static Stream<Arguments> certificateAndExpectedSurnameGivenNameSerialNumberResultPairs() {
        return Stream.of(
                Arguments.arguments(
                        loadCertificate("src/test/resources/certificates/J_EORG_JAAK_KRISTJAN_38001085718.cer"),
                        "JÕEORG,JAAK-KRISTJAN,38001085718"
                ),
                Arguments.arguments(
                        loadCertificate("src/test/resources/certificates/O_CONNE_USLIK_TESTNUMBER_MARY_NN_60001013739.cer"),
                        "O’CONNEŽ-ŠUSLIK TESTNUMBER,MARY ÄNN,60001013739"
                ),
                Arguments.arguments(
                        loadCertificate("src/test/resources/certificates/O_CONNE_USLIK_TESTNUMBER_MARY_NN_60001016970.cer"),
                        "O’CONNEŽ-ŠUSLIK TESTNUMBER,MARY ÄNN,60001016970"
                ),
                Arguments.arguments(
                        loadCertificate("src/test/resources/certificates/_RIN_W_KY_M_R_L_Z_11404176865.cer"),
                        "ŽÕRINÜWŠKY,MÄRÜ-LÖÖZ,11404176865"
                )
        );
    }

    @ParameterizedTest(name = "[{index}] {0} -> null")
    @MethodSource("certificatesNotSupportingSurnameAndGivenNameAndSerialNumberLists")
    void testGetSurnameAndGivenNameAndSerialNumberFromCertificateFails(X509Certificate certificate) {
        String result = DistinguishedNameUtil.getSubjectSurnameAndGivenNameAndSerialNumber(certificate);
        Assertions.assertNull(result);
    }

    static Stream<X509Certificate> certificatesNotSupportingSurnameAndGivenNameAndSerialNumberLists() {
        return Stream.of(
                loadCertificate("src/test/resources/certificates/innovaatik_b4b.cer")
        );
    }

    static X509Certificate loadCertificate(String path) {
        try (InputStream in = new FileInputStream(path)) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(in);
        } catch (CertificateException | IOException e) {
            throw new IllegalStateException("Failed to load certificate: " + path, e);
        }
    }

}