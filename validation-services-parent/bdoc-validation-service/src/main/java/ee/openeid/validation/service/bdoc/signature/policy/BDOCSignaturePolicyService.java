package ee.openeid.validation.service.bdoc.signature.policy;

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
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
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;

    public String getAbsolutePath(String policyName) {
        LOGGER.debug("creating policy file from path {}", policyName);
        InputStream inputStream = signaturePolicyService.getPolicy(policyName).getConstraintDataStream();
        try {
            File file = File.createTempFile(getTempPolicyName(policyName), ".xml");
            file.deleteOnExit();
            OutputStream outputStream = new FileOutputStream(file);

            LOGGER.info("BDOC policy constraints file added: {}", file.getAbsolutePath());
            IOUtils.copy(inputStream, outputStream);

            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error("Unable to create temporary file from bdoc policy resource", e);
            throw new BdocPolicyFileCreationException(e);
        }
    }

    public ConstraintDefinedPolicy getPolicy(String policyName) {
        return signaturePolicyService.getPolicy(policyName);
    }

    private String getTempPolicyName(String policyName) {
        String validPolicyName = StringUtils.isEmpty(policyName) ? "default" : policyName;
        return "siva-bdoc-" + validPolicyName + "-constraint-";
    }

    @Autowired
    public void setSignaturePolicyService(ConstraintLoadingSignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }
}
