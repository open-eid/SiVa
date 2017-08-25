/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.proxy.http.RESTProxyService;
import ee.openeid.siva.statistics.StatisticsService;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.validation.service.timestamptoken.TimeStampTokenValidationService;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.InMemoryDocument;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ValidationProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationProxy.class);
    private static final String SERVICE_BEAN_NAME_POSTFIX = "ValidationService";
    private static final String GENERIC_SERVICE = "generic";
    private static final String ASICS_EXTENSION = "ASICS";
    private static final String SCS_FILE_TYPE = "SCS";
    private static final String ZIP_FILE_TYPE = "ZIP";
    private static final String TIMESTAMP_EXTENSION = ".TST";
    private static final String TIMESTAMP_TOKEN_SERVICE = "timeStampToken";
    private static final String MIME_TYPE_FILE_NAME = "mimetype";
    private static final String ASICS_MIME_TYPE = "application/vnd.etsi.asic-s+zip";
    private static final String META_INF_FOLDER = "META-INF/";
    private static final String DOCUMENT_FORMAT_NOT_RECOGNIZED = "Document format not recognized/handled";
    private RESTProxyService restProxyService;
    private StatisticsService statisticsService;
    private ApplicationContext applicationContext;


    public QualifiedReport validate(ProxyDocument proxyDocument) {
        long validationStartTime = System.nanoTime();
        QualifiedReport report;
        if (proxyDocument.getDocumentType() != null && proxyDocument.getDocumentType() == DocumentType.XROAD) {
            report = restProxyService.validate(createValidationDocument(proxyDocument));
        } else {
            ValidationService validationService = getServiceForType(proxyDocument);
            report = validationService.validateDocument(createValidationDocument(proxyDocument));
            if (validationService instanceof TimeStampTokenValidationService && TimeStampTokenValidationData.Indication.TOTAL_PASSED == report.getTimeStampTokens().get(0).getIndication()) {
                ProxyDocument dataFileProxyDocument = generateDataFileProxyDocument(proxyDocument);
                ValidationService dataFileValidationService = getServiceForType(dataFileProxyDocument);
                QualifiedReport dataFileReport = null;
                try {
                    dataFileReport = dataFileValidationService.validateDocument(createValidationDocument(dataFileProxyDocument));
                } catch (DSSException e) {
                    if (!DOCUMENT_FORMAT_NOT_RECOGNIZED.equalsIgnoreCase(e.getMessage())) {
                        throw e;
                    }
                }
                report = mergeReports(report, dataFileReport);
            }
        }
        statisticsService.publishValidationStatistic(System.nanoTime() - validationStartTime, report);
        return report;
    }

    private QualifiedReport mergeReports(QualifiedReport timeStampTokenReport, QualifiedReport dataFileReport) {
        if (dataFileReport != null) {
            dataFileReport.setTimeStampTokens(timeStampTokenReport.getTimeStampTokens());
            return dataFileReport;
        }
        return timeStampTokenReport;
    }

    private ProxyDocument generateDataFileProxyDocument(ProxyDocument proxyDocument) {
        ProxyDocument dataFileDocument = new ProxyDocument();
        InMemoryDocument inMemoryDocument = getDataFile(proxyDocument.getBytes());
        dataFileDocument.setName(inMemoryDocument.getName());
        dataFileDocument.setSignaturePolicy(proxyDocument.getSignaturePolicy());
        dataFileDocument.setBytes(inMemoryDocument.getBytes());
        return dataFileDocument;
    }

    private InMemoryDocument getDataFile(byte[] proxyDocument) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(proxyDocument))) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (!entry.getName().startsWith(META_INF_FOLDER) && !entry.getName().equalsIgnoreCase(MIME_TYPE_FILE_NAME)) {
                    return new InMemoryDocument(zipStream, entry.getName());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Invalid document");
    }

    private String constructValidatorName(ProxyDocument proxyDocument) {
        String fileName = proxyDocument.getName();
        String extension = FilenameUtils.getExtension(fileName).toUpperCase();
        if (!StringUtils.isNotBlank(extension)) {
            throw new IllegalArgumentException("Invalid file format:" + fileName);
        }
        if (DocumentType.DDOC.name().equals(extension) || DocumentType.BDOC.name().equals(extension)) {
            return extension + SERVICE_BEAN_NAME_POSTFIX;
        } else if (extension.equals(ASICS_EXTENSION) || extension.equals(SCS_FILE_TYPE) || extension.equals(ZIP_FILE_TYPE)) {
            return decideAsicsValidatorService(proxyDocument.getBytes(), extension);
        }
        return GENERIC_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
    }

    private String decideAsicsValidatorService(byte[] document, String extension) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(document))) {
            ZipEntry entry;
            boolean isAsicsMimeType = false;
            boolean isTimeStampExtension = false;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (isAsicsMimeType(entry, zipStream)) {
                    isAsicsMimeType = true;
                } else if (entry.getName().toUpperCase().endsWith(TIMESTAMP_EXTENSION)) {
                    isTimeStampExtension = true;
                }
            }

            if (extension.equals(ZIP_FILE_TYPE)) {
                if (isAsicsMimeType && isTimeStampExtension) {
                    return TIMESTAMP_TOKEN_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
                }
            } else {
                if (isTimeStampExtension) {
                    return TIMESTAMP_TOKEN_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return GENERIC_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
    }

    private boolean isAsicsMimeType(ZipEntry entry, ZipInputStream zipStream) {
        return entry.getName().equals(MIME_TYPE_FILE_NAME) && ASICS_MIME_TYPE.equals(new String(new InMemoryDocument(zipStream, entry.getName()).getBytes()));
    }

    private ValidationService getServiceForType(ProxyDocument proxyDocument) {
        String validatorName = constructValidatorName(proxyDocument);
        try {
            return (ValidationService) applicationContext.getBean(validatorName);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.error("{} not found", validatorName, e);
            throw new ValidatonServiceNotFoundException(validatorName + " not found");
        }
    }

    private ValidationDocument createValidationDocument(ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName(proxyDocument.getName());
        validationDocument.setBytes(proxyDocument.getBytes());
        validationDocument.setSignaturePolicy(proxyDocument.getSignaturePolicy());
        return validationDocument;
    }

    @Autowired
    public void setRestProxyService(RESTProxyService restProxyService) {
        this.restProxyService = restProxyService;
    }

    @Autowired
    public void setStatisticsService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
