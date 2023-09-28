/*
 * Copyright 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.tsl.configuration;

import ee.openeid.siva.validation.helper.TestLog;
import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.annotation.LoadableTsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TSLLoaderDefinitionRegistererTest {

    private static final String TEST_BEAN_NAME = "testBeanName";
    private static final String TEST_LOADER_NAME = TEST_BEAN_NAME + "Loader";
    private static final String TEST_TSL_NAME = "testTslName";

    private static final String NO_ATTRIBUTE_FOUND_MESSAGE = "No attribute 'name' found";
    private static final String INVALID_ATTRIBUTE_TYPE_MESSAGE = "Attribute 'name' is not of type java.lang.String";
    private static final String BEAN_REGISTRATION_MESSAGE_TEMPLATE = "Registering bean definition '%s' for loadable TSL bean '%s' named '%s'";
    private static final String BEAN_REGISTRATION_MESSAGE = String
            .format(BEAN_REGISTRATION_MESSAGE_TEMPLATE, TEST_LOADER_NAME, TEST_BEAN_NAME, TEST_TSL_NAME);

    private TestLog testLog;
    private TSLLoaderDefinitionRegisterer tslLoaderDefinitionRegisterer;

    @BeforeEach
    void setUp() {
        testLog = new TestLog(TSLLoaderDefinitionRegisterer.class);
        tslLoaderDefinitionRegisterer = new TSLLoaderDefinitionRegisterer();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasNoDefinitions_NoDefinitionsAdded() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[0]).when(beanDefinitionRegistry).getBeanDefinitionNames();

        tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);

        verifyNoMoreInteractions(beanDefinitionRegistry);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasNoAnnotatedDefinitions_NoDefinitionsAdded() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        BeanDefinition nonAnnotatedBeanDefinition = mock(BeanDefinition.class);
        doReturn(nonAnnotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);

        tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);

        verifyNoMoreInteractions(beanDefinitionRegistry);
        verifyNoInteractions(nonAnnotatedBeanDefinition);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasAnnotatedDefinitionWithoutLoadableTslAnnotationInFactoryMethodMetadata_NoDefinitionsAdded() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        MethodMetadata factoryMethodMetadata = mock(MethodMetadata.class);
        doReturn(factoryMethodMetadata).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        doReturn(false).when(factoryMethodMetadata).isAnnotated(LoadableTsl.class.getCanonicalName());

        tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);

        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, factoryMethodMetadata);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasAnnotatedDefinitionWithoutLoadableTslAnnotationInClassMetadata_NoDefinitionsAdded() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        doReturn(null).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        AnnotationMetadata classMetadata = mock(AnnotationMetadata.class);
        doReturn(classMetadata).when(annotatedBeanDefinition).getMetadata();
        doReturn(false).when(classMetadata).hasAnnotation(LoadableTsl.class.getCanonicalName());

        tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);

        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, classMetadata);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasAnnotatedDefinitionWithLoadableTslAnnotationWithMissingNameInFactoryMethodMetadata_ExceptionThrown() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        MethodMetadata factoryMethodMetadata = mock(MethodMetadata.class);
        doReturn(factoryMethodMetadata).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        doReturn(true).when(factoryMethodMetadata).isAnnotated(LoadableTsl.class.getCanonicalName());
        doReturn(Collections.emptyMap()).when(factoryMethodMetadata).getAnnotationAttributes(LoadableTsl.class.getCanonicalName());

        IllegalStateException caughtException = assertThrows(
                IllegalStateException.class,
                () -> tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry)
        );

        assertThat(caughtException.getMessage(), equalTo(NO_ATTRIBUTE_FOUND_MESSAGE));
        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, factoryMethodMetadata);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasAnnotatedDefinitionWithLoadableTslAnnotationWithMissingNameInClassMetadata_ExceptionThrown() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        doReturn(null).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        AnnotationMetadata classMetadata = mock(AnnotationMetadata.class);
        doReturn(classMetadata).when(annotatedBeanDefinition).getMetadata();
        doReturn(true).when(classMetadata).hasAnnotation(LoadableTsl.class.getCanonicalName());
        doReturn(Collections.emptyMap()).when(classMetadata).getAnnotationAttributes(LoadableTsl.class.getCanonicalName());

        IllegalStateException caughtException = assertThrows(
                IllegalStateException.class,
                () -> tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry)
        );

        assertThat(caughtException.getMessage(), equalTo(NO_ATTRIBUTE_FOUND_MESSAGE));
        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, classMetadata);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasAnnotatedDefinitionWithLoadableTslAnnotationWithInvalidNameInFactoryMethodMetadata_ExceptionThrown() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        MethodMetadata factoryMethodMetadata = mock(MethodMetadata.class);
        doReturn(factoryMethodMetadata).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        doReturn(true).when(factoryMethodMetadata).isAnnotated(LoadableTsl.class.getCanonicalName());
        Map<String, Object> attributes = Collections.singletonMap("name", String.class);
        doReturn(attributes).when(factoryMethodMetadata).getAnnotationAttributes(LoadableTsl.class.getCanonicalName());

        IllegalStateException caughtException = assertThrows(
                IllegalStateException.class,
                () -> tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry)
        );

        assertThat(caughtException.getMessage(), equalTo(INVALID_ATTRIBUTE_TYPE_MESSAGE));
        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, factoryMethodMetadata);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasAnnotatedDefinitionWithLoadableTslAnnotationWithInvalidNameInClassMetadata_ExceptionThrown() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        doReturn(null).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        AnnotationMetadata classMetadata = mock(AnnotationMetadata.class);
        doReturn(classMetadata).when(annotatedBeanDefinition).getMetadata();
        doReturn(true).when(classMetadata).hasAnnotation(LoadableTsl.class.getCanonicalName());
        Map<String, Object> attributes = Collections.singletonMap("name", String.class);
        doReturn(attributes).when(classMetadata).getAnnotationAttributes(LoadableTsl.class.getCanonicalName());

        IllegalStateException caughtException = assertThrows(
                IllegalStateException.class,
                () -> tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry)
        );

        assertThat(caughtException.getMessage(), equalTo(INVALID_ATTRIBUTE_TYPE_MESSAGE));
        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, classMetadata);
        testLog.verifyLogEmpty();
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasHasValidTslBeanDefinition_LoaderBeanDefinitionRegistered() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        MethodMetadata factoryMethodMetadata = mock(MethodMetadata.class);
        doReturn(factoryMethodMetadata).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        doReturn(true).when(factoryMethodMetadata).isAnnotated(LoadableTsl.class.getCanonicalName());
        Map<String, Object> attributes = Collections.singletonMap("name", TEST_TSL_NAME);
        doReturn(attributes).when(factoryMethodMetadata).getAnnotationAttributes(LoadableTsl.class.getCanonicalName());

        tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);

        verifyLoaderDefinitionRegistration(beanDefinitionRegistry);
        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, factoryMethodMetadata);
        testLog.verifyLogInOrder(equalTo(BEAN_REGISTRATION_MESSAGE));
    }

    @Test
    void postProcessBeanDefinitionRegistry_WhenBeanDefinitionRegistryHasValidTslBeanDefinition_LoaderBeanDefinitionRegistered() {
        BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);
        doReturn(new String[] {TEST_BEAN_NAME}).when(beanDefinitionRegistry).getBeanDefinitionNames();
        AnnotatedBeanDefinition annotatedBeanDefinition = mock(AnnotatedBeanDefinition.class);
        doReturn(annotatedBeanDefinition).when(beanDefinitionRegistry).getBeanDefinition(TEST_BEAN_NAME);
        doReturn(null).when(annotatedBeanDefinition).getFactoryMethodMetadata();
        AnnotationMetadata classMetadata = mock(AnnotationMetadata.class);
        doReturn(classMetadata).when(annotatedBeanDefinition).getMetadata();
        doReturn(true).when(classMetadata).hasAnnotation(LoadableTsl.class.getCanonicalName());
        Map<String, Object> attributes = Collections.singletonMap("name", TEST_TSL_NAME);
        doReturn(attributes).when(classMetadata).getAnnotationAttributes(LoadableTsl.class.getCanonicalName());

        tslLoaderDefinitionRegisterer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);

        verifyLoaderDefinitionRegistration(beanDefinitionRegistry);
        verifyNoMoreInteractions(beanDefinitionRegistry, annotatedBeanDefinition, classMetadata);
        testLog.verifyLogInOrder(equalTo(BEAN_REGISTRATION_MESSAGE));
    }

    @Test
    void postProcessBeanFactory_WhenBeanFactoryProvided_NoInteractions() {
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);

        tslLoaderDefinitionRegisterer.postProcessBeanFactory(beanFactory);

        verifyNoInteractions(beanFactory);
        testLog.verifyLogEmpty();
    }

    private static void verifyLoaderDefinitionRegistration(BeanDefinitionRegistry beanDefinitionRegistry) {
        ArgumentCaptor<BeanDefinition> beanDefinitionCaptor = ArgumentCaptor.forClass(BeanDefinition.class);
        verify(beanDefinitionRegistry).registerBeanDefinition(Mockito.eq(TEST_LOADER_NAME), beanDefinitionCaptor.capture());
        BeanDefinition registeredBeanDefinition = beanDefinitionCaptor.getValue();

        assertThat(registeredBeanDefinition.isAutowireCandidate(), is(true));
        assertThat(registeredBeanDefinition.isLazyInit(), is(false));
        assertThat(registeredBeanDefinition.isPrimary(), is(false));
        assertThat(registeredBeanDefinition.isPrototype(), is(false));
        assertThat(registeredBeanDefinition.isSingleton(), is(true));

        assertThat(registeredBeanDefinition.hasConstructorArgumentValues(), is(true));
        assertThat(registeredBeanDefinition.getConstructorArgumentValues().getGenericArgumentValues(), emptyIterable());
        assertThat(registeredBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues(), hasEntry(
                equalTo(0),
                samePropertyValuesAs(new ConstructorArgumentValues.ValueHolder(TEST_TSL_NAME))
        ));
        assertThat(registeredBeanDefinition.hasPropertyValues(), is(true));
        assertThat(registeredBeanDefinition.getPropertyValues().getPropertyValues(), arrayWithSize(1));
        assertThat(
                registeredBeanDefinition.getPropertyValues().getPropertyValues()[0].getName(),
                equalTo("trustedListsCertificateSource")
        );
        assertThat(
                registeredBeanDefinition.getPropertyValues().getPropertyValues()[0].getValue(),
                instanceOf(RuntimeBeanReference.class)
        );
        assertThat(
                registeredBeanDefinition.getPropertyValues().getPropertyValues()[0].getValue(),
                samePropertyValuesAs(new RuntimeBeanReference(TEST_BEAN_NAME))
        );

        assertThat(registeredBeanDefinition, instanceOf(GenericBeanDefinition.class));
        GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) registeredBeanDefinition;
        assertThat(genericBeanDefinition.getBeanClass(), is(TSLLoader.class));
    }

}
