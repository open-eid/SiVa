package ee.openeid.siva.integrationtest.report.simple;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignatureDeserializer extends JsonDeserializer<List<Signature>> {

    @Override
    public List<Signature> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        List<Signature> signatures = new ArrayList<>();
        if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
            Signature signature = jsonParser.readValueAs(Signature.class);
            signatures.add(signature);
        } else {
            signatures = jsonParser.readValueAs(new TypeReference<List<Signature>>(){});
        }
        return signatures;
    }
}
