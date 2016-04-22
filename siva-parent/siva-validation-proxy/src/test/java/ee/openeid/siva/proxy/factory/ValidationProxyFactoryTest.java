package ee.openeid.siva.proxy.factory;


import ee.openeid.siva.proxy.PdfValidationProxy;
import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.proxy.document.DocumentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

public class ValidationProxyFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ValidationProxyFactory validationProxyFactory;

    @Before
    public void setUp() {
        validationProxyFactory = new ValidationProxyFactory();
        validationProxyFactory.setPdfValidationProxy(new PdfValidationProxy());
    }

    @Test
    public void mimeTypePDFReturnsPdfValidationProxy() {
        ValidationProxy validationProxy = validationProxyFactory.getProxyForType(DocumentType.PDF);
        assertTrue(validationProxy instanceof PdfValidationProxy);
    }

    @Test
    public void expectExceptionWhenMimeTypeUnsupported() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("type = DDOC is unsupported");

        validationProxyFactory.getProxyForType(DocumentType.DDOC);
    }

}
