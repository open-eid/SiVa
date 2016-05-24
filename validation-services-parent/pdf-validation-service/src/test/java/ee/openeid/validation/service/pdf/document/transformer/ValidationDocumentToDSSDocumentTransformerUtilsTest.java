package ee.openeid.validation.service.pdf.document.transformer;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValidationDocumentToDSSDocumentTransformerUtilsTest {

    @Test
    public void tryToAccessingPrivateConstructorShouldFail() throws Exception {
        final Constructor<ValidationDocumentToDSSDocumentTransformerUtils> constructor = ValidationDocumentToDSSDocumentTransformerUtils
                .class
                .getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void createDssDocumentGivenDssDocumentIsNullReturnsNull() throws Exception {
        assertEquals(null, ValidationDocumentToDSSDocumentTransformerUtils.createDssDocument(null));
    }
}