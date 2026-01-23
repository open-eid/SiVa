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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
public class StringAsInstant extends TypeSafeDiagnosingMatcher<String> {

    private final @NonNull Matcher<Instant> instantMatcher;

    @Override
    protected boolean matchesSafely(String item, Description mismatchDescription) {
        Instant parsedInstant;
        try {
            parsedInstant = Instant.parse(item);
        } catch (DateTimeParseException e) {
            mismatchDescription.appendText("'").appendText(item).appendText("' is not a valid Instant");
            return false;
        }
        if (!instantMatcher.matches(parsedInstant)) {
            instantMatcher.describeMismatch(parsedInstant, mismatchDescription);
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("String as ").appendDescriptionOf(instantMatcher);
    }

    public static Matcher<String> stringAsInstant(Matcher<Instant> instantMatcher) {
        return new StringAsInstant(instantMatcher);
    }

}
