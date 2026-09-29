/*
 * Copyright 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.document.report.builder;

import eu.europa.esig.dss.enumerations.SignatureLevel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Strings;

import java.util.function.Predicate;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportBuilderTestUtil {

    private static final Predicate<SignatureLevel> LTA_PROFILE_PREDICATE = level ->
            Strings.CS.endsWith(level.name(), "_LTA");

    public static Stream<String> ltaSignatureFormatStrings() {
        return Stream.of(SignatureLevel.values())
                .filter(LTA_PROFILE_PREDICATE)
                .map(SignatureLevel::name);
    }

    public static Stream<String> nonLtaSignatureFormatStrings() {
        return Stream.of(SignatureLevel.values())
                .filter(LTA_PROFILE_PREDICATE.negate())
                .map(SignatureLevel::name);
    }

}
