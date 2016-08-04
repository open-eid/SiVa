package ee.openeid.siva.proxy.document;

import eu.europa.esig.dss.MimeType;

public enum DocumentType {

    PDF {
        @Override
        public MimeType getMimeType() {
            return MimeType.PDF;
        }
    },
    XROAD {
        @Override
        public MimeType getMimeType() {
            return MimeType.ASICE;
        }
    },
    BDOC {
        @Override
        public MimeType getMimeType() {
            return MimeType.ASICE;
        }
    },
    DDOC {
        @Override
        public MimeType getMimeType() { return MimeType.XML; }
    };

    public abstract MimeType getMimeType();
}
