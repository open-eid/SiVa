package eu.europa.esig.jaxb.tsl;

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.File;

import static org.junit.Assert.assertNotNull;

/**
 *
 * This test loads the UE LOTL
 */
@Ignore
public class LoadUELOTLTest {

	@Test
	@SuppressWarnings("unchecked")
	public void test() throws JAXBException{
		File euLOTL = new File("src/test/resources/tl-mp.xml");

		JAXBContext jc = JAXBContext.newInstance("eu.europa.esig.jaxb.tsl");
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		JAXBElement<TrustStatusListType> unmarshalled = (JAXBElement<TrustStatusListType>) unmarshaller.unmarshal(euLOTL);
		assertNotNull(unmarshalled);

		TrustStatusListType euLOTLObj = unmarshalled.getValue();

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		JAXBIntrospector introspector = jc.createJAXBIntrospector();

		if (null == introspector.getElementName(euLOTLObj)) {
			JAXBElement jaxbElement = new JAXBElement(new QName("ROOT"), Object.class, euLOTLObj);
			marshaller.marshal(jaxbElement, System.out);
		}

	}
}
