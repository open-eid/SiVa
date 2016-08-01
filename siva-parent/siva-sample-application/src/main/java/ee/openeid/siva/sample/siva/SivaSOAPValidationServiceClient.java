package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.cache.UploadedFile;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.io.IOException;

@Service(value = SivaServiceType.SOAP_SERVICE)
public class SivaSOAPValidationServiceClient implements ValidationService {
    @Override
    public Observable<String> validateDocument(UploadedFile file) throws IOException {
        return null;
    }
}
