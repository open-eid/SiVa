/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.validation.service.pdf.validator;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;

import java.io.InputStream;

public class EstonianPDFDocumentValidator extends PDFDocumentValidator {

    public EstonianPDFDocumentValidator() {
        super(null);
    }

    public EstonianPDFDocumentValidator(DSSDocument dssDocument) {
        super(dssDocument);
    }

    @Override
    public Reports validateDocument(final String policyResourcePath) {
        if (policyResourcePath == null) {
            return validateDocument((InputStream) null);
        }

        return validateDocument(getClass().getResourceAsStream(policyResourcePath));
    }

}
