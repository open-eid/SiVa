package ee.openeid.validation.service.pdf.signature.policy;


import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.SignaturePolicyProperties;

public class PDFSignaturePolicyService extends SignaturePolicyService {

    public PDFSignaturePolicyService(SignaturePolicyProperties signaturePolicyProperties) {
        super(signaturePolicyProperties);
    }

}
