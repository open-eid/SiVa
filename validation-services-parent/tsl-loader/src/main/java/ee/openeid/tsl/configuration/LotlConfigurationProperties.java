package ee.openeid.tsl.configuration;

import lombok.Data;

import java.util.List;

@Data
public class LotlConfigurationProperties {
    private boolean primary;
    private String url;
    private String ojurl;
    private String lotlRootSchemeInfoUri;
    private String otherTslPointer;
    private List<String> trustedTerritories;
    private boolean lotlPivotSupportEnabled;
    private boolean mraSupport;
    private TSLValidationKeystoreProperties validationTruststore;
}
