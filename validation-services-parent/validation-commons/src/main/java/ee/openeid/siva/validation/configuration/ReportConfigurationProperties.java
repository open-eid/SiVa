package ee.openeid.siva.validation.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("siva.report")
public class ReportConfigurationProperties {
    private boolean reportSignatureEnabled = false;
} 
