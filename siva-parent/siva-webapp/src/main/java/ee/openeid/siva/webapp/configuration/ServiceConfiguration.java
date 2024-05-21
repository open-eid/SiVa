/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import eu.europa.esig.dss.diagnostic.jaxb.XmlAbstractToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.xml.bind.annotation.XmlIDREF;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setAnnotationIntrospector(new JaxbIdRefResolvingAnnotationIntrospector());

        SimpleModule dateSerializationModule = new SimpleModule();
        dateSerializationModule.addSerializer(Date.class, new DateAsIsoInstantSerializer());
        objectMapper.registerModule(dateSerializationModule);

        objectMapper.getFactory().setStreamReadConstraints(getStreamReadConstraintsWithMaxStringLength());

        return objectMapper;
    }

    private static StreamReadConstraints getStreamReadConstraintsWithMaxStringLength() {

        return StreamReadConstraints.builder()
                .maxStringLength(Integer.MAX_VALUE)
                .build();
    }

    /**
     * An introspector that is able to resolve XmlIDREF annotations when serializing DSS Jaxb classes as JSON
     * The introspector is currently able to resolve ID-s of the following DSS classes:
     *  - eu.europa.esig.dss.validation.diagnostic.AbstractToken
     *  - eu.europa.esig.dss.diagnostic.jaxb.XmlAbstractToken
     */
    static class JaxbIdRefResolvingAnnotationIntrospector extends JacksonAnnotationIntrospector {

        private static final Map.Entry<Class<?>, Class<?>>[] TYPE_TO_SERIALIZER_MAPPINGS;

        static {
            Map<Class<?>, Class<?>> typeToSerializerMap = new HashMap<>();
            typeToSerializerMap.put(XmlAbstractToken.class, XmlAbstractTokenIdSerializer.class);
            TYPE_TO_SERIALIZER_MAPPINGS = typeToSerializerMap.entrySet().stream().toArray(Map.Entry[]::new);
        }

        @Override
        public Object findSerializer(Annotated annotated) {
            if (annotated.hasAnnotation(XmlIDREF.class)) {
                final Class<?> fieldType = annotated.getRawType();
                for (Map.Entry<Class<?>, Class<?>> mapping : TYPE_TO_SERIALIZER_MAPPINGS) {
                    if (mapping.getKey().isAssignableFrom(fieldType)) {
                        return mapping.getValue();
                    }
                }
            }
            return super.findSerializer(annotated);
        }
    }

    static class XmlAbstractTokenIdSerializer extends StdSerializer<XmlAbstractToken> {

        public XmlAbstractTokenIdSerializer() {
            super(XmlAbstractToken.class);
        }

        @Override
        public void serialize(XmlAbstractToken value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(value.getId());
        }

    }

    static class DateAsIsoInstantSerializer extends StdSerializer<Date> {

        public DateAsIsoInstantSerializer() {
            super(Date.class);
        }

        @Override
        public void serialize(Date value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(value.toInstant().toString());
        }

    }

}
