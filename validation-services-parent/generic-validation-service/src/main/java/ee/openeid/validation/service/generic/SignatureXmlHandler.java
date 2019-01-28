package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.document.Datafile;
import eu.europa.esig.dss.DigestAlgorithm;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SignatureXmlHandler extends DefaultHandler {

    private boolean signedInfo;
    private boolean reference;
    private List<Datafile> datafiles = new ArrayList<>();
    private Datafile currentDatafile;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("ds:SignedInfo".equals(qName)) {
            signedInfo = true;
        }
        if (signedInfo && "ds:Reference".equals(qName)) {
            reference = true;
            currentDatafile = new Datafile();
            for (int i = 0; i < attributes.getLength(); i++) {
                if ("URI".equals(attributes.getQName(i))) {
                    currentDatafile.setFilename(attributes.getValue(i));
                }
            }
        }
        if (signedInfo && reference && "ds:DigestMethod".equals(qName)) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if ("Algorithm".equals(attributes.getQName(i))) {
                    currentDatafile.setHashAlgo(DigestAlgorithm.forXML(attributes.getValue(i)).getName());
                }
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        if ("ds:SignedInfo".equals(qName)) {
            signedInfo = false;
            datafiles.remove(currentDatafile);
            currentDatafile = null;
        }
        if ("ds:Reference".equals(qName)) {
            reference = false;
            datafiles.add(currentDatafile);
        }

    }

    @Override
    public void characters(char ch[], int start, int length) {
        if (signedInfo && reference && currentDatafile != null) {
            currentDatafile.setHash(new String(ch, start, length));
        }
    }

    public List<Datafile> getDatafiles() {
        return datafiles;
    }
}
