/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp.request.validation;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ReportType;
import eu.europa.esig.dss.DigestAlgorithm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum AcceptedValue {
    DOCUMENT {
        @Override
        public List<String> getAcceptedValues() {
            return Collections.singletonList(DocumentType.XROAD.name());
        }
    }, REPORT_TYPE {
        @Override
        public List<String> getAcceptedValues() {
            return Arrays.stream(ReportType.values()).map(Enum::name).collect(Collectors.toList());
        }
    }, HASH_ALGO {
        @Override
        public List<String> getAcceptedValues() {
            return Arrays.stream(DigestAlgorithm.values()).map(Enum::name).collect(Collectors.toList());
        }
    };

    public abstract List<String> getAcceptedValues();
}
