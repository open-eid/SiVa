package ee.openeid.siva.proxy.document;

import eu.europa.esig.dss.MimeType;

public enum DocumentType {

    PDF {
        @Override
        public MimeType getMimeType() {
            return MimeType.PDF;
        }
    },
    XML {
        @Override
        public MimeType getMimeType() {
            return MimeType.XML;
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
        public MimeType getMimeType() {
            return null;
        }
    };

    public abstract MimeType getMimeType();
}
