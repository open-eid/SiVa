package ee.openeid.validation.service.pdf.validator.policy.rules;

import eu.europa.esig.dss.validation.policy.rules.AttributeName;

public enum EstonianMessageTag {

    BBB_VCI_ISFC("Is the signature format correct?"),
    BBB_VCI_ISFC_ANS_1("The signature format is not allowed by the validation policy constraint!"),
    ADEST_IOABST("Is OCSP after the best-signature-time?"),
    ADEST_IOABST_ANS("The validation failed, because OCSP is before the best-signature-time!"),
    ADEST_IOTNLABST("Is OCSP not long after the best-signature-time?"),
    ADEST_IOTNLABST_ANS("OCSP is too long after the best-signature-time");

    public static final String NAME_ID = AttributeName.NAME_ID;

    private final String message;

    EstonianMessageTag(final String message) {

        this.message = message;
    }

    /**
     * This method return the message associated with the tag.
     *
     * @return {@code String} message.
     */
    public String getMessage() {
        return message;
    }

}
