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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ListUtilTest {

    @Mock
    private Supplier<List<CustomType>> listGetter;
    @Mock
    private Consumer<List<CustomType>> listSetter;

    @Test
    void getOrCreateListWithCustomSupplier_WhenGetterReturnsAnExistingList_ReturnsItWithoutInteractingWithSetterNorSupplier() {
        List<CustomType> existingList = mockList();
        doReturn(existingList).when(listGetter).get();
        Supplier<List<CustomType>> newListSupplier = mockListSupplier();

        List<CustomType> result = ListUtil.getOrCreateList(listGetter, listSetter, newListSupplier);

        assertThat(result, sameInstance(existingList));
        verifyNoMoreInteractions(listGetter);
        verifyNoInteractions(listSetter, existingList, newListSupplier);
    }

    @Test
    void getOrCreateListWithCustomSupplier_WhenGetterReturnsNull_AcquiresNewListViaSupplierAndSetsItViaSetterAndReturnsIt() {
        doReturn(null).when(listGetter).get();
        Supplier<List<CustomType>> newListSupplier = mockListSupplier();
        List<CustomType> newList = mockList();
        doReturn(newList).when(newListSupplier).get();

        List<CustomType> result = ListUtil.getOrCreateList(listGetter, listSetter, newListSupplier);

        assertThat(result, sameInstance(newList));
        ArgumentCaptor<List<CustomType>> argumentCaptor = createArgumentCaptor();
        verify(listSetter).accept(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), sameInstance(newList));
        verifyNoMoreInteractions(listGetter, listSetter, newListSupplier);
        verifyNoInteractions(newList);
    }

    @Test
    void getOrCreateListWithoutCustomSupplier_WhenGetterReturnsAnExistingList_ReturnsItWithoutInteractingWithSetter() {
        List<CustomType> existingList = mockList();
        doReturn(existingList).when(listGetter).get();

        List<CustomType> result = ListUtil.getOrCreateList(listGetter, listSetter);

        assertThat(result, sameInstance(existingList));
        verifyNoMoreInteractions(listGetter);
        verifyNoInteractions(listSetter, existingList);
    }

    @Test
    void getOrCreateListWithoutCustomSupplier_WhenGetterReturnsNull_SetsNewArrayListViaSetterAndReturnsIt() {
        doReturn(null).when(listGetter).get();

        List<CustomType> result = ListUtil.getOrCreateList(listGetter, listSetter);

        assertThat(result, instanceOf(ArrayList.class));
        ArgumentCaptor<List<CustomType>> argumentCaptor = createArgumentCaptor();
        verify(listSetter).accept(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), sameInstance(result));
        verifyNoMoreInteractions(listGetter, listSetter);
    }

    private record CustomType() {
    }

    @SuppressWarnings("unchecked")
    private static ArgumentCaptor<List<CustomType>> createArgumentCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<List<CustomType>> mockListSupplier() {
        return mock(Supplier.class);
    }

    @SuppressWarnings("unchecked")
    private static List<CustomType> mockList() {
        return mock(List.class);
    }

}
