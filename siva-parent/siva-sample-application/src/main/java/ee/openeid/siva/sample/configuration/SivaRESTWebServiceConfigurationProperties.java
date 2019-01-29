/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.sample.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "siva.service")
public class SivaRESTWebServiceConfigurationProperties {
    private static final String DEFAULT_SERVICE_URL = "http://localhost:8080";
    private String jsonServicePath = "/validate";
    private String jsonHashcodeServicePath = "/validateHashcode";
    private String jsonDataFilesServicePath = "/getDataFiles";
    private String soapDataFilesServicePath = "/soap/dataFilesWebService/getDocumentDataFiles";
    private String soapServicePath = "/soap/validationWebService/validateDocument";
    private String soapHashcodeServicePath = "/soap/hashcodeValidationWebService/hashcodeValidationDocument";
    private String serviceHost = DEFAULT_SERVICE_URL;
}
