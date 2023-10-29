/*
 * Copyright 2022 - 2023 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.helper;

import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestXmlDetailUtils {

    public static List<XmlMessage> createMessageList(XmlMessage... messages) {
        return Arrays.stream(messages).collect(Collectors.toList());
    }

    public static List<XmlMessage> createMessageListWithNullKeys(String... messageValues) {
        return Arrays.stream(messageValues)
                .map(messageValue -> createMessage(null, messageValue))
                .collect(Collectors.toList());
    }

    public static List<XmlMessage> createMessageListWithNullValues(String... messageKeys) {
        return Arrays.stream(messageKeys)
                .map(messageKey -> createMessage(messageKey, null))
                .collect(Collectors.toList());
    }

    public static void assertEquals(List<XmlMessage> expected, List<XmlMessage> actual) {
        if (expected == actual) {
            return;
        } else if (expected != null && actual != null && expected.size() == actual.size()) {
            if (IntStream.range(0, expected.size()).allMatch(i -> equals(expected.get(i), actual.get(i)))) {
                return;
            }
        }
        Assertions.fail(String.format("Message lists do not match!\n\texpected: %s\n\tbut was: %s",
                messageListToString(expected), messageListToString(actual)
        ));
    }

    public static void assertEquals(XmlMessage expected, XmlMessage actual) {
        Assertions.assertTrue(equals(expected, actual), () -> String
                .format("Messages do not match!\n\texpected: %s\n\tbut was: %s",
                        messageToString(expected), messageToString(actual)
                ));
    }

    public static XmlMessage createMessage(String messageKey, String messageValue) {
        final XmlMessage message = new XmlMessage();
        message.setKey(messageKey);
        message.setValue(messageValue);
        return message;
    }

    public static boolean equals(XmlMessage message1, XmlMessage message2) {
        return (message1 == message2) || (message1 != null && message2 != null &&
                StringUtils.equals(message1.getKey(), message2.getKey()) &&
                StringUtils.equals(message1.getValue(), message2.getValue())
        );
    }

    private static String messageListToString(List<XmlMessage> messageList) {
        return Objects.toString(Optional
                .ofNullable(messageList)
                .map(msg -> msg.stream()
                        .map(TestXmlDetailUtils::messageToString)
                        .collect(Collectors.toList())
                )
                .orElse(null)
        );
    }

    private static String messageToString(XmlMessage message) {
        return Objects.toString(Optional
                .ofNullable(message)
                .map(msg -> String.format("{key:%s,value:%s}",
                        valueToString(msg.getKey()),
                        valueToString(msg.getValue())
                ))
                .orElse(null)
        );
    }

    private static String valueToString(String value) {
        return Objects.toString(Optional
                .ofNullable(value)
                .map(val -> String.format("\"%s\"", value))
                .orElse(null)
        );
    }

}
