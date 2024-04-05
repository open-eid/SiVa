/*
 * Copyright 2022 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator.report;

import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlSignature;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DssSimpleReportWrapper {

    private final @NonNull SimpleReport dssSimpleReport;

    public DssSimpleReportWrapper(@NonNull Reports dssReports) {
        this(dssReports.getSimpleReport());
    }

    public XmlSimpleReport getXmlSimpleReport() {
        return getDssSimpleReport().getJaxbModel();
    }

    public XmlSignature getXmlSignature(String signatureId) {
        final List<XmlToken> xmlTokens = getXmlSimpleReport().getSignatureOrTimestampOrEvidenceRecord();

        if (CollectionUtils.isNotEmpty(xmlTokens)) {
            for (XmlToken xmlToken : xmlTokens) {
                if (StringUtils.equals(signatureId, xmlToken.getId()) && xmlToken instanceof XmlSignature) {
                    return (XmlSignature) xmlToken;
                }
            }
        }

        return null;
    }

    public XmlDetails getSignatureAdESValidationXmlDetails(String signatureId) {
        final XmlSignature xmlSignature = getXmlSignature(signatureId);

        if (xmlSignature == null) {
            return null;
        }

        XmlDetails xmlAdESValidationDetails = xmlSignature.getAdESValidationDetails();

        if (xmlAdESValidationDetails == null) {
            xmlAdESValidationDetails = new XmlDetails();
            xmlSignature.setAdESValidationDetails(xmlAdESValidationDetails);
        }

        return xmlAdESValidationDetails;
    }

    public XmlDetails getSignatureQualificationXmlDetails(String signatureId) {
        final XmlSignature xmlSignature = getXmlSignature(signatureId);

        if (xmlSignature == null) {
            return null;
        }

        XmlDetails xmlQualificationDetails = xmlSignature.getQualificationDetails();

        if (xmlQualificationDetails == null) {
            xmlQualificationDetails = new XmlDetails();
            xmlSignature.setQualificationDetails(xmlQualificationDetails);
        }

        return xmlQualificationDetails;
    }

    public static XmlMessage createXmlMessage(String messageKey, String messageValue) {
        final XmlMessage xmlMessage = new XmlMessage();
        xmlMessage.setKey(messageKey);
        xmlMessage.setValue(messageValue);
        return xmlMessage;
    }

    public static XmlMessage createXmlMessage(String message) {
        return createXmlMessage(null, message);
    }

}
