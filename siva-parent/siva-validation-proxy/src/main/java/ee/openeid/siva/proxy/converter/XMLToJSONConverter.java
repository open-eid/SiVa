package ee.openeid.siva.proxy.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;

@Component
public class XMLToJSONConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLToJSONConverter.class);

    public String toJSON(String xml) {
        final JSONObject jsonObject = XML.toJSONObject(xml);

        removeConvertedXMLNamespaceAttributes(jsonObject);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(jsonObject.toString(), Object.class);
            return mapper.writeValueAsString(json);
        } catch (IOException e) {
            LOGGER.warn("Failed to read XML content", e);
        }

        return null;
    }

    private void removeConvertedXMLNamespaceAttributes(JSONObject jsonObject) {
        Iterator<?> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = (String)keys.next();
            if (jsonObject.get(key) instanceof JSONObject) {
                ((JSONObject) jsonObject.get(key)).remove("xmlns");
            }
        }
    }

}
