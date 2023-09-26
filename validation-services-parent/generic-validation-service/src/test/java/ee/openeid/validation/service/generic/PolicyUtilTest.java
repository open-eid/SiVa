/*
 * Copyright 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.exception.IllegalInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.validation.service.generic.PolicyUtil.getTLevelSignatures;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class PolicyUtilTest {

    @Mock
    private ConstraintDefinedPolicy policy;

    @ParameterizedTest
    @MethodSource("signatureLevelProvider")
    void getTLevelSignatures_WhenSignatureValidatorReturnsStreamFromValidConstraintFile_ThenReturnListOfTLevelSignatureTypes(
            String newElements, List<String> expectedSignatureLevels) throws Exception {
        InputStream inputStream = replaceValidXmlBlock(newElements);

        doReturn(inputStream).when(policy).getConstraintDataStream();

        List<String> result = getTLevelSignatures(policy);

        assertEquals(expectedSignatureLevels, result);
        verifyNoMoreInteractions(policy);
    }

    @Test
    void getTLevelSignatures_WhenSignatureValidatorReturnsEmptyStream_ThenThrowException() {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        doReturn(inputStream).when(policy).getConstraintDataStream();

        IllegalInputException caughtException = assertThrows(
                IllegalInputException.class, () -> getTLevelSignatures(policy)
        );

        assertEquals("Unable to load the policy", caughtException.getMessage());
        verifyNoMoreInteractions(policy);
    }

    @Test
    void getTLevelSignatures_WhenSignatureValidatorReturnsStreamFromInvalidConstraintFile_ThenThrowException() {
        InputStream inputStream = PolicyUtilTest.class.getClassLoader().getResourceAsStream("invalid-constraint.xml");

        doReturn(inputStream).when(policy).getConstraintDataStream();

        IllegalInputException caughtException = assertThrows(
                IllegalInputException.class, () -> getTLevelSignatures(policy)
        );

        assertEquals("Unable to load the policy", caughtException.getMessage());
        verifyNoMoreInteractions(policy);
    }

    private static ByteArrayInputStream replaceValidXmlBlock(String newElements) throws Exception {
        String xmlString;

        try (InputStream inputStream = PolicyUtilTest.class.getClassLoader().getResourceAsStream("valid-constraint.xml")) {
            Scanner scanner = new Scanner(inputStream);
            xmlString = scanner.useDelimiter("\\A").next();
        }

        String startTag = "<AcceptableFormats Level=\"FAIL\">";
        String endTag = "</AcceptableFormats>";
        int startIndex = xmlString.indexOf(startTag) + startTag.length();
        int endIndex = xmlString.indexOf(endTag);

        String block = xmlString.substring(startIndex, endIndex);
        xmlString = xmlString.replace(block, newElements);

        return new ByteArrayInputStream(xmlString.getBytes());
    }

    private static String createNewBlockElements(SignatureLevel... values) {
        return Stream.of(values)
                .map(value -> String.format("<Id>%s</Id>", value))
                .collect(Collectors.joining("\n"));
    }

    private static List<String> asStringList(SignatureLevel... values) {
        return Stream.of(values)
                .map(SignatureLevel::toString)
                .collect(Collectors.toUnmodifiableList());
    }

    private static Stream<Arguments> signatureLevelProvider() {
        return Stream.of(
                Arguments.of(
                        createNewBlockElements(
                                SignatureLevel.XAdES_BASELINE_B,
                                SignatureLevel.XAdES_BASELINE_LT,
                                SignatureLevel.XAdES_BASELINE_LTA
                        ),
                        Collections.emptyList()
                ),
                Arguments.of(
                        createNewBlockElements(
                                SignatureLevel.XAdES_BASELINE_B,
                                SignatureLevel.XAdES_BASELINE_T,
                                SignatureLevel.XAdES_BASELINE_LT,
                                SignatureLevel.XAdES_BASELINE_LTA
                        ),
                        asStringList(
                                SignatureLevel.XAdES_BASELINE_T
                        )
                ),
                Arguments.of(
                        createNewBlockElements(
                                SignatureLevel.XAdES_BASELINE_B,
                                SignatureLevel.XAdES_BASELINE_T,
                                SignatureLevel.XAdES_BASELINE_LT,
                                SignatureLevel.XAdES_BASELINE_LTA,
                                SignatureLevel.CAdES_BASELINE_B,
                                SignatureLevel.CAdES_BASELINE_T,
                                SignatureLevel.CAdES_BASELINE_LT,
                                SignatureLevel.CAdES_BASELINE_LTA
                        ),
                        asStringList(
                                SignatureLevel.XAdES_BASELINE_T,
                                SignatureLevel.CAdES_BASELINE_T
                        )
                ),
                Arguments.of(
                        createNewBlockElements(
                                SignatureLevel.XAdES_BASELINE_B,
                                SignatureLevel.XAdES_BASELINE_T,
                                SignatureLevel.XAdES_BASELINE_LT,
                                SignatureLevel.XAdES_BASELINE_LTA,
                                SignatureLevel.CAdES_BASELINE_B,
                                SignatureLevel.CAdES_BASELINE_T,
                                SignatureLevel.CAdES_BASELINE_LT,
                                SignatureLevel.CAdES_BASELINE_LTA,
                                SignatureLevel.PAdES_BASELINE_B,
                                SignatureLevel.PAdES_BASELINE_T,
                                SignatureLevel.PAdES_BASELINE_LT,
                                SignatureLevel.PAdES_BASELINE_LTA
                        ),
                        asStringList(
                                SignatureLevel.XAdES_BASELINE_T,
                                SignatureLevel.CAdES_BASELINE_T,
                                SignatureLevel.PAdES_BASELINE_T
                        )
                )
        );
    }
}
