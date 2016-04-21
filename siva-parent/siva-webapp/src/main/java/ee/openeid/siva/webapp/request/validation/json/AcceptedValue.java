package ee.openeid.siva.webapp.request.validation.json;

import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.typeresolver.DocumentType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AcceptedValue {
    DOCUMENT {
        public List<String> getAcceptedValues() {
            return Arrays.asList(DocumentType.values()).stream().map(Enum::name).collect(Collectors.toList());
        }
    },
    REPORT {
        public List<String> getAcceptedValues() {
            return Arrays.asList(ReportType.values()).stream().map(Enum::name).collect(Collectors.toList());
        }
    };

    public abstract List<String> getAcceptedValues();
}
