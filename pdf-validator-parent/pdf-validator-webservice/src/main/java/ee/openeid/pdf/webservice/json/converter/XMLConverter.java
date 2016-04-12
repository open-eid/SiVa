package ee.openeid.pdf.webservice.json.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class XMLConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLConverter.class);

    public String toJSON(String xml) {
        final JSONObject jsonObject = XML.toJSONObject(xml);
        jsonObject.getJSONObject("SimpleReport").remove("xmlns");

        ObjectMapper mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(jsonObject.toString(), Object.class);
            return mapper.writeValueAsString(json);
        } catch (IOException e) {
            LOGGER.warn("Failed to read XML content", e);
        }

        return null;
    }

}
