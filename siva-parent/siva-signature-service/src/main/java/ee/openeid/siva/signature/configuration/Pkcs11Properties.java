package ee.openeid.siva.signature.configuration;

import lombok.Data;

@Data
public class Pkcs11Properties {
    private String path;
    private String password;
    private int slotIndex;
} 
