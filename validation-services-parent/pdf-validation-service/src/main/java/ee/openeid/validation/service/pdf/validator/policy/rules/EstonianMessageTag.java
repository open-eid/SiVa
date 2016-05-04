package ee.openeid.validation.service.pdf.validator.policy.rules;

import eu.europa.esig.dss.validation.policy.rules.AttributeName;

public enum EstonianMessageTag {

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
