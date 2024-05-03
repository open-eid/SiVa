/*
 * Copyright 2018 - 2024 Riigi Infosüsteemi Amet
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
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.exception.MalformedSignatureFileException;
import ee.openeid.siva.validation.security.SecureSAXParsers;
import ee.openeid.validation.service.generic.SignatureXmlHandler;
import ee.openeid.validation.service.generic.HashcodeGenericValidationService;
import ee.openeid.validation.service.timemark.TimemarkHashcodeValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.xml.parsers.SAXParser;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Service
public class HashcodeValidationProxy extends ValidationProxy {
    private final HasBdocTimemarkPolicyService hasBdocTimemarkPolicyService;
    private final HashcodeValidationMapper hashcodeValidationMapper;
    private final HashcodeGenericValidationService hashcodeGenericValidationService;
    private final TimemarkHashcodeValidationService timemarkHashcodeValidationService;

    @Autowired
    public HashcodeValidationProxy(StatisticsService statisticsService,
                                   ApplicationContext applicationContext,
                                   Environment environment,
                                   HasBdocTimemarkPolicyService hasBdocTimemarkPolicyService,
                                   HashcodeValidationMapper hashcodeValidationMapper,
                                   HashcodeGenericValidationService hashcodeGenericValidationService,
                                   TimemarkHashcodeValidationService timemarkHashcodeValidationService) {
        super(statisticsService, applicationContext, environment);
        this.hasBdocTimemarkPolicyService = hasBdocTimemarkPolicyService;
        this.hashcodeValidationMapper = hashcodeValidationMapper;
        this.hashcodeGenericValidationService = hashcodeGenericValidationService;
        this.timemarkHashcodeValidationService = timemarkHashcodeValidationService;
    }

    @Override
    String constructValidatorName(ProxyRequest proxyRequest) {
        throw new IllegalStateException("Method is unimplemented");
    }

    @Override
    public SimpleReport validateRequest(ProxyRequest proxyRequest) {
        if (proxyRequest instanceof ProxyHashcodeDataSet) {
            var reports = hashcodeValidationMapper.mapToValidationDocuments((ProxyHashcodeDataSet) proxyRequest)
              .stream()
              .map(this::validateDocument)
              .toList();
            return chooseReport(hashcodeValidationMapper.mergeReportsToOne(reports), ReportType.SIMPLE);
        }
        throw new IllegalStateException("Something went wrong with hashcode validation");
    }

    private Reports validateDocument(ValidationDocument validationDocument) {
        Optional.ofNullable(getDataFileInfoIfNeeded(validationDocument))
          .filter(dataFiles -> !dataFiles.isEmpty())
          .ifPresent(validationDocument::setDatafiles);
        if (hasBdocTimemarkPolicyService.hasBdocTimemarkPolicy(validationDocument)) {
            return timemarkHashcodeValidationService.validateDocument(validationDocument);
        } else {
            return hashcodeGenericValidationService.validateDocument(validationDocument);
        }
    }

    private List<Datafile> getDataFileInfoIfNeeded(ValidationDocument validationDocument) {
        if (!CollectionUtils.isEmpty(validationDocument.getDatafiles())) {
            return null;
        } else {
            try {
                SAXParser saxParser = SecureSAXParsers.createParser();
                SignatureXmlHandler handler = new SignatureXmlHandler();
                saxParser.parse(new ByteArrayInputStream(validationDocument.getBytes()), handler);
                return handler.getDatafiles();
            } catch (Exception e) {
                throw constructMalformedDocumentException(new RuntimeException(e));
            }
        }
    }

    private RuntimeException constructMalformedDocumentException(Exception cause) {
        return new MalformedSignatureFileException(cause, "Signature file malformed");
    }
}
