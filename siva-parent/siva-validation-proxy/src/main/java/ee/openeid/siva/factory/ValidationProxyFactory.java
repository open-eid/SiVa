package ee.openeid.siva.factory;

import ee.openeid.siva.ValidationProxy;
import ee.openeid.siva.impl.PdfValidationProxy;
import ee.openeid.siva.mimetype.MimeTypeResolver;
import eu.europa.esig.dss.MimeType;
import org.springframework.stereotype.Service;

@Service
public class ValidationProxyFactory {

    public ValidationProxy getValidationProxy(MimeType mimetype) {
        if (mimetype == MimeType.PDF) {
            return new PdfValidationProxy();
        } else {
            throw new MimeTypeResolver.UnsupportedTypeException("type = " + mimetype.getMimeTypeString() + " is unsupported");
        }
    }
}
