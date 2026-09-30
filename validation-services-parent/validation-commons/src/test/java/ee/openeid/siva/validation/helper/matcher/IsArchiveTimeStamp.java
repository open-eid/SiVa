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

package ee.openeid.siva.validation.helper.matcher;

import ee.openeid.siva.validation.document.report.ArchiveTimeStamp;
import ee.openeid.siva.validation.helper.Generators;
import eu.europa.esig.dss.enumerations.Indication;
import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class IsArchiveTimeStamp extends TypeSafeDiagnosingMatcher<ArchiveTimeStamp> {

    private final Matcher<String> signedTimeMatcher;
    private final Matcher<Indication> indicationMatcher;
    private final Matcher<String> subIndicationMatcher;
    private final Matcher<String> signedByMatcher;
    private final Matcher<String> countryMatcher;
    private final Matcher<String> contentMatcher;

    @Override
    protected boolean matchesSafely(ArchiveTimeStamp item, Description mismatchDescription) {
        Supplier<String> conjunctionGenerator = Generators.infinite("ArchiveTimeStamp ", " and ");
        boolean result = true;

        if (signedTimeMatcher != null && !signedTimeMatcher.matches(item.getSignedTime())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("signedTime ");
            signedTimeMatcher.describeMismatch(item.getSignedTime(), mismatchDescription);
            result = false;
        }

        if (indicationMatcher != null && !indicationMatcher.matches(item.getIndication())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("indication ");
            indicationMatcher.describeMismatch(item.getIndication(), mismatchDescription);
            result = false;
        }

        if (subIndicationMatcher != null && !subIndicationMatcher.matches(item.getSubIndication())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("subIndication ");
            subIndicationMatcher.describeMismatch(item.getSubIndication(), mismatchDescription);
            result = false;
        }

        if (signedByMatcher != null && !signedByMatcher.matches(item.getSignedBy())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("signedBy ");
            signedByMatcher.describeMismatch(item.getSignedBy(), mismatchDescription);
            result = false;
        }

        if (countryMatcher != null && !countryMatcher.matches(item.getCountry())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("country ");
            countryMatcher.describeMismatch(item.getCountry(), mismatchDescription);
            result = false;
        }

        if (contentMatcher != null && !contentMatcher.matches(item.getContent())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("content ");
            contentMatcher.describeMismatch(item.getContent(), mismatchDescription);
            result = false;
        }

        return result;
    }

    @Override
    public void describeTo(Description description) {
        Supplier<String> conjunctionGenerator = Generators.infinite(" with ", " and ");
        description.appendText("ArchiveTimeStamp");

        if (signedTimeMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("signedTime ").appendDescriptionOf(signedTimeMatcher);
        }
        if (indicationMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("indication ").appendDescriptionOf(indicationMatcher);
        }
        if (subIndicationMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("subIndication ").appendDescriptionOf(subIndicationMatcher);
        }
        if (signedByMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("signedBy ").appendDescriptionOf(signedByMatcher);
        }
        if (countryMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("country ").appendDescriptionOf(countryMatcher);
        }
        if (contentMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("content ").appendDescriptionOf(contentMatcher);
        }
    }

    public static Matcher<ArchiveTimeStamp> isArchiveTimeStampWithIndications(
            Indication indication,
            String subIndication
    ) {
        return isArchiveTimeStampWithIndications(
                Optional.ofNullable(indication).map(Matchers::sameInstance).orElseGet(() -> Matchers.nullValue(Indication.class)),
                Optional.ofNullable(subIndication).map(Matchers::equalTo).orElseGet(() -> Matchers.nullValue(String.class))
        );
    }

    public static Matcher<ArchiveTimeStamp> isArchiveTimeStampWithIndications(
            Matcher<Indication> indicationMatcher,
            Matcher<String> subIndicationMatcher
    ) {
        return new IsArchiveTimeStamp(
                null,
                indicationMatcher,
                subIndicationMatcher,
                null,
                null,
                null
        );
    }

}
