package ee.openeid.siva.singature;

import java.io.IOException;

public interface SignatureService {

    byte[] getSignature(byte[] dataToSign, String dataName, String mimeTypeString) throws IOException;

} 
