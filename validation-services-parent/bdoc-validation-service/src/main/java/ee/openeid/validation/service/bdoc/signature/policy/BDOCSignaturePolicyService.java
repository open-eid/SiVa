package ee.openeid.validation.service.bdoc.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class BDOCSignaturePolicyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BDOCSignaturePolicyService.class);
    private SignaturePolicyService signaturePolicyService;

    public String getAbsolutePath(String policy) {
        LOGGER.debug("creating policy file from path {}", policy);
        InputStream inputStream = StringUtils.isEmpty(policy) ?
                new ByteArrayInputStream(signaturePolicyService.getDefaultPolicy()) :
                signaturePolicyService.getPolicyDataStreamFromPolicy(policy);

        try {
            final File file = File.createTempFile(setTempPolicyName(policy), ".xml");
            file.deleteOnExit();
            final OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error("Unable to create temporary file from bdoc policy resource", e);
            throw new BdocPolicyFileCreationException(e);
        }
    }

    private String setTempPolicyName(String policyName) {
        String validPolicyName = StringUtils.isEmpty(policyName) ? "default" : policyName;
        return validPolicyName + "_bdoc_constraint";
    }

    @Autowired
    public void setSignaturePolicyService(SignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }
}
