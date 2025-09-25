/*
 * Copyright 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.helper.matcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonMatchers {

    private static final String BASE64_REGEX = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";
    private static final Pattern BASE64_PATTERN = Pattern.compile(BASE64_REGEX);
    private static final Duration DEFAULT_CLOCK_SKEW = Duration.ofMinutes(1L);

    public static Matcher<String> base64String() {
        return Matchers.matchesPattern(BASE64_PATTERN);
    }

    public static Matcher<String> stringAsInstantInRange(Instant notBefore, Instant notAfter) {
        return StringAsInstant.stringAsInstant(InstantInRange.instantInRange(notBefore, notAfter));
    }

    public static Matcher<String> stringAsInstantInRange(Instant notBefore, Instant notAfter, TemporalAmount clockSkew) {
        return stringAsInstantInRange(notBefore.minus(clockSkew), notAfter.plus(clockSkew));
    }

    public static Matcher<String> stringAsInstantInRangeWithClockSkew(Instant notBefore, Instant notAfter) {
        return stringAsInstantInRange(notBefore, notAfter, DEFAULT_CLOCK_SKEW);
    }

    public static Matcher<String> stringAsInstantInRangeOfNotBeforeAndNow(Instant notBefore) {
        return stringAsInstantInRange(notBefore, Instant.now());
    }

    public static Matcher<String> stringAsInstantInRangeOfNotBeforeAndNow(Instant notBefore, TemporalAmount clockSkew) {
        return stringAsInstantInRange(notBefore, Instant.now(), clockSkew);
    }

    public static Matcher<String> stringAsInstantInRangeOfNotBeforeAndNowWithClockSkew(Instant notBefore) {
        return stringAsInstantInRangeOfNotBeforeAndNow(notBefore, DEFAULT_CLOCK_SKEW);
    }

}
