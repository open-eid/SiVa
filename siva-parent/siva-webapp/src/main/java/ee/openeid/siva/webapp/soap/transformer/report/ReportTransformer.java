/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp.soap.transformer.report;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Transforms DSS returned report object to service api report object.
 *
 * @param <D> Initial report class from the dependency
 * @param <I> Intermediate report class generated from given report XSD
 * @param <F> Final report class generated from WSDL descriptive of given service
 */
abstract class ReportTransformer<D, I, F> {

    private final JAXBContext marshallerContext;
    private final JAXBContext unMarshallerContext;

    ReportTransformer(Class<D> reportTypeClass, String reportClassesPackage) {
        try {
            marshallerContext = JAXBContext.newInstance(reportTypeClass);
            unMarshallerContext = JAXBContext.newInstance(reportClassesPackage);
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to initialize JAXBContext", e);
        }
    }

    public F transform(D report) {
        return transformReport(unmarshallerReport(report));
    }

    abstract F transformReport(I report);

    private I unmarshallerReport(D report) {
        try {
            Marshaller marshaller = marshallerContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(report, writer);
            String requestString = writer.toString();
            StringReader reader = new StringReader(requestString);
            Unmarshaller unmarshaller = unMarshallerContext.createUnmarshaller();
            return ((JAXBElement<I>) unmarshaller.unmarshal(reader)).getValue();
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to un-marshal report", e);
        }
    }
}
