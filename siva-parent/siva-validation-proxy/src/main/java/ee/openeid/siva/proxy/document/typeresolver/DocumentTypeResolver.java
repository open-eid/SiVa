/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.proxy.document.typeresolver;

import ee.openeid.siva.proxy.document.DocumentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static java.util.Arrays.stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentTypeResolver {

    public static DocumentType documentTypeFromString(String type) {
        Optional<DocumentType> documentType = stream(DocumentType.class.getEnumConstants())
                .filter(dt -> dt.name().equalsIgnoreCase(type))
                .findAny();

        if (!documentType.isPresent()) {
            throw new UnsupportedTypeException("type = " + type + " is unsupported");
        }
        return documentType.get();
    }

}
