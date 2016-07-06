package ee.openeid.validation.service.pdf.signature.policy;


import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicySettings;

public class PDFSignaturePolicyService extends SignaturePolicyService {

    public PDFSignaturePolicyService(SignaturePolicySettings signaturePolicySettings) {
        super(signaturePolicySettings);
    }

}
