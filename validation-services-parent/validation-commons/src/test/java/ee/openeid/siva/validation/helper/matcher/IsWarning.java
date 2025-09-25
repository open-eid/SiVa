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

import ee.openeid.siva.validation.document.report.Warning;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

@RequiredArgsConstructor
public class IsWarning extends TypeSafeDiagnosingMatcher<Warning> {

    private final @NonNull Matcher<String> contentMatcher;

    @Override
    protected boolean matchesSafely(Warning item, Description mismatchDescription) {
        if (!contentMatcher.matches(item.getContent())) {
            mismatchDescription.appendText("Warning content ");
            contentMatcher.describeMismatch(item.getContent(), mismatchDescription);
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Warning with content ").appendDescriptionOf(contentMatcher);
    }

    public static Matcher<Warning> warning(Matcher<String> contentMatcher) {
        return new IsWarning(contentMatcher);
    }

    public static Matcher<Warning> warning(String content) {
        return warning(Matchers.equalTo(content));
    }

}
