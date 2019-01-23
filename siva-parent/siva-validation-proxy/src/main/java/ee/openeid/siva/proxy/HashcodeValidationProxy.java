/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.document.SignatureFile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.generic.HashcodeGenericValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HashcodeValidationProxy extends ValidationProxy {

    private static final String HASHCODE_GENERIC_SERVICE = "hashcodeGeneric";

    @Override
    String constructValidatorName(ProxyRequest proxyRequest) {
        return HASHCODE_GENERIC_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
    }

    @Override
    public SimpleReport validateRequest(ProxyRequest proxyRequest) {
        ValidationService validationService = getServiceForType(proxyRequest);
        if (validationService instanceof HashcodeGenericValidationService && proxyRequest instanceof ProxyHashcodeDataSet) {

            List<ValidationDocument> validationDocuments = ((ProxyHashcodeDataSet) proxyRequest).getSignatureFiles()
                    .stream()
                    .map(signatureFile -> createValidationDocument(proxyRequest.getSignaturePolicy(), signatureFile))
                    .collect(Collectors.toList());
            Reports reports =  ((HashcodeGenericValidationService) validationService).validate(validationDocuments);
            return chooseReport(reports, ReportType.SIMPLE);
        }
        throw new IllegalStateException("Something went wrong with hashcode validation");
    }

    ValidationDocument createValidationDocument(String signaturePolicy, SignatureFile signatureFile) {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setSignaturePolicy(signaturePolicy);
        validationDocument.setBytes(signatureFile.getSignature());
        validationDocument.setDatafiles(signatureFile.getDatafiles());
        return validationDocument;
    }
}
