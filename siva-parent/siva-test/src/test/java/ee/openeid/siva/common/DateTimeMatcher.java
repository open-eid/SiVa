package ee.openeid.siva.common;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.ZonedDateTime;

public class DateTimeMatcher extends TypeSafeMatcher<String> {

    private final ZonedDateTime beforeDateTime;

    public DateTimeMatcher(ZonedDateTime beforeDateTime) {
        this.beforeDateTime = beforeDateTime.withNano(0);
    }

    @Override
    protected boolean matchesSafely(String target) {
        ZonedDateTime targetDateTime = ZonedDateTime.parse(target);
        return targetDateTime.isEqual(beforeDateTime) || targetDateTime.isAfter(beforeDateTime);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("date after '" + beforeDateTime + "'");
    }

    public static Matcher<String> isEqualOrAfter(ZonedDateTime beforeDateTime) {
        return new DateTimeMatcher(beforeDateTime);
    }
}
