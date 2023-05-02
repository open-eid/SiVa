package ee.openeid.siva.common;


public class Constants {

    public static final String TEST_ENV_VALIDATION_WARNING = "This is validation service demo. Use it for testing purposes only";

    public static final String MIMETYPE_NOT_FIRST_WARNING = "\"mimetype\" should be the first file in the container";
    public static final String MIMETYPE_COMPRESSED_WARNING = "Container \"mimetype\" file must not be compressed";
    public static final String MIMETYPE_EXTRA_FIELDS_WARNING = "Container \"mimetype\" file must not contain \"Extra fields\" in its ZIP header";
    public static final String MIMETYPE_INVALID_TYPE = "Container should have one of the expected mimetypes: \"application/vnd.etsi.asic-e+zip\", \"application/vnd.etsi.asic-s+zip\"";

    private Constants() {

    }

}
