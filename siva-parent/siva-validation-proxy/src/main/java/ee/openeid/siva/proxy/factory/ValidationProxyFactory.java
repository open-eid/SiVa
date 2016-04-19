package ee.openeid.siva.proxy.factory;

import ee.openeid.siva.mimetype.MimeTypeResolver;
import ee.openeid.siva.proxy.ValidationProxy;
import eu.europa.esig.dss.MimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationProxyFactory {

    @Autowired
    private ValidationProxy validationProxy;

    public ValidationProxy getValidationProxy(final MimeType mimetype) {
        if (MimeType.PDF.equals(mimetype)) {
            return validationProxy;
        } else {
            throw new MimeTypeResolver.UnsupportedTypeException("type = " + mimetype.getMimeTypeString() + " is unsupported");
        }
    }
}
