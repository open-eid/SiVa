/*
 * Copyright 2019 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.sample.siva;


import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;
import java.util.Collections;

@Service(value = SivaServiceType.JSON_HASHCODE_SERVICE)
public class SivaJSONHashcodeValidationServiceClient implements HashcodeValidationService {

    private static final int GENERIC_ERROR_CODE = 101;

    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;
    private SivaValidationServiceErrorHandler errorHandler;

    @Override
    public Observable<String> validateDocument(String policy, String report, UploadedFile file) throws IOException {
        HashcodeValidationRequest validationRequest = new HashcodeValidationRequest();
        final String base64EncodedFile = file.getEncodedFile();
        if (StringUtils.isNotBlank(policy))
            validationRequest.setSignaturePolicy(policy);
        if (StringUtils.isNotBlank(report))
            validationRequest.setReportType(report);
        SignatureFile signatureFile = new SignatureFile();
        signatureFile.setSignature(base64EncodedFile);
        validationRequest.setSignatureFiles(Collections.singletonList(signatureFile));

        try {
            restTemplate.setErrorHandler(errorHandler);
            String fullUrl = properties.getServiceHost() + properties.getJsonHashcodeServicePath();
            return Observable.just(restTemplate.postForObject(fullUrl, validationRequest, String.class));
        } catch (ResourceAccessException ce) {
            String errorMessage = "Connection to web service failed. Make sure You have configured SiVa web service correctly";
            return Observable.just(new ObjectMapper().writer().writeValueAsString(new ServiceError(GENERIC_ERROR_CODE, errorMessage)));
        }
    }

    @Autowired
    public void setProperties(SivaRESTWebServiceConfigurationProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setErrorHandler(final SivaValidationServiceErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
