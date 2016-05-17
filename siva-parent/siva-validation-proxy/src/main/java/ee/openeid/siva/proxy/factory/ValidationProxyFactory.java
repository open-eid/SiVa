package ee.openeid.siva.proxy.factory;

import ee.openeid.siva.proxy.BDocValidationProxy;
import ee.openeid.siva.proxy.DDocValidationProxy;
import ee.openeid.siva.proxy.PdfValidationProxy;
import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationProxyFactory {

    private PdfValidationProxy pdfValidationProxy;

    private BDocValidationProxy bdocValidationProxy;

    private DDocValidationProxy ddocValidationProxy;

    public ValidationProxy getProxyForType(final DocumentType documentType) {
        if (DocumentType.PDF.equals(documentType)) {
            return pdfValidationProxy;
        } else if (DocumentType.BDOC.equals(documentType)) {
            return bdocValidationProxy;
        } else if (DocumentType.DDOC.equals(documentType)) {
            return ddocValidationProxy;
        } else {
            throw new UnsupportedTypeException("type = " + documentType.name() + " is unsupported");
        }
    }

    @Autowired
    public void setPdfValidationProxy(PdfValidationProxy pdfValidationProxy) {
        this.pdfValidationProxy = pdfValidationProxy;
    }

    @Autowired
    public void setBDocValidationProxy(BDocValidationProxy bdocValidationProxy) {
        this.bdocValidationProxy = bdocValidationProxy;
    }

    @Autowired
    public void setDDocValidationProxy(DDocValidationProxy ddocValidationProxy) {
        this.ddocValidationProxy = ddocValidationProxy;
    }

}
