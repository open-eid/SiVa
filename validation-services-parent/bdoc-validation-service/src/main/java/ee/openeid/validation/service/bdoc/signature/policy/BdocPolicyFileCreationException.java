package ee.openeid.validation.service.bdoc.signature.policy;

public class BdocPolicyFileCreationException extends RuntimeException {
    public BdocPolicyFileCreationException(Exception e) {
        super(e);
    }
}
