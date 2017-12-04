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

package ee.openeid.siva.proxy.document;

import ee.openeid.siva.proxy.document.typeresolver.UnsupportedTypeException;

import java.util.Optional;

import static java.util.Arrays.stream;

public enum ReportType {

    SIMPLE("Simple"),
    DETAILED("Detailed");

    private String value;

    ReportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static ReportType reportTypeFromString(String type) {
        Optional<ReportType> reportType = stream(ReportType.class.getEnumConstants())
                .filter(dt -> dt.name().equalsIgnoreCase(type))
                .findAny();

        if (!reportType.isPresent()) {
            throw new UnsupportedTypeException("type = " + type + " is unsupported");
        }
        return reportType.get();
    }
}
