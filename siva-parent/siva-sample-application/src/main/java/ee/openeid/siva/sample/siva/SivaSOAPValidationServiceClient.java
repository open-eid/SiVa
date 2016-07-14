package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.upload.UploadedFile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service(value = "sivaSOAP")
public class SivaSOAPValidationServiceClient implements ValidationService {
    @Override
    public String validateDocument(UploadedFile file) throws IOException {
        return null;
    }
}
