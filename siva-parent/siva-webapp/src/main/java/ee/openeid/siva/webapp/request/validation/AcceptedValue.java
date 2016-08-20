package ee.openeid.siva.webapp.request.validation;

import ee.openeid.siva.proxy.document.DocumentType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AcceptedValue {
    DOCUMENT {
        @Override
        public List<String> getAcceptedValues() {
            return Arrays.asList(DocumentType.values()).stream().map(Enum::name).collect(Collectors.toList());
        }
    };

    public abstract List<String> getAcceptedValues();
}
