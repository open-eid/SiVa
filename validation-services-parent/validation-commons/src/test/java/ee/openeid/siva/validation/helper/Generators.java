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

package ee.openeid.siva.validation.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Generators {

    /**
     * Creates a supplier that returns the specified values in order, repeating the last value infinitively.
     * At least one value must be specified!
     *
     * @param values an array of values to return in order
     * @return the next value from the specified list of values, or the last value if the end of the list has been reached
     * @param <T> value type
     *
     * @see #infinite(List)
     */
    @SafeVarargs
    public static <T> Supplier<T> infinite(T... values) {
        return infinite(List.of(values));
    }

    /**
     * Creates a supplier that returns the specified values in order, repeating the last value infinitively.
     * At least one value must be specified!
     *
     * @param values a list of values to return in order
     * @return the next value from the specified list of values, or the last value if the end of the list has been reached
     * @param <T> value type
     *
     * @see #infinite(Object[])
     */
    public static <T> Supplier<T> infinite(List<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            throw new IllegalArgumentException("Infinite generator must be provided with at least one value");
        }

        final T last = values.get(values.size() - 1);

        return Stream.concat(
                values.stream(),
                Stream.generate(() -> last)
        )
                .iterator()::next;
    }

}
