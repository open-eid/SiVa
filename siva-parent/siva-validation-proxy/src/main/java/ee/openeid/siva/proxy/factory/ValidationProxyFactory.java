package ee.openeid.siva.proxy.factory;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.PdfValidationProxy;
import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;
import eu.europa.esig.dss.MimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationProxyFactory {

    private PdfValidationProxy pdfValidationProxy;

    public ValidationProxy getValidationProxy(final MimeType mimetype) {
        if (MimeType.PDF.equals(mimetype)) {
            return pdfValidationProxy;
        } else {
            throw new UnsupportedTypeException("type = " + mimetype.getMimeTypeString() + " is unsupported");
        }
    }

    @Autowired
    public void setPdfValidationProxy(PdfValidationProxy pdfValidationProxy) {
        this.pdfValidationProxy = pdfValidationProxy;
    }

}
