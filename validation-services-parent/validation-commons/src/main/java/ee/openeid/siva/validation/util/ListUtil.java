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

package ee.openeid.siva.validation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListUtil {

    /**
     * Returns either the list provided by {@code listGetter} or a new list obtained via {@code newListSupplier}.
     * If the return value of {@code listGetter} is {@code null}, then the new list obtained via {@code newListSupplier}
     * will be registered via {@code listSetter} before returning it.
     *
     * @param listGetter the supplier to obtain an existing list from
     * @param listSetter the consumer to register a new list to
     * @param newListSupplier the supplier to obtain a new list from (e.g. a list constructor)
     * @return an existing {@link List} or a new list obtained via the new list supplier
     * @param <E> list element type
     */
    public static <E> List<E> getOrCreateList(
            Supplier<List<E>> listGetter,
            Consumer<List<E>> listSetter,
            Supplier<List<E>> newListSupplier
    ) {
        List<E> list = listGetter.get();

        if (list == null) {
            list = newListSupplier.get();
            listSetter.accept(list);
        }

        return list;
    }

    /**
     * Returns either the list provided by {@code listGetter} or a newly created {@link ArrayList}.
     * If the return value of {@code listGetter} is {@code null}, then the newly created {@link ArrayList} will be
     * registered via {@code listSetter} before returning it.
     *
     * @param listGetter the supplier to obtain an existing list from
     * @param listSetter the consumer to register a newly created list to
     * @return an existing {@link List} or a newly created {@link ArrayList}
     * @param <E> list element type
     *
     * @see #getOrCreateList(Supplier, Consumer, Supplier)
     */
    public static <E> List<E> getOrCreateList(
            Supplier<List<E>> listGetter,
            Consumer<List<E>> listSetter
    ) {
        return getOrCreateList(listGetter, listSetter, ArrayList::new);
    }

}
