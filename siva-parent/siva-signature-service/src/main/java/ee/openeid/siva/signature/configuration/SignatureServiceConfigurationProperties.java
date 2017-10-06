package ee.openeid.siva.signature.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("siva.signatureService")
public class SignatureServiceConfigurationProperties {
    private String signatureLevel;
    private String tspUrl;
    private String ocspUrl;
    private Pkcs11Properties pkcs11;
    private Pkcs12Properties pkcs12;
} 
