package ee.openeid.siva.common;

public class DssMessage {
    String key;
    String value;

    public DssMessage(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}


