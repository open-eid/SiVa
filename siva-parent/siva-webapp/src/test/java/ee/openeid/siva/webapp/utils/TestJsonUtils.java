/*
 * Copyright 2026 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.webapp.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestJsonUtils {

    public static ObjectNode toJsonNode(Map<String, Object> properties) {
        return new JsonMapper().convertValue(properties, ObjectNode.class);
    }

    public static String toJsonString(Map<String, Object> properties) {
        return toJsonNode(properties).toString();
    }

    public static byte[] toJsonBytes(Map<String, Object> properties) {
        return toJsonBytes(toJsonNode(properties));
    }

    public static byte[] toJsonBytes(ObjectNode jsonNode) {
        return jsonNode.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static ObjectNode with(ObjectNode jsonNode, Map<String, Object> properties) {
        toJsonNode(properties).forEachEntry(jsonNode::set);
        return jsonNode;
    }

}
