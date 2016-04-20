package ee.openeid.siva.proxy.document.typeresolver;

import eu.europa.esig.dss.MimeType;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeResolver {

    private static Map<String, MimeType> supportedMimeTypes = new HashMap<String, MimeType>() {{
        put("PDF", MimeType.PDF);
    }};

    public static MimeType mimeTypeFromString(String type) {
        MimeType mimeType = supportedMimeTypes.get(StringUtils.upperCase(type));
        if (mimeType == null) {
            throw new UnsupportedTypeException("type = " + type + " is unsupported");
        }
        return mimeType;
    }

}
