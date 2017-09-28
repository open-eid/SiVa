package ee.openeid.siva.signature;

import java.io.IOException;

public interface SignatureService {

    byte[] getSignature(byte[] dataToSign, String dataName, String mimeTypeString) throws IOException;

} 
