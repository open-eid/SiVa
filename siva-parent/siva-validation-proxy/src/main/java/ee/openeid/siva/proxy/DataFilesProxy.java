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

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.DataFilesServiceNotFoundException;
import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.validation.service.DataFilesService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DataFilesProxy {

    private static final String SERVICE_BEAN_NAME_POSTFIX = "DataFilesService";
    private ApplicationContext applicationContext;

    public DataFilesReport getDataFiles(ProxyDocument proxyDocument) {
        return getServiceForType(extractDocumentType(proxyDocument.getName())).getDataFiles(createDataFilesDocument(proxyDocument));
    }

    private static DocumentType extractDocumentType(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        return DocumentType.documentTypeFromString(extension.toUpperCase());
    }

    private static String constructDataFilesServiceName(DocumentType documentType) {
        return documentType.name() + SERVICE_BEAN_NAME_POSTFIX;
    }

    private DataFilesDocument createDataFilesDocument(ProxyDocument proxyDocument) {
        DataFilesDocument dataFilesDocument = new DataFilesDocument();
        dataFilesDocument.setBytes(proxyDocument.getBytes());
        return dataFilesDocument;
    }

    private DataFilesService getServiceForType(DocumentType documentType) {
        String dataFilesServiceName = constructDataFilesServiceName(documentType);
        try {
            return (DataFilesService) applicationContext.getBean(dataFilesServiceName);
        } catch (NoSuchBeanDefinitionException e) {
            throw new DataFilesServiceNotFoundException(dataFilesServiceName + " not found");
        }
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
