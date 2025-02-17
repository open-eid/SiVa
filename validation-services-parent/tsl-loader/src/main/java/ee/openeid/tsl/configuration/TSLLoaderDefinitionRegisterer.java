/*
 * Copyright 2023 - 2025 Riigi Infosüsteemi Amet
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

import ee.openeid.tsl.TSLLoader;
import ee.openeid.tsl.annotation.LoadableTsl;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * A bean definition registry post-processor that finds all the existing bean definitions with {@link LoadableTsl}
 * annotation, and for each such definition registers a new bean definition of type {@link TSLLoader}, wiring the
 * annotated bean into the loader.
 * <b>NB:</b> All beans having {@link LoadableTsl} annotation are expected to be of type or subtype of
 * {@link TrustedListsCertificateSource}.
 * The {@link TSLLoader#getTslName()} property of each TSL loader is assigned from {@link LoadableTsl#name()} attribute
 * of its corresponding trusted lists certificate source bean.
 */
@Slf4j
@Component
public class TSLLoaderDefinitionRegisterer implements BeanDefinitionRegistryPostProcessor {

    private static final String ANNOTATION_NAME = LoadableTsl.class.getCanonicalName();
    private static final String TSL_SOURCE_PROPERTY_NAME = resolvePropertyName(TSLLoader.class, TrustedListsCertificateSource.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for (String beanDefinitionName : beanDefinitionRegistry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
            Map<String, Object> loadableTslAttributes = getAttributesIfLoadableTslBeanDefinition(beanDefinition);
            if (loadableTslAttributes == null) {
                continue;
            }

            String loaderBeanDefinitionName = beanDefinitionName + "Loader";
            String tslName = getAttribute(loadableTslAttributes, "name", String.class);
            log.info(
                    "Registering bean definition '{}' for loadable TSL bean '{}' named '{}'",
                    loaderBeanDefinitionName, beanDefinitionName, tslName
            );
            beanDefinitionRegistry.registerBeanDefinition(loaderBeanDefinitionName, BeanDefinitionBuilder
                    .genericBeanDefinition(TSLLoader.class)
                    .addConstructorArgValue(tslName)
                    .addPropertyReference(TSL_SOURCE_PROPERTY_NAME, beanDefinitionName)
                    .getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Do nothing
    }

    private static Map<String, Object> getAttributesIfLoadableTslBeanDefinition(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
            MethodMetadata factoryMethodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
            if (factoryMethodMetadata != null) {
                if (factoryMethodMetadata.isAnnotated(ANNOTATION_NAME)) {
                    return factoryMethodMetadata.getAnnotationAttributes(ANNOTATION_NAME);
                }
            } else {
                AnnotationMetadata classMetadata = annotatedBeanDefinition.getMetadata();
                if (classMetadata.hasAnnotation(ANNOTATION_NAME)) {
                    return classMetadata.getAnnotationAttributes(ANNOTATION_NAME);
                }
            }
        }
        return null;
    }

    private static <T> T getAttribute(Map<String, Object> attributes, String name, Class<T> type) {
        Object attributeValue = attributes.get(name);
        if (attributeValue == null) {
            throw new IllegalStateException(String.format("No attribute '%s' found", name));
        } else if (type.isInstance(attributeValue)) {
            return type.cast(attributeValue);
        }
        throw new IllegalStateException(String.format("Attribute '%s' is not of type %s", name, type.getName()));
    }

    private static String resolvePropertyName(Class<?> beanType, Class<?> propertyType) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanType);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if (propertyType.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    return propertyDescriptor.getName();
                }
            }
        } catch (IntrospectionException e) {
            throw new IllegalStateException("Failed to introspect " + beanType.getName(), e);
        }
        throw new IllegalStateException(String.format(
                "Failed to find property of type %s from %s",
                propertyType.getName(), beanType.getName()
        ));
    }

}
