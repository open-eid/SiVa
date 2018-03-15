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

package ee.openeid.siva.validation.service;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestDocument;

public interface ValidationService {

    default Reports validateDocument(ValidationDocument validationDocument) {
        throw new NotImplementedException("Not implemented");
    }

    default Reports validateDocuments(ValidationDocument validationDocument, List<DigestDocument> digestDocuments, DSSDocument signatureDocument) throws DSSException {
        throw new NotImplementedException("Not implemented");
    }

}
