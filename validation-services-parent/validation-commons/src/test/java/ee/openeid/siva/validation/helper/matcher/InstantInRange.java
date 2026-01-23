/*
 * Copyright 2025 - 2026 Riigi Infosüsteemi Amet
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

import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.time.Instant;

@AllArgsConstructor
public class InstantInRange extends TypeSafeDiagnosingMatcher<Instant> {

    private final Instant notBefore;
    private final Instant notAfter;

    @Override
    protected boolean matchesSafely(Instant item, Description mismatchDescription) {
        if (notBefore != null && item.isBefore(notBefore)) {
            mismatchDescription.appendText("Instant ").appendValue(item).appendText(" is before ").appendValue(notBefore);
            return false;
        }
        if (notAfter != null && item.isAfter(notAfter)) {
            mismatchDescription.appendText("Instant ").appendValue(item).appendText(" is after ").appendValue(notAfter);
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Instant");
        if (notBefore != null) {
            description.appendText(" not before ").appendValue(notBefore);
        }
        if (notBefore != null && notAfter != null) {
            description.appendText(" and");
        }
        if (notAfter != null) {
            description.appendText(" not after ").appendValue(notAfter);
        }
    }

    public static Matcher<Instant> instantInRange(String notBefore, String notAfter) {
        return instantInRange(Instant.parse(notBefore), Instant.parse(notAfter));
    }

    public static Matcher<Instant> instantInRange(Instant notBefore, Instant notAfter) {
        return new InstantInRange(notBefore, notAfter);
    }

    public static Matcher<Instant> instantNotBefore(String notBefore) {
        return instantNotBefore(Instant.parse(notBefore));
    }

    public static Matcher<Instant> instantNotBefore(Instant notBefore) {
        return new InstantInRange(notBefore, null);
    }

    public static Matcher<Instant> instantNotAfter(String notAfter) {
        return instantNotAfter(Instant.parse(notAfter));
    }

    public static Matcher<Instant> instantNotAfter(Instant notAfter) {
        return new InstantInRange(null, notAfter);
    }

}
