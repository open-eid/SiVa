package ee.openeid.siva.validation.document.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Certificate {
    private String commonName;
    private String content;
    private Certificate issuer;
    private CertificateType type;
}
